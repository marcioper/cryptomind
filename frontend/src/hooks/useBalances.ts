import { useQuery, UseQueryResult } from "@tanstack/react-query";
import api from "@/app/api";

export function useBalances<T = unknown>(): UseQueryResult<T> {
  return useQuery<T>({
    queryKey: ["balances"],
    queryFn: async () => {
      const { data } = await api.get("/balances"); // shape: { balances: [...] }
      return data as T;
    },
  });
}
