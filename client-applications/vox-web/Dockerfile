# Dockerfile for Vox Frontend
FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
# Generate package-lock.json if missing (since we just created package.json)
RUN npm install
COPY . .
# We need to ensure the build script works even if src is empty or minimal.
# For now, we assume a standard structure will be populated.
# But since src is empty, `ng build` might fail.
# I will create a dummy index.html and main.ts to satisfy the build if needed.
# For now, let's just assume the user will populate it or using 'npm install' is enough for the docker build to pass the install phase.
# Actually, 'npm run build' will fail without src.
# I'll modify this Dockerfile to be a bit more resilient or I should scaffold src/ too.
# I'll create a minimal src/ structure in the next step.
RUN npm run build -- --configuration production

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/vox/browser /usr/share/nginx/html
EXPOSE 80
