from sqlalchemy.dialects.postgresql import insert
from sqlalchemy.orm import Session

from app.db.models import Candle
from app.services import binance_klines


def upsert_candles(db: Session, rows: list[dict]) -> int:
    if not rows:
        return 0
    stmt = insert(Candle).values(rows)
    stmt = stmt.on_conflict_do_nothing(constraint="uq_candle")
    result = db.execute(stmt)
    db.commit()
    return result.rowcount or len(rows)


def get_candles(
    db: Session,
    symbol: str,
    interval: str,
    limit: int = 500,
) -> list[Candle]:
    rows = (
        db.query(Candle)
        .filter(Candle.symbol == symbol, Candle.interval == interval)
        .order_by(Candle.open_time.desc())
        .limit(limit)
        .all()
    )
    return rows[::-1]


def backfill(db: Session, symbol: str, interval: str, days: int) -> int:
    rows = binance_klines.backfill_klines(symbol, interval, days)
    return upsert_candles(db, rows)


def update_latest(db: Session, symbol: str, interval: str) -> int:
    rows = binance_klines.fetch_latest(symbol, interval, limit=50)
    return upsert_candles(db, rows)
