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

`V2__categories_unique_name_type.sql` меняет уникальность категорий с
`(user_id, name)` на `(user_id, name, type)` — нужно, чтобы у пользователя
могли быть отдельные категории «Прочее» для расходов и для доходов.

## REST API

- `POST /api/users` — создать пользователя (email, password, fullName);
  пароль хешируется (BCrypt), пользователю сразу создаются категории по
  умолчанию. Полноценной аутентификации (логин/JWT) пока нет — это
  временная точка входа для регистрации.
- `GET /api/users/{id}` — профиль пользователя
- `GET/POST /api/users/{userId}/categories`,
  `GET/PUT/DELETE /api/users/{userId}/categories/{id}` — CRUD категорий.
  Удалить категорию нельзя, если на неё уже есть транзакции или лимит
  (409 Conflict)
- `GET/POST /api/users/{userId}/transactions`,
  `GET/PUT/DELETE /api/users/{userId}/transactions/{id}` — CRUD транзакций
- `GET/POST /api/users/{userId}/limits`,
  `GET/PUT/DELETE /api/users/{userId}/limits/{id}` — CRUD месячных лимитов
  (один лимит на категорию)

### Категории по умолчанию

При регистрации пользователю автоматически создаются категории:

- **Расходы**: Еда, Транспорт, Коммуналка, Развлечения, Здоровье, Прочее
- **Доходы**: Зарплата, Подработка, Прочее

## Сборка для продакшена

```bash
# Backend
cd backend && ./mvnw -DskipTests package

# Frontend
cd frontend && npm run build
```

Также доступны `Dockerfile` в `backend/` и `frontend/` для сборки контейнерных образов.
