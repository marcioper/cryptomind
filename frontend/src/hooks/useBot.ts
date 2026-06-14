import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import botApi from "@/app/botApi";

export interface BotStatus {
  running: boolean;
  symbol: string;
  interval: string;
  tradeSizeUsdt: number;
  maxDailyTrades: number;
  dailyTradeCount: number;
  minConfidence: number;
  accumulatedPnl: number;
  lastRunAt?: string;
  cooldownUntil?: string;
  consecutiveLosses: number;
  lastSignal?: string;
  lastConfidence?: number;
}

export interface TradeLogEntry {
  id: number;
  symbol: string;
  side: string;
  quantity: number;
  price: number;
  notionalUsdt: number;
  pnl: number;
  signal: string;
  confidence: number;
  status: string;
  executedAt: string;
}

export interface PnlSummary {
  totalPnl: number;
  returnPct: number;
  tradeCount: number;
  period: string;
  monthly: { month: string; pnl: number }[];
}

export const useBotStatus = () =>
  useQuery<BotStatus>({
    queryKey: ["bot", "status"],
    queryFn: async () => {
      const { data } = await botApi.get("/status");
      return data;
    },
    refetchInterval: 30_000,
  });

export const useBotTrades = () =>
  useQuery<TradeLogEntry[]>({
    queryKey: ["bot", "trades"],
    queryFn: async () => {
      const { data } = await botApi.get("/trades");
      return data;
    },
    refetchInterval: 30_000,
  });

export const useBotPnl = (period = "30d") =>
  useQuery<PnlSummary>({
    queryKey: ["bot", "pnl", period],
    queryFn: async () => {
      const { data } = await botApi.get("/pnl", { params: { period } });
      return data;
    },
    refetchInterval: 60_000,
  });

export const useBotControl = () => {
  const qc = useQueryClient();
  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ["bot"] });
  };
  const start = useMutation({
    mutationFn: () => botApi.post("/start"),
    onSuccess: invalidate,
  });
  const stop = useMutation({
    mutationFn: () => botApi.post("/stop"),
    onSuccess: invalidate,
  });
  return { start, stop };
};
