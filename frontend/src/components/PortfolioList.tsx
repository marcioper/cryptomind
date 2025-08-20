"use client";
import { usePortfolios } from "../hooks/usePortfolios";

export default function PortfolioList({ userId }: { userId: number }) {
  const { data, isLoading, error } = usePortfolios(userId);

  if (isLoading) return <div>Loading portfolios...</div>;
  if (error) return <div>Error loading portfolios!</div>;
  if (!data || data.length === 0) return <div>No assets yet.</div>;

  return (
    <table>
      <thead>
        <tr>
          <th>Asset</th>
          <th>Quantity</th>
        </tr>
      </thead>
      <tbody>
        {data?.map((p) => (
          <tr key={p.id}>
            <td>{p.assetSymbol}</td>
            <td>{p.quantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
