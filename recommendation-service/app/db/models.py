from sqlalchemy import BigInteger, Column, Float, String, UniqueConstraint
from app.db.database import Base


class Candle(Base):
    __tablename__ = "candles"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    symbol = Column(String(20), nullable=False, index=True)
    interval = Column(String(10), nullable=False, index=True)
    open_time = Column(BigInteger, nullable=False)
    open = Column(Float, nullable=False)
    high = Column(Float, nullable=False)
    low = Column(Float, nullable=False)
    close = Column(Float, nullable=False)
    volume = Column(Float, nullable=False)

    __table_args__ = (
        UniqueConstraint("symbol", "interval", "open_time", name="uq_candle"),
    )
