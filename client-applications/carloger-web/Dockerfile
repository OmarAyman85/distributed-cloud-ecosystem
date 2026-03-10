FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
# Skip build for now if src is incomplete, or use a dummy build command
# For scaffolding, we will assume standard structure is populated by the script
RUN npm run build -- --configuration production

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/carloger/browser /usr/share/nginx/html
EXPOSE 80
