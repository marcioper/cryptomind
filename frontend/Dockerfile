# frontend/Dockerfile

# ---------- Build stage ----------
FROM node:20-alpine AS builder
WORKDIR /app

ARG NEXT_PUBLIC_API_BASE=http://localhost:8081/api/portfolio
ARG NEXT_PUBLIC_BOT_API_BASE=http://localhost:8081/api/bot
ENV NEXT_PUBLIC_API_BASE=$NEXT_PUBLIC_API_BASE
ENV NEXT_PUBLIC_BOT_API_BASE=$NEXT_PUBLIC_BOT_API_BASE

# Install dependencies first (better layer caching)
COPY package*.json ./
# If you use Yarn or pnpm, swap this line accordingly:
# RUN yarn install --frozen-lockfile
# RUN pnpm install --frozen-lockfile
RUN npm ci

# Copy the rest of the source and build
COPY . .
# Ensure next.config.ts has `output: 'standalone'`
RUN npm run build

# ---------- Runtime stage ----------
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV PORT=3000

# Copy the compiled standalone server and assets from builder
# - .next/standalone contains the compiled server
# - .next/static are the static assets served by Next
# - public/ are your public assets (favicons, images, etc.)
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

# If your app reads env at runtime (e.g., NEXT_PUBLIC_API_BASE),
# pass it via `docker run -e` or compose `environment:`.

EXPOSE 3000
CMD ["node", "server.js"]
