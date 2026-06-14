# CryptoMind v1

## Vision

Autonomous Binance Testnet trading bot that executes fixed-size micro-trades, uses ML signals from OHLCV candles, and measures monthly returns via backtest + live simulation.

## Core Value

> Bot testnet that executes micro-trades with fixed notional and proves monthly return viability via backtest + live simulated operation.

## Constraints

- **Environment:** Binance Testnet only (no production accounts)
- **Intelligence:** Classical ML (LightGBM/sklearn), not LLM
- **Trade sizing:** Fixed USDT amount per trade (configurable)
- **Scope:** Single pair (BTCUSDT) initially; no Kafka/Redis/GraphQL unless proven necessary

## Architecture

| Service | Role |
|---------|------|
| `frontend` | Bot dashboard, PnL, trade history |
| `portfolio-service` | Bot scheduler, risk engine, Binance execution |
| `recommendation-service` | Strategy service: OHLCV ingest, features, ML, backtest |
| `user-service` | User CRUD (minimal auth in v1) |
| `postgres` | Candles, bot state, trade logs |

## Brownfield Context

Existing MVP: Docker compose, Binance testnet integration, portfolio CRUD, random recommendation signal, basic frontend dashboard. Gaps: OHLCV pipeline, real ML, backtest, automated bot, PnL dashboard.
