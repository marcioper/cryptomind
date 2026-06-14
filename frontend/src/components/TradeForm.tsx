"use client";
import { useState } from "react";
import api from "@/app/api";
import axios, { AxiosError } from "axios";

type TradeAction = "buy" | "sell";

interface TradeResponse {
  // shape this to match your backend if you know it
  status?: string;
  orderId?: string | number;
  message?: string;
  [key: string]: unknown;
}

export default function TradeForm({
  userId,
  action,
}: {
  userId: number;
  action: TradeAction;
}) {
  const [assetSymbol, setAssetSymbol] = useState("");
  const [quantity, setQuantity] = useState("");
  const [result, setResult] = useState("");

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setResult("...");
    try {
      const res = await api.post<TradeResponse>(`/trade`, {
        userId,
        action,
        assetSymbol,
        quantity: parseFloat(quantity),
      });
      setResult(JSON.stringify(res.data, null, 2));
    } catch (err: unknown) {
      // Properly narrow the error instead of using `any`
      if (axios.isAxiosError(err)) {
        const ax = err as AxiosError<unknown>;
        const body =
          typeof ax.response?.data === "string"
            ? ax.response.data
            : JSON.stringify(ax.response?.data ?? ax.message);
        setResult(`Error: ${body}`);
      } else {
        setResult(`Error: ${String(err)}`);
      }
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
      <h3>{action === "buy" ? "Buy" : "Sell"} Crypto</h3>

      <input
        type="text"
        placeholder="Symbol (BTCUSDT)"
        value={assetSymbol}
        onChange={(e) => setAssetSymbol(e.target.value)}
      />

      <input
        type="number"
        step="any"
        placeholder="Quantity"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
      />

      <button type="submit">{action === "buy" ? "Buy" : "Sell"}</button>

      {result && <pre>{result}</pre>}
    </form>
  );
}
