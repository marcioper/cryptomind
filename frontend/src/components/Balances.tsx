"use client";
import { useBalances } from "../hooks/useBalances";

export default function Balances() {
  const { data, isLoading, error } = useBalances();

  if (isLoading) return <div>Loading balances...</div>;
  if (error) return <div>Error loading balances!</div>;

  return (
    <div>
      <h2>Balances</h2>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
}
