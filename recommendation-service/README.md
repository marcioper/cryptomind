# CryptoMind Recommendation Service

AI-powered service for crypto portfolio and trading recommendations.

---

## Prerequisites

- Python 3.11+
- pip
- Docker (optional)

---

## How to Run

```bash
# 1. Create and activate a virtual environment
python3 -m venv venv
source venv/bin/activate

# 2. Install requirements
pip install -r requirements.txt

# 3. Run Locally (for development)
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
# Service available at: http://localhost:8000/health

# 4. Build and Run with Docker
docker build -t cryptomind-recommendation-service .
docker run -p 8000:8000 cryptomind-recommendation-service

# Health Check
curl http://localhost:8000/health
# Should return:
# {"status": "Recommendation Service OK!"}