import numpy as np
import pandas as pd


def _rsi(series: pd.Series, period: int = 14) -> pd.Series:
    delta = series.diff()
    gain = delta.clip(lower=0).rolling(period).mean()
    loss = (-delta.clip(upper=0)).rolling(period).mean()
    rs = gain / loss.replace(0, np.nan)
    return 100 - (100 / (1 + rs))


def build_features(df: pd.DataFrame) -> pd.DataFrame:
    """Compute technical features from OHLCV dataframe."""
    out = df.copy()
    out["return_1"] = out["close"].pct_change(1)
    out["return_5"] = out["close"].pct_change(5)
    out["return_15"] = out["close"].pct_change(15)
    out["volatility_10"] = out["return_1"].rolling(10).std()
    out["ema_9"] = out["close"].ewm(span=9, adjust=False).mean()
    out["ema_21"] = out["close"].ewm(span=21, adjust=False).mean()
    out["ema_ratio"] = out["ema_9"] / out["ema_21"] - 1
    out["rsi_14"] = _rsi(out["close"], 14)
    out["volume_ma_20"] = out["volume"].rolling(20).mean()
    out["rel_volume"] = out["volume"] / out["volume_ma_20"].replace(0, np.nan)
    return out


FEATURE_COLUMNS = [
    "return_1", "return_5", "return_15",
    "volatility_10", "ema_ratio", "rsi_14", "rel_volume",
]


def candles_to_df(candles) -> pd.DataFrame:
    rows = [
        {
            "open_time": c.open_time,
            "open": c.open,
            "high": c.high,
            "low": c.low,
            "close": c.close,
            "volume": c.volume,
        }
        for c in candles
    ]
    return pd.DataFrame(rows)
