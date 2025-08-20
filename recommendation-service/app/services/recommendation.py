from app.models.schemas import PortfolioRequest, RecommendationResponse

def make_recommendation(portfolio: PortfolioRequest) -> RecommendationResponse:
    btc = next((a for a in portfolio.assets if a.assetSymbol == "BTC"), None)
    if btc and btc.quantity < 1:
        message = "Consider increasing your BTC holdings."
        action = "BUY"
    else:
        message = "Your BTC position is strong!"
        action = "HOLD"
    return RecommendationResponse(message=message, action=action)
