import { useQuery } from "@tanstack/react-query";
import api from "@/app/api";
import { Recommendation } from "../types";

export const useRecommendation = (userId: number) =>
  useQuery<Recommendation>({
    queryKey: ["recommendation", userId],
    queryFn: async () => {
      const { data } = await api.get(`/recommendation/${userId}`);
      return data;
    },
    enabled: !!userId,
  });
