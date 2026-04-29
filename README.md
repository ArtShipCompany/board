# BOARD

## Start project
```
docker compose up -d --build
```

## Swagger-UI
```
http://localhost:8081/swagger-ui/index.html
```

## API-Docs
```
http://localhost:8081/v3/api-docs
```

## Setup backend
```bash
cd backend
```

## Setup frontend
```bash
cd frontend
nvm install $(cat .nvmrc)
nvm use $(cat .nvmrc)
npm install
npm start
```

# Requirements

### Backend
- Java 17
- Maven 3.6+

### Frontend
- Node.js 22.20.0
- npm 9.0.0+

### Recommended
- nvm (Node Version Manager) 1.2.2
- Docker & Docker Compose