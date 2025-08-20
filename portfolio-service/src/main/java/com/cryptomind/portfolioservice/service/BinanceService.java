package com.cryptomind.portfolioservice.service;

import com.binance.connector.client.impl.SpotClientImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BinanceService {

    private final SpotClientImpl client;

    public BinanceService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String apiKey = dotenv.get("BINANCE_API_KEY", System.getenv("BINANCE_API_KEY"));
        String apiSecret = dotenv.get("BINANCE_API_SECRET", System.getenv("BINANCE_API_SECRET"));
        String baseUrl = "https://testnet.binance.vision";

        client = new SpotClientImpl(apiKey, apiSecret, baseUrl);
    }

    // Get account balances (signed GET request)
    public String getAccountBalances() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        return client.createTrade().account(parameters);
    }

    // Create an order (signed POST request)
    public String createOrder(String symbol, String side, String type, String quantity) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("symbol", symbol);
        params.put("side", side);   // "BUY" or "SELL"
        params.put("type", type);   // "MARKET" or "LIMIT"
        params.put("quantity", quantity);

        return client.createTrade().newOrder(params);
    }
}
