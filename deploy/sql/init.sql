-- 创建普通的市场数据表
CREATE TABLE IF NOT EXISTS market_data_bar (
    id BIGSERIAL, -- 仍然保留自增ID用于唯一标识（非必需，但方便ORM）
    symbol VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    open DECIMAL(12, 4),
    high DECIMAL(12, 4),
    low DECIMAL(12, 4),
    close DECIMAL(12, 4),
    volume BIGINT,
    amount DECIMAL(18, 4),
    PRIMARY KEY (symbol, date) -- 使用联合主键
);

-- 将其转换为 Hypertable (TimescaleDB 特性)
-- 按照 'date' 列进行时间分片
SELECT create_hypertable('market_data_bar', 'date', if_not_exists => TRUE);

-- 创建索引加速查询
CREATE INDEX IF NOT EXISTS idx_bar_symbol_date ON market_data_bar (symbol, date DESC);

-- 添加市场数据表注释
COMMENT ON TABLE market_data_bar IS '股票日线行情数据表 (Hypertable)';
COMMENT ON COLUMN market_data_bar.symbol IS '股票代码';
COMMENT ON COLUMN market_data_bar.date IS '交易日期';
COMMENT ON COLUMN market_data_bar.open IS '开盘价';
COMMENT ON COLUMN market_data_bar.high IS '最高价';
COMMENT ON COLUMN market_data_bar.low IS '最低价';
COMMENT ON COLUMN market_data_bar.close IS '收盘价';
COMMENT ON COLUMN market_data_bar.volume IS '成交量 (手/股)';
COMMENT ON COLUMN market_data_bar.amount IS '成交额 (元)';


-- 1. 回测执行记录表
CREATE TABLE IF NOT EXISTS backtest_execution (
    id SERIAL PRIMARY KEY,
    strategy_name VARCHAR(50) NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    initial_cash DECIMAL(18, 4),
    final_assets DECIMAL(18, 4),
    total_return DECIMAL(10, 4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加回测主表注释
COMMENT ON TABLE backtest_execution IS '回测执行记录主表';
COMMENT ON COLUMN backtest_execution.id IS '主键 ID';
COMMENT ON COLUMN backtest_execution.strategy_name IS '策略名称 (例如: ai_v1, ma_dual)';
COMMENT ON COLUMN backtest_execution.symbol IS '回测标的代码';
COMMENT ON COLUMN backtest_execution.start_date IS '回测开始日期';
COMMENT ON COLUMN backtest_execution.end_date IS '回测结束日期';
COMMENT ON COLUMN backtest_execution.initial_cash IS '初始资金';
COMMENT ON COLUMN backtest_execution.final_assets IS '最终资产';
COMMENT ON COLUMN backtest_execution.total_return IS '总收益率 (0.15 = 15%)';
COMMENT ON COLUMN backtest_execution.created_at IS '创建时间';


-- 2. 回测每日详情表
CREATE TABLE IF NOT EXISTS backtest_daily_result (
    execution_id INT NOT NULL,
    date DATE NOT NULL,
    total_assets DECIMAL(18, 4),
    benchmark_value DECIMAL(18, 4),
    holdings_value DECIMAL(18, 4),
    cash DECIMAL(18, 4),
    daily_return DECIMAL(10, 4),
    PRIMARY KEY (execution_id, date)
);

-- 将每日详情表转换为 Hypertable (TimescaleDB 特性)
SELECT create_hypertable('backtest_daily_result', 'date', if_not_exists => TRUE);

-- 添加每日详情表注释
COMMENT ON TABLE backtest_daily_result IS '回测每日资金曲线详情表 (Hypertable)';
COMMENT ON COLUMN backtest_daily_result.execution_id IS '关联回测主表 ID';
COMMENT ON COLUMN backtest_daily_result.date IS '交易日期';
COMMENT ON COLUMN backtest_daily_result.total_assets IS '当日总资产 (现金+持仓)';
COMMENT ON COLUMN backtest_daily_result.benchmark_value IS '当日基准净值 (用于对比)';
COMMENT ON COLUMN backtest_daily_result.holdings_value IS '当日持仓市值';
COMMENT ON COLUMN backtest_daily_result.cash IS '当日可用现金';
COMMENT ON COLUMN backtest_daily_result.daily_return IS '当日收益率';

-- 3. AI 模型记录表
CREATE TABLE IF NOT EXISTS ai_model (
    id SERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL UNIQUE,
    algo_type VARCHAR(50),
    file_path VARCHAR(255) NOT NULL,
    features TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_model IS 'AI 模型元数据表';
COMMENT ON COLUMN ai_model.model_name IS '模型名称 (唯一)';
COMMENT ON COLUMN ai_model.algo_type IS '算法类型 (rf, lr, lgbm)';
COMMENT ON COLUMN ai_model.file_path IS '模型文件存储路径 (相对或绝对)';
COMMENT ON COLUMN ai_model.features IS '使用的特征列表 (JSON 或 CSV 字符串)';