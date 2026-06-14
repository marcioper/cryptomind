"use client";
import { useBotPnl } from "@/hooks/useBot";
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from "recharts";

export default function PnlChart() {
  const { data, isLoading } = useBotPnl("30d");

  if (isLoading) return <div>Loading PnL chart...</div>;
  if (!data?.monthly?.length) return <div className="text-gray-500 text-sm">No PnL data yet.</div>;

  const chartData = data.monthly.map((m) => ({
    month: m.month,
    pnl: m.pnl,
    cumulative: data.monthly
      .filter((x) => x.month <= m.month)
      .reduce((sum, x) => sum + x.pnl, 0),
  }));

  return (
    <section className="border rounded-lg p-4">
      <h2 className="text-xl font-semibold mb-4">Monthly PnL</h2>
      <ResponsiveContainer width="100%" height={250}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="cumulative" stroke="#16a34a" strokeWidth={2} dot />
        </LineChart>
      </ResponsiveContainer>
    </section>
  );
}
