FROM node:18-alpine

WORKDIR /app

COPY package*.json ./

RUN apk add --no-cache wget && npm install --production

COPY . .

EXPOSE 4000

CMD ["npm", "start"]
