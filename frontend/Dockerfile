FROM node:22-alpine AS build
WORKDIR /app
# Railway (and `docker build --build-arg`) pass service variables as build
# args for Dockerfile builds. VITE_API_URL must be present at BUILD time -
# Vite inlines import.meta.env.VITE_* into the static bundle, there is no
# way to change it at container runtime afterwards.
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
