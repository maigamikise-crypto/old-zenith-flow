import akshare as ak
import baostock as bs
import pandas as pd
from sqlalchemy import create_engine, text
from sqlalchemy.types import String
import time

# 数据库连接配置
DB_URL = "postgresql://postgres:password@localhost:5432/zenith_flow"

def init_db_and_fetch_names():
    engine = create_engine(DB_URL)
    
    with engine.connect() as conn:
        # 1. 创建 stock_info 表
        print("正在检查并创建 stock_info 表...")
        conn.execute(text("""
            CREATE TABLE IF NOT EXISTS stock_info (
                symbol VARCHAR(20) PRIMARY KEY,
                name VARCHAR(100),
                market VARCHAR(10)
            );
        """))
        
        # 2. 为 backtest_execution 添加 stock_name 列
        print("正在检查并更新 backtest_execution 表结构...")
        try:
            conn.execute(text("""
                ALTER TABLE backtest_execution ADD COLUMN IF NOT EXISTS stock_name VARCHAR(100);
            """))
        except Exception as e:
            # 忽略已存在列的错误
            pass
            
        conn.commit()
    
    df_result = None
    
    # 3. 尝试从 AkShare 获取
    print("尝试从 AkShare 获取全量股票列表...")
    try:
        # stock_zh_a_spot_em 返回实时行情，包含代码和名称
        # 增加 timeout
        df = ak.stock_zh_a_spot_em()
        
        if not df.empty:
            df = df[['代码', '名称']]
            df.rename(columns={'代码': 'symbol', '名称': 'name'}, inplace=True)
            df_result = df
            print("AkShare 获取成功。")
    except Exception as e:
        print(f"AkShare 获取失败 (跳过)")

    # 4. 如果 AkShare 失败，尝试 Baostock
    if df_result is None or df_result.empty:
        print("尝试从 Baostock 获取...")
        try:
            bs.login()
            rs = bs.query_stock_basic()
            
            data_list = []
            while (rs.error_code == '0') & rs.next():
                data_list.append(rs.get_row_data())
                    
            bs.logout()
            
            if data_list:
                df = pd.DataFrame(data_list, columns=rs.fields)
                df = df[['code', 'code_name']]
                # code format: sh.600000 -> 600000
                df['symbol'] = df['code'].apply(lambda x: x.split('.')[1])
                df['name'] = df['code_name']
                df = df[['symbol', 'name']]
                
                # 去除空名
                df = df[df['name'] != '']
                
                df_result = df
                print("Baostock 获取成功。")
        except Exception as e:
            print(f"Baostock 获取失败: {e}")

    if df_result is not None and not df_result.empty:
        # 去重
        df_result.drop_duplicates(subset=['symbol'], inplace=True)
        
        print(f"获取到 {len(df_result)} 只股票，正在写入数据库...")
        
        with engine.connect() as conn:
            conn.execute(text("TRUNCATE TABLE stock_info"))
            conn.commit()
            
        df_result.to_sql('stock_info', engine, if_exists='append', index=False, dtype={
            'symbol': String(20),
            'name': String(100)
        })
        
        print("股票名称库更新完成。")
    else:
        print("所有数据源均失败，无法更新股票名称。")

if __name__ == "__main__":
    init_db_and_fetch_names()
