from typing import List, Optional

from pydantic import BaseModel

class Asset(BaseModel):
    assetSymbol: str
    quantity: float

class PortfolioRequest(BaseModel):
    userId: int
    assets: List[Asset]
    symbol: Optional[str] = "BTCUSDT"
    interval: Optional[str] = "5m"

class RecommendationResponse(BaseModel):
    signal: str
    confidence: Optional[float] = None
    reason: Optional[str] = None
    interval: Optional[str] = None
    model_version: Optional[str] = None

class BacktestRequest(BaseModel):
    symbol: str = "BTCUSDT"
    interval: str = "5m"
    days: int = 90
    trade_size_usdt: float = 50.0

class CandleResponse(BaseModel):
    symbol: str
    interval: str
    open_time: int
    open: float
    high: float
    low: float
    close: float
    volume: float
