import akshare as ak
import baostock as bs
import tushare as ts
import pandas as pd
import argparse
import time
from datetime import datetime, timedelta, time as dtime
from sqlalchemy import create_engine, text
from sqlalchemy.types import Date, Numeric, BigInteger, String
from concurrent.futures import ThreadPoolExecutor

# 数据库连接配置
DB_URL = "postgresql://postgres:password@localhost:5432/zenith_flow"

# Tushare Token (如果你有，请填入这里；留空则跳过 Tushare)
TUSHARE_TOKEN = ""

# 全局字典，缓存每只股票的最新日期
# 格式: {'000001': '20251126', ...}
LATEST_DATES_MAP = {}

def format_date(date_str, format_type="dash"):
    """
    格式化日期
    :param format_type: "dash" -> "2023-01-01", "compact" -> "20230101"
    """
    dt = datetime.strptime(date_str, "%Y%m%d")
    if format_type == "dash":
        return dt.strftime("%Y-%m-%d")
    else:
        return dt.strftime("%Y%m%d")

def get_all_latest_dates(symbols):
    """
    批量预取所有股票的最新日期
    """
    if not symbols:
        return {}
        
    print("🔍 正在预检数据库中已有数据...")
    engine = create_engine(DB_URL)
    
    # 如果 symbol 太多 (>1000)，建议分批查询，这里简单起见一次性查
    # 构建 IN Clause: '000001', '000002', ...
    symbols_str = ",".join([f"'{s}'" for s in symbols])
    sql = text(f"SELECT symbol, max(date) FROM market_data_bar WHERE symbol IN ({symbols_str}) GROUP BY symbol")
    
    result_map = {}
    try:
        with engine.connect() as conn:
            rows = conn.execute(sql).fetchall()
            for row in rows:
                symbol, max_date = row
                if max_date:
                    result_map[symbol] = max_date.strftime("%Y%m%d")
    except Exception as e:
        print(f"❌ 预检失败: {e}")
    
    return result_map

def get_next_date(date_str):
    """
    给定日期 '20230101'，返回下一天 '20230102'
    """
    dt = datetime.strptime(date_str, "%Y%m%d")
    next_dt = dt + timedelta(days=1)
    return next_dt.strftime("%Y%m%d")

def get_target_end_date(user_end_date):
    """
    动态计算目标结束日期
    如果 user_end_date 是今天，根据收盘时间判断是否应该抓取今天
    """
    now = datetime.now()
    today_str = now.strftime("%Y%m%d")
    
    # 如果用户指定的结束日期早于今天，直接用用户指定的
    if user_end_date < today_str:
        return user_end_date
        
    # 如果用户指定的是今天(或未来)，需要判断是否收盘
    # A股收盘时间通常是 15:00，数据源更新可能延迟，这里设为 15:30 比较稳妥
    market_close_time = dtime(15, 30)
    
    if now.time() < market_close_time:
        # 还没收盘/更新，只能抓到昨天
        target_date = now - timedelta(days=1)
        return target_date.strftime("%Y%m%d")
    else:
        # 已收盘，抓到今天
        return today_str

def fetch_akshare(symbol, start_date, end_date):
    # print(f"[{symbol}] 尝试使用 AkShare 获取数据...")
    
    # AkShare 重试逻辑 (3次)
    max_retries = 3
    for i in range(max_retries):
        try:
            df = ak.stock_zh_a_hist(symbol=symbol, period="daily", start_date=start_date, end_date=end_date, adjust="qfq")
            
            if df.empty:
                # 空数据不一定是错，可能是停牌或假期，但这里为了简化视为空
                # 如果确实是空数据，返回空 DataFrame 交给上层处理
                return df
            
            df.rename(columns={
                '日期': 'date',
                '开盘': 'open',
                '收盘': 'close',
                '最高': 'high',
                '最低': 'low',
                '成交量': 'volume',
                '成交额': 'amount'
            }, inplace=True)
            return df
            
        except Exception as e:
            if i < max_retries - 1:
                # print(f"[{symbol}] AkShare 第 {i+1} 次重试...")
                time.sleep(2)
            else:
                raise e # 3次都失败，抛出异常触发降级

