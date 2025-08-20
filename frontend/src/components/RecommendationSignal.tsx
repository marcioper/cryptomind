"use client";
import { useRecommendation } from "../hooks/useRecommendation";
export default function RecommendationSignal({ userId }: { userId: number }) {
  const { data, isLoading, error } = useRecommendation(userId);

  if (isLoading) return <div>Loading recommendation...</div>;
  if (error) return <div>Error loading recommendation!</div>;
  if (!data) return <div>No recommendation data.</div>;

  return (
    <div>
      <h2>Recommendation</h2>
      <strong>{data.signal}</strong>
      {data.reason && <div>{data.reason}</div>}
    </div>
  );
}
