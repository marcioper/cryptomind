import Balances from "@/components/Balances";
import PortfolioList from "@/components/PortfolioList";
import TradeForm from "@/components/TradeForm";
import RecommendationSignal from "@/components/RecommendationSignal";

const userId = 1; // Change as needed

export default function Home() {
  return (
    <main style={{ maxWidth: 600, margin: "auto", padding: 40 }}>
      <h1>CryptoMind Dashboard</h1>
      <Balances />
      <TradeForm userId={userId} action="buy" />
      <TradeForm userId={userId} action="sell" />
      <PortfolioList userId={userId} />
      <RecommendationSignal userId={userId} />
    </main>
  );
}
