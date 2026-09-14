# moliyaviy-web — Финансовый помощник

Веб-приложение для учёта личных финансов: доходы/расходы по категориям,
настраиваемые месячные лимиты и дашборд со статистикой.

## Стек

- **Backend**: Java 21, Spring Boot 3.3 (Web, Data JPA, Validation, Actuator, Security), Flyway, JWT (jjwt), Maven
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

## Аутентификация

- `POST /api/users` — регистрация (email, password, fullName), публичный
  эндпоинт. Пароль хешируется BCrypt, пользователю сразу создаются
  категории по умолчанию.
- `POST /api/auth/login` — вход (email, password) → JWT-токен
  (`{"token", "tokenType": "Bearer", "user": {...}}`), подписан HS-ключом
  (`app.jwt.secret`, живёт `app.jwt.expiration-ms` мс, по умолчанию час).

Все остальные эндпоинты защищены и требуют заголовок
`Authorization: Bearer <token>`. `userId` для транзакций/категорий/лимитов
теперь берётся **из токена** (`JwtAuthenticationFilter` кладёт его в
`SecurityContext`), а не из пути или тела запроса — так пользователь не
может ни прочитать, ни изменить чужие данные (запрос к чужому ресурсу по
id отдаёт `404`, а не `403`, чтобы не подтверждать факт его существования).
Запрос без токена или с невалидным/просроченным токеном получает `401`.

## REST API

- `GET /api/users/me` — профиль текущего пользователя
- `GET/POST /api/categories`, `GET/PUT/DELETE /api/categories/{id}` — CRUD
  категорий. Удалить категорию нельзя, если на неё уже есть транзакции или
  лимит (409 Conflict)
- `GET/POST /api/transactions`, `GET/PUT/DELETE /api/transactions/{id}` —
  CRUD транзакций
- `GET/POST /api/limits`, `GET/PUT/DELETE /api/limits/{id}` — CRUD
  месячных лимитов (один лимит на категорию)
- `GET /api/dashboard?month=YYYY-MM` — статистика за месяц:
  - `totalIncome`, `totalExpense`, `balance` (доходы − расходы)
  - `expensesByCategory` — расходы по категориям (сумма и `%` от общих
    расходов за месяц), отсортировано по убыванию суммы
  - `limits` — по каждой категории с настроенным лимитом: `limit`, `spent`,
    `remaining` (может быть отрицательным) и `exceeded` (boolean)

  Неверный формат месяца или отсутствие параметра → `400`.

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

## Деплой backend (Railway)

Активируется профилем `SPRING_PROFILES_ACTIVE=prod`, конфиг — в
`application-prod.yml`:

- **Подключение к БД**: приоритетный вариант — переменные `PGHOST`,
  `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` (их даёт плагин Postgres
  в Railway). Если задан только `DATABASE_URL`
  (`postgres://user:pass@host:port/db`) — `DatabaseUrlEnvironmentPostProcessor`
  сам конвертирует его в JDBC-урл, оба варианта работают из коробки.
- **Порт**: `server.port: ${PORT:8080}` в базовом `application.yml` — Railway
  подставляет свой `PORT`, локально используется дефолт `8080`.
- **JWT**: `JWT_SECRET` обязателен в `prod`-профиле (без дефолта — сервис не
  запустится без него, чтобы не работать на dev-секрете в продакшене).
- **Flyway**: `baseline-on-migrate: true` + `baseline-version: 2` — если
  схема уже накатана вручную (например, через `pg_dump`/`psql`) и таблица
  `flyway_schema_history` есть в дампе, это no-op; это подстраховка на
  случай, если её там нет.

Обязательные переменные окружения сервиса backend в Railway:
`SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET`, и либо `PGHOST`/`PGPORT`/
`PGDATABASE`/`PGUSER`/`PGPASSWORD`, либо `DATABASE_URL`.

Также нужно добавить `CORS_ALLOWED_ORIGINS` — публичный домен фронтенда
(если он задеплоен отдельным сервисом/на другом хосте), иначе браузер
заблокирует запросы. Для нескольких доменов — через запятую.

## Деплой frontend

Frontend — статический SPA (Vue 3 + Vite), собирается в контейнере с nginx
(`frontend/Dockerfile`) и отдаёт `index.html` на все пути, кроме статики
(`frontend/nginx.conf`) — нужно для клиентского роутинга (`/login` и т.д.
не должны отдавать 404 при прямом переходе или обновлении страницы).

Backend и frontend в проде — разные origin (разные Railway-сервисы/домены),
поэтому:

- **`VITE_API_URL`** — публичный URL backend-сервиса (например,
  `https://backend-production-xxxx.up.railway.app`, без `/api` в конце).
  Это **build-time** переменная — Vite вставляет её в статический бандл при
  сборке, изменить после сборки контейнера уже нельзя. На Railway она
  автоматически подставится в `docker build` как build-arg, если задать
  переменную сервиса с тем же именем — просто добавьте её в Variables
  фронтенд-сервиса.
- На backend-сервисе не забудьте выставить **`CORS_ALLOWED_ORIGINS`**
  на публичный домен фронтенда (см. выше) — иначе запросы с фронтенда
  будут блокироваться браузером (проверено: без совпадения origin
  бэкенд отвечает `403 Invalid CORS request` на preflight).

Локальная сборка с той же конфигурацией:

```bash
cd frontend
VITE_API_URL=https://your-backend.example.com npm run build
```
