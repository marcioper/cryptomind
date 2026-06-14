import axios from "axios";

const botApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_BOT_API_BASE ?? "http://localhost:8081/api/bot",
});

export default botApi;
