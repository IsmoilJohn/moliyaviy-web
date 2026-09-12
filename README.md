# moliyaviy-web — Финансовый помощник

Веб-приложение для учёта личных финансов: доходы/расходы по категориям,
настраиваемые месячные лимиты и дашборд со статистикой.

## Стек

- **Backend**: Java 21, Spring Boot 3.3 (Web, Data JPA, Validation, Actuator), Flyway, Maven
- **Frontend**: Vue 3, TypeScript, Vite, Vue Router, Pinia, Axios, ESLint + Prettier
- **База данных**: PostgreSQL 16
- **Инфраструктура**: Docker / Docker Compose

## Структура проекта

```
moliyaviy-web/
├── backend/     # Spring Boot приложение (REST API)
├── frontend/    # Vue 3 SPA
└── docker-compose.yml   # PostgreSQL для локальной разработки
```

## Быстрый старт

### 1. База данных

```bash
docker compose up -d postgres
```

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

API поднимется на `http://localhost:8080`, проверка здоровья: `GET /api/health`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Приложение будет доступно на `http://localhost:5173`, запросы к `/api/*` проксируются на backend (см. `vite.config.ts`).

## Схема базы данных

Миграции лежат в `backend/src/main/resources/db/migration/` (Flyway,
применяются автоматически при старте backend). Начальная схема
(`V1__init_schema.sql`) описывает таблицы:

- **users** — пользователи (email, хэш пароля, имя)
- **categories** — категории доходов/расходов, настраиваемые пользователем
  (название, тип `INCOME`/`EXPENSE`, цвет для графиков)
- **transactions** — транзакции (пользователь, категория, тип, сумма, дата,
  комментарий)
- **limits** — месячный лимит по категории расходов (один лимит на категорию)

## Сборка для продакшена

```bash
# Backend
cd backend && ./mvnw -DskipTests package

# Frontend
cd frontend && npm run build
```

Также доступны `Dockerfile` в `backend/` и `frontend/` для сборки контейнерных образов.
