import json
import os
from datetime import datetime, timezone

import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import TimeSeriesSplit

from app.config import settings
from app.services.features import FEATURE_COLUMNS, build_features, candles_to_df
from app.services.labeling import label_signals


def _artifacts_path() -> str:
    path = settings.artifacts_dir
    os.makedirs(path, exist_ok=True)
    return path


def train_model(candles) -> dict:
    df = candles_to_df(candles)
    feat = build_features(df)
    labels = label_signals(feat)
    data = feat[FEATURE_COLUMNS].copy()
    data["label"] = labels.values
    data = data.dropna()

    if len(data) < 200:
        raise ValueError(f"Insufficient data for training: {len(data)} rows (need >= 200)")

    X = data[FEATURE_COLUMNS]
    y = data["label"]

    clf = GradientBoostingClassifier(
        n_estimators=100,
        max_depth=4,
        learning_rate=0.05,
        random_state=42,
    )
    clf.fit(X, y)

    # Walk-forward validation score
    tscv = TimeSeriesSplit(n_splits=3)
    scores = []
    for train_idx, test_idx in tscv.split(X):
        clf_cv = GradientBoostingClassifier(n_estimators=50, max_depth=3, random_state=42)
        clf_cv.fit(X.iloc[train_idx], y.iloc[train_idx])
        scores.append(clf_cv.score(X.iloc[test_idx], y.iloc[test_idx]))

    version = datetime.now(timezone.utc).strftime("%Y%m%d%H%M%S")
    model_path = os.path.join(_artifacts_path(), f"model_{version}.joblib")
    meta_path = os.path.join(_artifacts_path(), f"model_{version}.json")

    joblib.dump(clf, model_path)

    metadata = {
        "version": version,
        "features": FEATURE_COLUMNS,
        "trained_at": datetime.now(timezone.utc).isoformat(),
        "samples": len(data),
        "cv_accuracy": round(float(np.mean(scores)), 4),
        "classes": list(clf.classes_),
    }
    with open(meta_path, "w") as f:
        json.dump(metadata, f, indent=2)

    # Symlink latest
    latest_model = os.path.join(_artifacts_path(), "model_latest.joblib")
    latest_meta = os.path.join(_artifacts_path(), "model_latest.json")
    if os.path.exists(latest_model):
        os.remove(latest_model)
    if os.path.exists(latest_meta):
        os.remove(latest_meta)
    os.symlink(os.path.basename(model_path), latest_model)
    os.symlink(os.path.basename(meta_path), latest_meta)

    return {"version": version, "model_path": model_path, **metadata}


def load_model():
    path = os.path.join(_artifacts_path(), "model_latest.joblib")
    if not os.path.exists(path):
        return None, None
    model = joblib.load(path)
    meta_path = os.path.join(_artifacts_path(), "model_latest.json")
    meta = {}
    if os.path.exists(meta_path):
        with open(meta_path) as f:
            meta = json.load(f)
    return model, meta
