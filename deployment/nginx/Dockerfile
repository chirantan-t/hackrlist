# ===================================================================
# Dockerfile - Frontend & Nginx Reverse Proxy (Multi-Stage Build)
# Target Architecture: Node 22 Build -> Nginx 1.27 Alpine Runtime
# ===================================================================

# Stage 1: Build React Production Bundle
FROM node:22-alpine AS frontend-builder
WORKDIR /app

# Install dependencies
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

# Copy source and build static assets
COPY frontend/ ./
RUN npm run build

# Stage 2: Serve with Nginx Alpine
FROM nginx:1.27-alpine AS nginx-runtime

# Remove default Nginx welcome assets
RUN rm -rf /usr/share/nginx/html/*

# Copy compiled static assets from builder stage
COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# Copy customized Nginx reverse proxy configuration
COPY deployment/nginx/default.conf /etc/nginx/conf.d/default.conf

# Security: Ensure non-root ownership on cache and logs
RUN touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid /var/cache/nginx /var/log/nginx /usr/share/nginx/html

# Expose HTTP port 80
EXPOSE 80

# Health check probe
HEALTHCHECK --interval=15s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:80/ || exit 1

# Start Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
