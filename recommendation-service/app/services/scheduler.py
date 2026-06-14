import logging

from apscheduler.schedulers.background import BackgroundScheduler

from app.config import settings
from app.db.database import SessionLocal, init_db
from app.services import candle_store, model_training

logger = logging.getLogger(__name__)
scheduler = BackgroundScheduler()


def _run_backfill():
    db = SessionLocal()
    try:
        symbol = settings.default_symbol
        for interval in settings.default_intervals.split(","):
            interval = interval.strip()
            count = candle_store.backfill(db, symbol, interval, settings.backfill_days)
            logger.info("Backfilled %s %s: %d rows", symbol, interval, count)
    finally:
        db.close()


def _run_update():
    db = SessionLocal()
    try:
        symbol = settings.default_symbol
        for interval in settings.default_intervals.split(","):
            interval = interval.strip()
            count = candle_store.update_latest(db, symbol, interval)
            logger.info("Updated %s %s: %d rows", symbol, interval, count)
    finally:
        db.close()


def _run_train_if_needed():
    model, _ = model_training.load_model()
    if model is not None:
        return
    db = SessionLocal()
    try:
        candles = candle_store.get_candles(db, settings.default_symbol, "5m", limit=5000)
        if len(candles) >= 500:
            result = model_training.train_model(candles)
            logger.info("Auto-trained model: %s", result.get("version"))
    except Exception as e:
        logger.warning("Auto-train skipped: %s", e)
    finally:
        db.close()


def start_scheduler():
    init_db()
    scheduler.add_job(_run_backfill, "date", id="backfill_once")
    scheduler.add_job(_run_update, "interval", minutes=1, id="candle_update")
    scheduler.add_job(_run_train_if_needed, "interval", minutes=30, id="auto_train")
    scheduler.start()
    logger.info("Scheduler started")


def stop_scheduler():
    if scheduler.running:
        scheduler.shutdown(wait=False)
