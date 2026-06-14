import pandas as pd

from app.config import settings


def label_signals(df: pd.DataFrame, forward: int | None = None, threshold: float | None = None) -> pd.Series:
    """Classify forward return into BUY / SELL / HOLD after fee threshold."""
    fwd = forward or settings.forward_candles
    thr = threshold or settings.min_profit_threshold
    fee = settings.fee_rate * 2

    future_return = df["close"].shift(-fwd) / df["close"] - 1
    labels = pd.Series("HOLD", index=df.index)
    labels[future_return > thr + fee] = "BUY"
    labels[future_return < -(thr + fee)] = "SELL"
    return labels