def fetch_baostock(symbol, start_date, end_date):
    # print(f"[{symbol}] 尝试使用 Baostock 获取数据...")
    
    # 登录系统
    lg = bs.login()
    if lg.error_code != '0':
        raise Exception(f"Baostock 登录失败: {lg.error_msg}")

    # Baostock 需要 "sz.000001" 格式
    prefix = "sh" if symbol.startswith("6") else "sz"
    bs_symbol = f"{prefix}.{symbol}"
    
    # 日期格式需要 "YYYY-MM-DD"
    start_dash = format_date(start_date, "dash")
    end_dash = format_date(end_date, "dash")

    # 获取沪深A股历史K线数据
    rs = bs.query_history_k_data_plus(bs_symbol,
        "date,open,high,low,close,volume,amount",
        start_date=start_dash, end_date=end_dash,
        frequency="d", adjustflag="2") # adjustflag="2" 为前复权
    
    if rs.error_code != '0':
        bs.logout()
        raise Exception(f"Baostock 查询失败: {rs.error_msg}")

    data_list = []
    while (rs.error_code == '0') & rs.next():
        data_list.append(rs.get_row_data())
    
    bs.logout()
    
    if not data_list:
        # 返回空 DataFrame 而不是抛异常
        return pd.DataFrame()

    df = pd.DataFrame(data_list, columns=rs.fields)
    
    # 类型转换 (Baostock 返回的全是 string)
    df['open'] = df['open'].astype(float)
    df['high'] = df['high'].astype(float)
    df['low'] = df['low'].astype(float)
    df['close'] = df['close'].astype(float)
    df['volume'] = df['volume'].replace('', 0).astype(float).astype(int) # Volume sometimes has decimals or empty
    df['amount'] = df['amount'].replace('', 0).astype(float)
    
    return df

def fetch_tushare(symbol, start_date, end_date):
    # print(f"[{symbol}] 尝试使用 Tushare 获取数据...")
    
    if not TUSHARE_TOKEN:
        raise Exception("Tushare Token 未配置，跳过")

    ts.set_token(TUSHARE_TOKEN)
    pro = ts.pro_api()
    
    # Tushare 需要 "000001.SZ" 格式
    prefix = "SH" if symbol.startswith("6") else "SZ"
    ts_symbol = f"{symbol}.{prefix}"
    
    # Tushare 日期格式 "YYYYMMDD"
    df = pro.daily(ts_code=ts_symbol, start_date=start_date, end_date=end_date)
    
    # 复权因子 (Tushare daily 默认是不复权的，需要额外调 adj_factor 接口计算，这里简化直接用 daily)
    # 注意: 严格来说应该用 pro.daily + pro.adj_factor 计算前复权，这里作为 Plan C 暂且接受不复权或假设是前复权
    # 实际上 pro.daily 只是不复权行情。如果需要复权，通常用 tushare.pro_bar(adj='qfq') 接口
    
    # 使用 tushare 的通用行情接口来获取复权数据 (依赖 tushare 包版本)
    df = ts.pro_bar(ts_code=ts_symbol, adj='qfq', start_date=start_date, end_date=end_date)
    
    if df is None or df.empty:
        # 返回空 DataFrame
        return pd.DataFrame()

    # 统一列名
    df.rename(columns={
        'trade_date': 'date',
        'vol': 'volume'
    }, inplace=True)
    
    # amount 单位转换: Tushare 'amount' 是千元，我们需要元
    df['amount'] = df['amount'] * 1000
    
    return df

def save_to_db(df, symbol):
    if df.empty:
        return

    # 1. 获取这次数据的起止日期
    min_date = df['date'].min()
    max_date = df['date'].max()
    
    engine = create_engine(DB_URL)
    
    # 2. 删除该区间内的旧数据 (防止主键冲突)
    # 使用 text() 包裹 SQL 语句以兼容 SQLAlchemy 2.0+
    delete_sql = text(f"DELETE FROM market_data_bar WHERE symbol = '{symbol}' AND date >= '{min_date}' AND date <= '{max_date}'")
    with engine.connect() as conn:
        conn.execute(delete_sql)
        conn.commit()

    # 添加 symbol 列
    df['symbol'] = symbol
    
    # 只保留需要的列
    columns = ['symbol', 'date', 'open', 'high', 'low', 'close', 'volume', 'amount']
    df = df[columns]

    # 3. 写入数据库
    df.to_sql('market_data_bar', engine, if_exists='append', index=False, dtype={
        'symbol': String(20),
        'date': Date,
        'open': Numeric(12, 4),
        'high': Numeric(12, 4),
        'low': Numeric(12, 4),
        'close': Numeric(12, 4),
        'volume': BigInteger,
        'amount': Numeric(18, 4)
    })
    # print(f"[{symbol}] 成功写入 {len(df)} 条数据")

