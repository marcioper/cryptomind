"use client";
import { useRecommendation } from "../hooks/useRecommendation";
import { usePortfolios } from "../hooks/usePortfolios";

export default function RecommendationSignal({ userId }: { userId: number }) {
  const { data: portfolios } = usePortfolios(userId);
  const portfolioId = portfolios?.[0]?.id;
  const { data, isLoading, error } = useRecommendation(portfolioId);

  if (isLoading) return <div>Loading recommendation...</div>;
  if (error) return <div>Error loading recommendation!</div>;
  if (!data) return <div>No recommendation data.</div>;

  return (
    <div>
      <h2>Recommendation</h2>
      <strong>{data.signal}</strong>
      {data.confidence != null && <div>Confidence: {(data.confidence * 100).toFixed(0)}%</div>}
      {data.reason && <div className="text-sm text-gray-500">{data.reason}</div>}
    </div>
  );
}
