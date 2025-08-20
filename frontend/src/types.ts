export interface Portfolio {
  id: number;
  userId: number;
  assetSymbol: string;
  quantity: number;
}
export interface Recommendation {
  signal: "BUY" | "SELL" | "HOLD";
  reason?: string;
}
