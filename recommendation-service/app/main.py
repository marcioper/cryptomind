import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.v1.endpoints import router as v1_router
from app.services.scheduler import start_scheduler, stop_scheduler

logging.basicConfig(level=logging.INFO)


@asynccontextmanager
async def lifespan(app: FastAPI):
    start_scheduler()
    yield
    stop_scheduler()


app = FastAPI(title="CryptoMind Strategy Service", lifespan=lifespan)
app.include_router(v1_router, prefix="/api/v1")
