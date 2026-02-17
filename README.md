# KnowledgeBase

Полноценное приложение для управления документами с аутентификацией, ролями пользователей и хранением файлов в MinIO.  
Бэкенд написан на **Java / Spring Boot**, фронтенд — на **Next.js / React**.

## Почему этот проект

Управление документами кажется простым, пока не приходится заниматься хранением файлов, 
предварительно подписанными URL-адресами, доступом на основе ролей и безопасностью токенов.

Ключевые инженерные решения:
- **MinIO over S3** — самохостинговое хранилище объектов с генерацией предварительно подписанных URL-адресов 
  для безопасного доступа к файлам с ограничением по времени
- **Сохранение токенов обновления** — хранение в БД с защитой от гонки ,
 а не только в памяти или куки
- **Доступ на основе ролей** — детализированные аннотации @PreAuthorize, 
отдельные валидаторы для границ USER/ADMIN

## Основные возможности

- **Аутентификация и авторизация**
  - Регистрация и вход пользователей (`/api/auth/register`, `/api/auth/login`)
  - JWT access/refresh токены, хранение refresh‑токенов в БД
  - Обновление access‑токена по refresh‑токену (`/api/auth/refresh`)
- **Управление пользователями**
  - Роли: `USER`, `ADMIN`
  - Просмотр списка пользователей (только ADMIN)
  - Блокировка и активация пользователя (`/api/users/{id}/suspend`, `/api/users/{id}/activate`)
  - Обновление данных профиля
- **Управление документами**
  - Загрузка файлов с сохранением метаданных в PostgreSQL и содержимого в MinIO
  - Статусы документа: `DRAFT`, `PUBLISHED`, `ARCHIVED`
  - Получение списка документов по статусу (`/api/documents?status=...`)
  - Просмотр метаданных и скачивание файла (`/api/documents/{id}`, `/api/documents/{id}/download`)
  - Архивация документа с удалением файла из MinIO (`/api/documents/{id}/archive`)
  - Получение временной ссылки на файл (`/api/documents/{id}/url`)
- **Фронтенд‑интерфейс**
  - Экран авторизации (login/registration)
  - Дашборд с сайдбаром и списком документов
  - Отдельная страница просмотра документа с предпросмотром:
    - PDF (через `iframe`)
    - изображения, видео, аудио
    - текстовые файлы (`.txt`, `.md`, `.json` и др.)
  - Страница управления пользователями (для роли ADMIN)

## Технологический стек

### Backend

- **Язык**: Java 22
- **Фреймворк**: Spring Boot 3.5.8
- **Модули Spring**:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-security`
- **Хранение данных**:
  - PostgreSQL (JDBC‑драйвер `org.postgresql:postgresql`)
  - Миграции БД через Flyway (`org.flywaydb:flyway-core`)
- **Хранение файлов**:
  - MinIO (`io.minio:minio`)
  - Генерация presigned‑URL для доступа к файлам
- **Безопасность**:
  - JWT (библиотека `io.jsonwebtoken:jjwt-*`)
  - Собственный `JwtAuthenticationFilter`
  - `AuthService` с хранением refresh‑токенов в таблице `refresh_tokens`
- **Утилиты**:
  - Lombok (геттеры/сеттеры, билдеры)
  - MapStruct (маппинг DTO ↔ Entity)

Основные пакеты бэкенда:

- `controller` — REST‑контроллеры (`AuthController`, `UserController`, `DocumentController`)
- `service` — бизнес‑логика (`AuthService`, `UserService`, `DocumentService`, `MinioService` и др.)
- `model.entity` — JPA‑сущности (`User`, `Document`, `RefreshToken`, базовый `BaseEntity`)
- `model.dto.*` — DTO‑объекты запросов и ответов
- `repository` — Spring Data JPA репозитории
- `config` — конфигурация Spring Security и интеграций (JWT, MinIO и пр.)

### Frontend

- **Фреймворк**: Next.js 14 (App Router)
- **Ядро**: React 18, TypeScript
- **UI**:
  - TailwindCSS
  - Собственный `Sidebar` с навигацией по страницам
  - Таблицы и карточки на тёмной теме
- **HTTP‑клиент**:
  - Axios с общим инстансом (`frontend/lib/api/client.ts`)
  - Интерсепторы:
    - автоматическое подставление `Authorization: Bearer <token>`
    - повтор запроса после 401 с автоматическим `refresh`
- **Состояние и контексты**:
  - `AuthContext` — хранение и обновление токенов, текущего пользователя
  - `DocumentsContext` — список документов, загрузка/обновление/архивация/скачивание
  - `UserContext` — работа с конкретным пользователем
- **Хранилище в браузере**:
  - `StorageService` — обёртка над `localStorage` для токенов и данных пользователя

## Архитектура и поток данных

- **Аутентификация**
  - Пользователь регистрируется или логинится через `/api/auth/register` или `/api/auth/login`
  - Бэкенд создаёт пользователя, генерирует access/refresh токены и сохраняет refresh‑токен в таблице `refresh_tokens`
  - Фронтенд сохраняет токены и пользователя в `localStorage` и в контексте `AuthContext`
  - При истечении access‑токена фронтенд прозрачно вызывает `/api/auth/refresh` через Axios‑интерсептор

- **Документы**
  - Метаданные документа хранятся в таблице `documents` (название, путь к файлу, MIME‑тип, размер, автор, статус)
  - Сам файл загружается в MinIO через `MinioService` с уникальным именем (`UUID + оригинальное имя`)
  - Для отображения/скачивания документа используется:
    - прямое скачивание файла (`/api/documents/{id}/download`)
    - presigned‑URL (`/api/documents/{id}/url`), который фронтенд использует для предпросмотра

- **Права доступа**
  - Доступ к административным действиям ограничен ролями и аннотациями `@PreAuthorize`
  - USER может работать со своими документами (валидируется в `DocumentValidator` и `UserValidator`)
  - ADMIN может просматривать всех пользователей, блокировать/активировать их и управлять пользователями

## Настройка окружения

### Backend (`backend/src/main/resources/application.yaml`)

Файл содержит конфигурацию для:

- **PostgreSQL**
  - `spring.datasource.url=jdbc:postgresql://<host>:<port>/<db>`
  - `spring.datasource.username=<username>`
  - `spring.datasource.password=<password>`
  - `spring.jpa.properties.hibernate.default_schema=knowledge_base`
