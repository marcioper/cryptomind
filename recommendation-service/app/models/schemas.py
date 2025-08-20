from pydantic import BaseModel
from typing import List

class Asset(BaseModel):
    assetSymbol: str
    quantity: float

class RecommendationRequest(BaseModel):
    userId: int
    assets: List[Asset]

class RecommendationResponse(BaseModel):
    signal: str
