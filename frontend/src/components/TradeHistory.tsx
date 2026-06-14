"use client";
import { useBotTrades } from "@/hooks/useBot";

export default function TradeHistory() {
  const { data, isLoading } = useBotTrades();

  if (isLoading) return <div>Loading trades...</div>;
  if (!data?.length) return <div className="text-gray-500 text-sm">No trades yet.</div>;

  return (
    <section className="border rounded-lg p-4">
      <h2 className="text-xl font-semibold mb-4">Recent Trades</h2>
      <table className="w-full text-sm">
        <thead>
          <tr className="text-left border-b">
            <th className="py-2">Time</th>
            <th>Side</th>
            <th>Symbol</th>
            <th className="text-right">Qty</th>
            <th className="text-right">Price</th>
            <th className="text-right">PnL</th>
            <th>Signal</th>
          </tr>
        </thead>
        <tbody>
          {data.map((t) => (
            <tr key={t.id} className="border-b border-gray-100">
              <td className="py-2">{new Date(t.executedAt).toLocaleString()}</td>
              <td className={t.side === "BUY" ? "text-green-600" : "text-red-600"}>{t.side}</td>
              <td>{t.symbol}</td>
              <td className="text-right">{t.quantity?.toFixed(6)}</td>
              <td className="text-right">${t.price?.toFixed(2)}</td>
              <td className={`text-right ${(t.pnl ?? 0) >= 0 ? "text-green-600" : "text-red-600"}`}>
                ${t.pnl?.toFixed(2)}
              </td>
              <td>{t.signal} ({t.confidence?.toFixed(2)})</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