- **Flyway**
  - `spring.flyway.schemas=knowledge_base`
  - `spring.flyway.locations=classpath:db/migration`
- **MinIO**
  - `minio.url=http://localhost:9000`
  - `minio.access-key=<MINIO_ACCESS_KEY>`
  - `minio.secret-key=<MINIO_SECRET_KEY>`
  - `minio.bucket-name=user-files`
- **JWT**
  - `jwt.access-secret`, `jwt.refresh-secret`
  - `jwt.access-expiration`, `jwt.refresh-expiration`

> **Рекомендация:** вынести чувствительные данные (пароли, JWT‑секреты, MinIO‑ключи) в переменные окружения/`application-*.yaml`, не хранить реальные значения в Git.

### Frontend (`frontend`)

- В `next.config.mjs` настроен `rewrite`:
  - все запросы на `/api/*` с фронтенда проксируются на `http://localhost:8080/api/*`
- В `lib/api/client.ts`:
  - `baseURL` берётся из `NEXT_PUBLIC_API_BASE_URL` или пустая строка (`''`)

Варианты конфигурации:

1. **Через `rewrite` (по умолчанию):**
   - не указывать `NEXT_PUBLIC_API_BASE_URL`
   - фронтенд делает запросы на `/api/...`, Next.js проксирует их на бэкенд
2. **Через прямой baseURL:**
   - создать `.env.local` в `frontend`:
     ```env
     NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
     ```
   - тогда запросы пойдут напрямую на бэкенд без прокси

## Запуск проекта локально

### Предварительные требования

- Установлен **JDK 22+**
- Установлен **Node.js 18+** и **npm**
- Запущен сервер **PostgreSQL**
- Запущен **MinIO** (локально или в Docker)

### 1. Запуск backend

```bash
cd backend
# Windows
gradlew.bat bootRun

# Unix-подобные системы
./gradlew bootRun
```

По умолчанию бэкенд стартует на `http://localhost:8080`.

### 2. Запуск frontend

```bash
cd frontend
npm install
npm run dev
```

По умолчанию фронтенд будет доступен на `http://localhost:3000`.

## Краткое API‑описание

### Auth (`/api/auth`)

- `POST /api/auth/register` — регистрация пользователя
- `POST /api/auth/login` — аутентификация пользователя
- `POST /api/auth/refresh` — обновление access‑токена по refresh‑токену

### Users (`/api/users`)

- `POST /api/users` — создание пользователя (ADMIN)
- `GET /api/users` — список всех пользователей (ADMIN)
- `GET /api/users/{id}` — получение пользователя по ID (аутентифицированный пользователь)
- `PUT /api/users/{id}` — обновление пользователя (ADMIN или владелец)
- `POST /api/users/{id}/suspend` — блокировка пользователя (ADMIN)
- `POST /api/users/{id}/activate` — активация пользователя (ADMIN)

### Documents (`/api/documents`)

- `POST /api/documents` — загрузка документа (multipart/form-data, `file`)  
- `GET /api/documents?status=PUBLISHED` — список документов по статусу
- `GET /api/documents/{id}` — подробная информация о документе
- `PUT /api/documents/{id}` — обновление метаданных/файла
- `POST /api/documents/{id}/archive` — архивирование документа
- `GET /api/documents/{id}/download` — скачивание файла
- `GET /api/documents/{id}/url` — получение временной ссылки на файл

## Оценка проекта и возможные улучшения

- **Что сделано хорошо**
  - Чистое разделение слоёв: контроллеры / сервисы / репозитории / валидаторы / мапперы
  - Использование Flyway для управления схемой БД
  - Хранение refresh‑токенов в БД и защита от гонок при обновлении токена
  - Аккуратная интеграция с MinIO и использование presigned‑URL
  - Фронтенд с контекстами (`AuthContext`, `DocumentsContext`) и централизованной работой с API

- **Зоны для улучшения**
  - Вынести реальные секреты и пароли из `application.yaml` в переменные окружения
  - Добавить README для бэкенд‑/фронтенд‑подпроектов с отдельными деталями (при необходимости)
  - Доработать навигацию / страницы (`/documents` в сайдбаре пока не реализована как отдельный маршрут)
  - Добавить тесты (юнит/интеграционные) и пример коллекции запросов (Bruno/Postman) в публичном виде без чувствительных данных

