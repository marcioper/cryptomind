import { useQuery } from "@tanstack/react-query";
import api from "@/app/api";
import { Portfolio } from "../types";

export const usePortfolios = (userId: number) =>
  useQuery<Portfolio[]>({
    queryKey: ["portfolios", userId],
    queryFn: async () => {
      const { data } = await api.get(`/user/${userId}`); // Adjust endpoint as needed
      return data;
    },
  });
