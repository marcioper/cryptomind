import Balances from "@/components/Balances";
import BotPanel from "@/components/BotPanel";
import PnlChart from "@/components/PnlChart";
import PortfolioList from "@/components/PortfolioList";
import RecommendationSignal from "@/components/RecommendationSignal";
import TradeForm from "@/components/TradeForm";
import TradeHistory from "@/components/TradeHistory";

const userId = 1;

export default function Home() {
  return (
    <main className="max-w-5xl mx-auto p-6 space-y-8">
      <h1 className="text-2xl font-bold">CryptoMind Dashboard</h1>

      <BotPanel />
      <div className="grid md:grid-cols-2 gap-6">
        <PnlChart />
        <Balances />
      </div>
      <TradeHistory />

      <div className="grid md:grid-cols-2 gap-6">
        <PortfolioList userId={userId} />
        <RecommendationSignal userId={userId} />
      </div>

      <details className="border rounded-lg p-4">
        <summary className="cursor-pointer font-medium text-gray-600">
          Debug: Manual Trading (deprecated)
        </summary>
        <div className="grid md:grid-cols-2 gap-4 mt-4">
          <TradeForm userId={userId} action="buy" />
          <TradeForm userId={userId} action="sell" />
        </div>
      </details>
    </main>
  );
}
