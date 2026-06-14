"use client";
import { useBalances } from "../hooks/useBalances";
import { Balance, BalancesResponse } from "../types";

export default function Balances() {
  const { data, isLoading, error } = useBalances<BalancesResponse>();

  if (isLoading) return <div>Loading balances...</div>;
  if (error) return <div>Error loading balances!</div>;

  const rows =
    (data?.balances ?? []).filter(
      (b) => Number(b.free) > 0 || Number(b.locked) > 0
    );

  return (
    <section>
      <h2 className="text-xl font-semibold mb-2">Binance Balances</h2>
      <table className="w-full text-sm">
        <thead><tr><th className="text-left">Asset</th><th>Free</th><th>Locked</th></tr></thead>
        <tbody>
          {rows.map((b) => (
            <tr key={b.asset}>
              <td>{b.asset}</td>
              <td className="text-right">{b.free}</td>
              <td className="text-right">{b.locked}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
