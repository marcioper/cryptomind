export interface Portfolio {
  id: number;
  userId: number;
  assetSymbol: string;
  quantity: number;
}
export interface Recommendation {
  signal: "BUY" | "SELL" | "HOLD";
  confidence?: number;
  reason?: string;
  interval?: string;
  model_version?: string;
}
export interface Balance {
  asset: string;
  free: string;   // free balance
  locked: string; // locked balance
}
export interface  BalancesResponse { 
  balances: Balance[] 
}
