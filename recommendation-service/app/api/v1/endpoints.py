import random

from fastapi import APIRouter
from app.models.schemas import RecommendationRequest, RecommendationResponse

router = APIRouter()

@router.get("/health")
def health():
    return {"status": "Recommendation Service OK!"}

@router.post("/recommend", response_model=RecommendationResponse)
def recommend(data: RecommendationRequest):
    # Demo: randomly return BUY/SELL/HOLD
    signal = random.choice(["BUY", "SELL", "HOLD"])
    return RecommendationResponse(signal=signal)
