import time
from typing import List

import httpx

from app.config import settings

INTERVAL_MS = {
    "1m": 60_000,
    "5m": 300_000,
    "1h": 3_600_000,
}


def fetch_klines(
    symbol: str,
    interval: str,
    limit: int = 1000,
    start_time: int | None = None,
    end_time: int | None = None,
) -> List[list]:
    params: dict = {"symbol": symbol, "interval": interval, "limit": limit}
    if start_time:
        params["startTime"] = start_time
    if end_time:
        params["endTime"] = end_time

    url = f"{settings.binance_klines_base_url}/api/v3/klines"
    with httpx.Client(timeout=30.0) as client:
        resp = client.get(url, params=params)
        resp.raise_for_status()
        return resp.json()


def backfill_klines(symbol: str, interval: str, days: int) -> List[dict]:
    """Paginate Binance klines for the last N days."""
    now_ms = int(time.time() * 1000)
    start_ms = now_ms - days * 86_400_000
    all_rows: List[dict] = []
    cursor = start_ms

    while cursor < now_ms:
        batch = fetch_klines(symbol, interval, limit=1000, start_time=cursor)
        if not batch:
            break
        for row in batch:
            all_rows.append(_parse_row(symbol, interval, row))
        last_open = batch[-1][0]
        step = INTERVAL_MS.get(interval, 60_000)
        cursor = last_open + step
        if len(batch) < 1000:
            break
        time.sleep(0.1)

    return all_rows


def _parse_row(symbol: str, interval: str, row: list) -> dict:
    return {
        "symbol": symbol,
        "interval": interval,
        "open_time": int(row[0]),
        "open": float(row[1]),
        "high": float(row[2]),
        "low": float(row[3]),
        "close": float(row[4]),
        "volume": float(row[5]),
    }


def fetch_latest(symbol: str, interval: str, limit: int = 10) -> List[dict]:
    rows = fetch_klines(symbol, interval, limit=limit)
    return [_parse_row(symbol, interval, r) for r in rows]
