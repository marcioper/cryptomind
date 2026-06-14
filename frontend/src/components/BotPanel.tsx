"use client";
import { useBotControl, useBotPnl, useBotStatus } from "@/hooks/useBot";

export default function BotPanel() {
  const { data: status, isLoading } = useBotStatus();
  const { data: pnl } = useBotPnl("30d");
  const { start, stop } = useBotControl();

  if (isLoading || !status) return <div>Loading bot status...</div>;

  return (
    <section className="border rounded-lg p-4 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Trading Bot</h2>
        <span
          className={`px-3 py-1 rounded-full text-sm font-medium ${
            status.running ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-600"
          }`}
        >
          {status.running ? "RUNNING" : "STOPPED"}
        </span>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
        <div>
          <p className="text-gray-500">Pair</p>
          <p className="font-medium">{status.symbol} ({status.interval})</p>
        </div>
        <div>
          <p className="text-gray-500">Trade Size</p>
          <p className="font-medium">${status.tradeSizeUsdt} USDT</p>
        </div>
        <div>
          <p className="text-gray-500">Trades Today</p>
          <p className="font-medium">{status.dailyTradeCount} / {status.maxDailyTrades}</p>
        </div>
        <div>
          <p className="text-gray-500">Accumulated PnL</p>
          <p className={`font-medium ${status.accumulatedPnl >= 0 ? "text-green-600" : "text-red-600"}`}>
            ${status.accumulatedPnl?.toFixed(2) ?? "0.00"}
          </p>
        </div>
        <div>
          <p className="text-gray-500">Last Signal</p>
          <p className="font-medium">{status.lastSignal ?? "—"} ({status.lastConfidence?.toFixed(2) ?? "—"})</p>
        </div>
        <div>
          <p className="text-gray-500">30d Return</p>
          <p className={`font-medium ${(pnl?.returnPct ?? 0) >= 0 ? "text-green-600" : "text-red-600"}`}>
            {pnl?.returnPct?.toFixed(2) ?? "0.00"}%
          </p>
        </div>
      </div>

      <div className="flex gap-2">
        <button
          onClick={() => start.mutate()}
          disabled={status.running || start.isPending}
          className="px-4 py-2 bg-green-600 text-white rounded disabled:opacity-50"
        >
          Start Bot
        </button>
        <button
          onClick={() => stop.mutate()}
          disabled={!status.running || stop.isPending}
          className="px-4 py-2 bg-red-600 text-white rounded disabled:opacity-50"
        >
          Stop Bot
        </button>
      </div>
    </section>
  );
}