def fetch_with_fallback(symbol, start, end):
    """
    多源自动降级抓取策略 (带智能增量检查)
    """
    
    # 1. 动态计算本次抓取的实际结束日期 (处理收盘问题)
    target_end_date = get_target_end_date(end)
    
    # 2. 智能增量检查 (使用内存缓存)
    latest_date = LATEST_DATES_MAP.get(symbol)
    actual_start = start
    
    if latest_date:
        # 比如最新是 20251126，则从 20251127 开始抓
        next_day = get_next_date(latest_date)
        if next_day > target_end_date:
            # print(f"⏩ [{symbol}] 数据已最新 ({latest_date})，跳过")
            return
        actual_start = next_day
        print(f"🔄 [{symbol}] 增量更新: {actual_start} -> {target_end_date}")
    else:
        print(f"📥 [{symbol}] 全量抓取: {start} -> {target_end_date}")

    # 3. 优先尝试 AkShare (带重试)
    try:
        df = fetch_akshare(symbol, actual_start, target_end_date)
        if df is not None and not df.empty:
            save_to_db(df, symbol)
            print(f"✅ [{symbol}] AkShare 成功")
            return
        elif df is not None and df.empty:
             print(f"⚪ [{symbol}] 无新数据 (AkShare)")
             return
    except Exception:
        pass
        # print(f"⚠️ [{symbol}] AkShare 失败: {e}")
    
    # 4. 降级尝试 Baostock
    try:
        # print(f"🔄 [{symbol}] 降级切换至 Baostock 源...")
        df = fetch_baostock(symbol, actual_start, target_end_date)
        if df is not None and not df.empty:
            save_to_db(df, symbol)
            print(f"✅ [{symbol}] Baostock 成功")
            return
    except Exception:
        pass
        # print(f"⚠️ [{symbol}] Baostock 失败: {e}")
        
    # 5. 再次降级尝试 Tushare
    try:
        # print(f"🔄 [{symbol}] 降级切换至 Tushare 源...")
        df = fetch_tushare(symbol, actual_start, target_end_date)
        if df is not None and not df.empty:
            save_to_db(df, symbol)
            print(f"✅ [{symbol}] Tushare 成功")
            return
    except Exception:
        pass
        # print(f"⚠️ [{symbol}] Tushare 失败: {e}")
    
    print(f"❌ [{symbol}] 所有数据源均失败或无数据。")

def fetch_index_stocks(index_code, date=None):
    """
    获取指数成分股列表 (使用 Baostock)
    :param index_code: 指数代码 (e.g. sh.000300)
    """
    print(f"正在获取指数 {index_code} 的成分股...")
    lg = bs.login()
    
    if not date:
        # 默认用最近的交易日（简单起见用今天，如果是非交易日 baostock 会返回最近的列表）
        date = datetime.now().strftime("%Y-%m-%d")
        
    rs = bs.query_hs300_stocks(date=date) if '000300' in index_code else bs.query_zz500_stocks(date=date)
    
    stocks = []
    while (rs.error_code == '0') & rs.next():
        # baostock 返回格式 'sh.600000'，我们需要 '600000'
        full_code = rs.get_row_data()[1]
        code = full_code.split('.')[1]
        stocks.append(code)
        
    bs.logout()
    return stocks

def process_symbol(sym, start, end):
    fetch_with_fallback(sym, start, end)
    # 每个线程内部依然保持一点礼貌间隔，避免IP被封
    time.sleep(0.5)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Zenith Flow Data Fetcher (Smart Incremental & Batch Pre-check)')
    
    # 既可以传单个股票，也可以传指数
    parser.add_argument('--symbol', nargs='+', help='Stock symbols (e.g. 000001 600519)', default=[])
    parser.add_argument('--index', help='Index code (e.g. sh.000300 for HS300)', default='')
    
    default_end = datetime.now().strftime("%Y%m%d")
    parser.add_argument('--start', help='Start date (YYYYMMDD)', default='20200101')
    parser.add_argument('--end', help='End date (YYYYMMDD)', default=default_end)
    
    # 新增并发数参数
    parser.add_argument('--workers', type=int, default=5, help='Concurrent workers (default: 5)')
    
    args = parser.parse_args()
    
    target_symbols = []
    
    # 1. 如果指定了指数，先获取成分股
    if args.index:
        try:
            index_stocks = fetch_index_stocks(args.index)
            print(f"✅ 获取到指数 {args.index} 成分股共 {len(index_stocks)} 只")
            target_symbols.extend(index_stocks)
        except Exception as e:
            print(f"❌ 获取指数成分股失败: {e}")
            
    # 2. 加入手动指定的股票
    if args.symbol:
        target_symbols.extend(args.symbol)
        
    # 去重
    target_symbols = list(set(target_symbols))
    
    if not target_symbols:
        print("⚠️ 未指定任何股票或指数，默认抓取示例股票...")
        target_symbols = ['000429', '600519']
    
    print(f"🚀 准备处理 {len(target_symbols)} 只股票 (并发数: {args.workers})...")
    
    # 3. 批量预检数据库状态 (极速版)
    # 将预检结果存入全局变量 LATEST_DATES_MAP
    LATEST_DATES_MAP = get_all_latest_dates(target_symbols)
    print(f"📚 已预加载 {len(LATEST_DATES_MAP)} 只股票的最新日期缓存")
    
    start_time = time.time()
    
    # 使用线程池并发执行
    with ThreadPoolExecutor(max_workers=args.workers) as executor:
        futures = [executor.submit(process_symbol, sym, args.start, args.end) for sym in target_symbols]
        
        for future in futures:
            future.result()
    
    elapsed = time.time() - start_time
    print(f"\n✅ 所有任务完成。耗时: {elapsed:.2f}秒")
