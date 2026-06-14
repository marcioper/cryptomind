from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    database_url: str = "postgresql://postgres:pass1234@localhost:5432/cryptomind"
    binance_klines_base_url: str = "https://api.binance.com"
    default_symbol: str = "BTCUSDT"
    default_intervals: str = "1m,5m,1h"
    backfill_days: int = 90
    artifacts_dir: str = "artifacts"
    fee_rate: float = 0.001
    trade_size_usdt: float = 50.0
    forward_candles: int = 3
    min_profit_threshold: float = 0.002

    class Config:
        env_file = ".env"


settings = Settings()
