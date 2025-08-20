import { useQuery } from "@tanstack/react-query";
import api from "@/app/api";

export const useBalances = () =>
  useQuery<any>({
    queryKey: ["balances"],
    queryFn: async () => {
      const { data } = await api.get("/balances");
      return data;
    },
  });
