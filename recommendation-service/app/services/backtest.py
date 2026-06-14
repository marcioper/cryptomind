from dataclasses import dataclass, asdict
from typing import Any

import numpy as np
import pandas as pd

from app.config import settings
from app.services.features import FEATURE_COLUMNS, build_features
from app.services.labeling import label_signals


@dataclass
class BacktestResult:
    total_trades: int
    win_rate: float
    profit_factor: float
    max_drawdown: float
    total_return_pct: float
    monthly_returns: list[dict]
    final_equity: float

    def to_dict(self) -> dict[str, Any]:
        return asdict(self)


def run_backtest(
    df: pd.DataFrame,
    signals: pd.Series,
    trade_size_usdt: float | None = None,
    fee_rate: float | None = None,
) -> BacktestResult:
    size = trade_size_usdt or settings.trade_size_usdt
    fee = fee_rate or settings.fee_rate

    equity = 1000.0
    peak = equity
    max_dd = 0.0
    wins = 0
    losses = 0
    gross_profit = 0.0
    gross_loss = 0.0
    trades = 0
    position = None  # {"entry_price", "qty", "entry_time"}
    monthly_pnl: dict[str, float] = {}

    feat_df = build_features(df)
    times = pd.to_datetime(df["open_time"], unit="ms")

    for i in range(len(df)):
        sig = signals.iloc[i] if i < len(signals) else "HOLD"
        price = df["close"].iloc[i]
        ts = times.iloc[i]
        month_key = ts.strftime("%Y-%m")

        if position is None and sig == "BUY":
            qty = size / price
            cost = size * (1 + fee)
            if equity >= cost:
                equity -= cost
                position = {"entry_price": price, "qty": qty, "entry_time": ts}
                trades += 1

        elif position is not None and sig == "SELL":
            proceeds = position["qty"] * price * (1 - fee)
            pnl = proceeds - (position["qty"] * position["entry_price"] * (1 + fee))
            equity += proceeds
            monthly_pnl[month_key] = monthly_pnl.get(month_key, 0) + pnl
            if pnl > 0:
                wins += 1
                gross_profit += pnl
            else:
                losses += 1
                gross_loss += abs(pnl)
            position = None
            trades += 1

        peak = max(peak, equity)
        dd = (peak - equity) / peak if peak > 0 else 0
        max_dd = max(max_dd, dd)

    monthly_returns = [
        {"month": m, "pnl_usdt": round(v, 2), "return_pct": round(v / 1000 * 100, 2)}
        for m, v in sorted(monthly_pnl.items())
    ]

    win_rate = wins / max(wins + losses, 1)
    profit_factor = gross_profit / max(gross_loss, 1e-9)
    total_return = (equity - 1000) / 1000 * 100

    return BacktestResult(
        total_trades=trades,
        win_rate=round(win_rate, 4),
        profit_factor=round(profit_factor, 4),
        max_drawdown=round(max_dd, 4),
        total_return_pct=round(total_return, 4),
        monthly_returns=monthly_returns,
        final_equity=round(equity, 2),
    )


def backtest_from_candles(candles, model=None) -> BacktestResult:
    from app.services.features import candles_to_df

    df = candles_to_df(candles)
    feat = build_features(df)
    labels = label_signals(feat)

    if model is not None:
        valid = feat[FEATURE_COLUMNS].dropna()
        if len(valid) == 0:
            signals = labels
        else:
            preds = model.predict(valid[FEATURE_COLUMNS])
            signals = pd.Series("HOLD", index=feat.index)
            signals.loc[valid.index] = preds
    else:
        signals = labels

    return run_backtest(feat.dropna(subset=FEATURE_COLUMNS), signals.loc[feat.dropna(subset=FEATURE_COLUMNS).index])
