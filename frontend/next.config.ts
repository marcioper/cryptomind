import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone', // <-- important: generates a minimal server bundle
};

export default nextConfig;
