import numpy as np

from app.services.features import FEATURE_COLUMNS, build_features, candles_to_df
from app.services.model_training import load_model


def predict_signal(candles, symbol: str = "BTCUSDT", interval: str = "5m") -> dict:
    model, meta = load_model()
    df = candles_to_df(candles)
    feat = build_features(df)
    latest = feat[FEATURE_COLUMNS].dropna()

    if model is None or len(latest) == 0:
        return _fallback_signal(feat, symbol, interval, reason="no_model")

    row = latest.iloc[[-1]]
    pred = model.predict(row[FEATURE_COLUMNS])[0]
    proba = None
    confidence = 0.5
    if hasattr(model, "predict_proba"):
        proba_arr = model.predict_proba(row[FEATURE_COLUMNS])[0]
        classes = list(model.classes_)
        if pred in classes:
            confidence = float(proba_arr[classes.index(pred)])
        proba = dict(zip(classes, [round(float(p), 4) for p in proba_arr]))

    return {
        "signal": pred,
        "confidence": round(confidence, 4),
        "symbol": symbol,
        "interval": interval,
        "model_version": meta.get("version"),
        "probabilities": proba,
        "reason": f"ML inference ({meta.get('version', 'unknown')})",
    }


def _fallback_signal(feat, symbol: str, interval: str, reason: str) -> dict:
    """Rule-based fallback when no model is trained."""
    if len(feat) < 22:
        return {"signal": "HOLD", "confidence": 0.0, "symbol": symbol, "interval": interval, "reason": reason}

    last = feat.iloc[-1]
    signal = "HOLD"
    if last.get("rsi_14", 50) < 35 and last.get("ema_ratio", 0) > 0:
        signal = "BUY"
    elif last.get("rsi_14", 50) > 65 and last.get("ema_ratio", 0) < 0:
        signal = "SELL"

    return {
        "signal": signal,
        "confidence": 0.55 if signal != "HOLD" else 0.3,
        "symbol": symbol,
        "interval": interval,
        "model_version": None,
        "reason": f"fallback_rules ({reason})",
    }
