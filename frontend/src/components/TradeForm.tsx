"use client";
import { useState } from "react";
import api from "@/app/api";

export default function TradeForm({ userId, action }: { userId: number; action: "buy" | "sell" }) {
  const [assetSymbol, setAssetSymbol] = useState("");
  const [quantity, setQuantity] = useState("");
  const [result, setResult] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setResult("...");
    try {
      const res = await api.post(`/${action}`, {
        userId,
        assetSymbol,
        quantity: parseFloat(quantity),
      });
      setResult(JSON.stringify(res.data));
    } catch (err: any) {
      setResult("Error: " + err.message);
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
