# CryptoMind

Autonomous Binance Testnet trading bot with ML-driven signals, fixed-size trades, backtesting, and PnL monitoring.

## Stack

| Component | Tech |
|-----------|------|
| Frontend | Next.js 15, React Query, Recharts |
| Portfolio service | Java 21, Spring Boot, JPA |
| Strategy service | Python, FastAPI, sklearn |
| Database | PostgreSQL 15 |
| Exchange | Binance Testnet |

## Prerequisites

- Docker & Docker Compose
- Binance Testnet API keys from [testnet.binance.vision](https://testnet.binance.vision/)

## Quick Start

1. **Copy environment files**

```bash
cp .env.example .env
cp portfolio-service/.env.example portfolio-service/.env
```

2. **Add your Testnet API keys** to `.env` and `portfolio-service/.env`:

```
BINANCE_API_KEY=your_testnet_api_key
BINANCE_API_SECRET=your_testnet_api_secret
```

3. **Start the stack**

```bash
docker compose up --build
```

4. **Open the dashboard**

- Frontend: http://localhost:3000
- Portfolio API: http://localhost:8081/api/portfolio
- Strategy API: http://localhost:8000/api/v1/health

## Services

| Service | Port | Purpose |
|---------|------|---------|
| frontend | 3000 | Bot dashboard, PnL, trades |
| portfolio-service | 8081 | Bot scheduler, Binance execution, REST API |
| recommendation-service | 8000 | OHLCV ingest, ML inference, backtest |
| user-service | 8080 | User management |
| postgres | 5432 | Shared database |

## Bot Operation

1. Ensure candles are populated (strategy service backfills on startup).
2. Train model: `POST http://localhost:8000/api/v1/train`
3. Start bot: `POST http://localhost:8081/api/bot/start`
4. Monitor: dashboard at http://localhost:3000 or `GET /api/bot/status`
5. Stop bot: `POST http://localhost:8081/api/bot/stop`

## Key API Endpoints

### Portfolio Service (`/api/portfolio`)

- `GET /balances` — Binance testnet balances
- `POST /trade` — Manual buy/sell (debug)
- `GET /{portfolioId}/recommendation` — ML signal for portfolio
- `GET /api/bot/status` — Bot state and PnL summary
- `GET /api/bot/trades` — Recent trade log
- `POST /api/bot/start` / `POST /api/bot/stop` — Control bot

### Strategy Service (`/api/v1`)

- `GET /candles?symbol=BTCUSDT&interval=5m&limit=100`
- `POST /recommend` — Inference (symbol, interval)
- `POST /backtest` — Run backtest simulation
- `POST /train` — Retrain ML model

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `BOT_TRADE_SIZE_USDT` | 50 | Fixed notional per trade |
| `BOT_SYMBOL` | BTCUSDT | Trading pair |
| `BOT_INTERVAL` | 5m | Candle interval for signals |
| `BOT_MAX_DAILY_TRADES` | 20 | Daily trade cap |
| `BOT_MIN_CONFIDENCE` | 0.65 | Minimum signal confidence |

## Development

```bash
# Portfolio service
cd portfolio-service && ./gradlew bootRun

# Strategy service
cd recommendation-service && pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000

# Frontend
cd frontend && npm install && npm run dev
```

## v1 Scope

- Binance Testnet only
- Single pair (BTCUSDT)
- Classical ML (sklearn), not LLM
- Fixed USDT trade size
- No Kafka/Redis/GraphQL in active path
