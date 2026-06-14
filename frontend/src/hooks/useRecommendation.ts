import { useQuery } from "@tanstack/react-query";
import api from "@/app/api";
import { Recommendation } from "../types";

export const useRecommendation = (portfolioId: number | undefined) =>
  useQuery<Recommendation>({
    queryKey: ["recommendation", portfolioId],
    queryFn: async () => {
      const { data } = await api.get(`/${portfolioId}/recommendation`);
      return data;
    },
    enabled: !!portfolioId,
  });
