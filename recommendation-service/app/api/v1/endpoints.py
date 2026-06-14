from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.schemas import (
    BacktestRequest,
    CandleResponse,
    PortfolioRequest,
    RecommendationResponse,
)
from app.services import candle_store, model_training
from app.services.backtest import backtest_from_candles
from app.services.inference import predict_signal

router = APIRouter()


@router.get("/health")
def health():
    model, meta = model_training.load_model()
    return {
        "status": "Strategy Service OK",
        "model_loaded": model is not None,
        "model_version": meta.get("version") if meta else None,
    }


@router.get("/candles", response_model=list[CandleResponse])
def get_candles(
    symbol: str = "BTCUSDT",
    interval: str = "5m",
    limit: int = 100,
    db: Session = Depends(get_db),
):
    candles = candle_store.get_candles(db, symbol, interval, limit=limit)
    return [
        CandleResponse(
            symbol=c.symbol,
            interval=c.interval,
            open_time=c.open_time,
            open=c.open,
            high=c.high,
            low=c.low,
            close=c.close,
            volume=c.volume,
        )
        for c in candles
    ]


@router.post("/recommend", response_model=RecommendationResponse)
def recommend(data: PortfolioRequest, db: Session = Depends(get_db)):
    symbol = data.symbol or "BTCUSDT"
    interval = data.interval or "5m"
    candles = candle_store.get_candles(db, symbol, interval, limit=200)
    if len(candles) < 30:
        raise HTTPException(status_code=503, detail="Insufficient candle data")
    result = predict_signal(candles, symbol=symbol, interval=interval)
    return RecommendationResponse(**result)


@router.post("/backtest")
def backtest(req: BacktestRequest, db: Session = Depends(get_db)):
    candles = candle_store.get_candles(db, req.symbol, req.interval, limit=req.days * 288)
    if len(candles) < 100:
        raise HTTPException(status_code=400, detail="Not enough candles for backtest")
    model, _ = model_training.load_model()
    result = backtest_from_candles(candles, model=model)
    return result.to_dict()


@router.post("/train")
def train(db: Session = Depends(get_db)):
    candles = candle_store.get_candles(db, "BTCUSDT", "5m", limit=10000)
    if len(candles) < 500:
        raise HTTPException(status_code=400, detail="Need at least 500 candles to train")
    try:
        result = model_training.train_model(candles)
        return {"status": "ok", **result}
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
