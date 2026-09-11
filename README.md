# QA Portfolio — Autotests

**Автор:** Роман Шкляев  
**Цель:** Демонстрация навыков тестирования для позиции QA Engineer

---

## Что здесь

Два слоя тестов: учебный (публичные демо-API/демо-магазин — база стека и
широта покрытия) и производственный (мои собственные живые сервисы —
показывает автотесты против настоящего прода, а не тренажёра).

### Производственный слой — тесты против собственных живых сервисов
- **9 контрактных тестов (Rest Assured)** против бэкенда одного из моих
  боевых сервисов (домен намеренно не публикуется — хранится только в
  секрете CI `SLUSHAYU_BASE_URL`, локально без него этот пакет
  пропускается через `@EnabledIfEnvironmentVariable`, а не падает):
  healthcheck-роутинг, контракт вебхука ЮKassa (пустое тело, битый JSON,
  неизвестное событие, сфабрикованный `payment.succeeded`), авторизация
  `/state`. Тестирование нашло два реальных дефекта прод-сервиса —
  `BUG-007` и `BUG-008` в `bug-reports.md`.
- **9 UI-тестов (Selenium)** против живого продукта
  [do-offera.github.io](https://do-offera.github.io) (конструктор резюме):
  заполнение формы и живой предпросмотр, прогресс-бар готовности, тег-инпут
  навыков, cookie-баннер и `localStorage`, диалоги подтверждения (`confirm`)
  на сброс формы, проверка внешней ссылки на реальный контакт.

### API-тесты (Rest Assured + JUnit 5) — учебный слой
- **34 теста** для REST API JSONPlaceholder
- CRUD-операции: GET, POST, PUT, PATCH, DELETE
- Параметризованные тесты (`@ParameterizedTest`)
- Проверки структуры ответа, типов данных, валидации
- Граничные значения и негативные сценарии
- Проверка времени ответа (SLA)

### UI-тесты (Selenium WebDriver + Page Object) — учебный слой
- **27 тестов** для интернет-магазина SauceDemo
- Page Object Model (4 страницы: Login, Catalog, Cart, Checkout)
- E2E-сценарий: Логин → Каталог → Корзина → Checkout → Завершение
- Негативные сценарии логина (6 вариантов)
- Сортировка товаров, добавление/удаление из корзины
- Валидация форм Checkout

### Тестовая документация
- **224 тест-кейса** (покрытие требований > 90%)
- **8 баг-репортов** в формате Jira — 2 из них найдены автотестами против
  собственного прод-сервиса, не придуманы для демонстрации
- Приоритезация: Critical/High (85), Medium (95), Low (44)

### CI/CD
- GitHub Actions: автоматический запуск API и UI тестов
- Allure-репортинг
- Артефакты (Allure-results)
- Мультиплатформенный запуск

---

## Стек технологий

| Компонент | Технология |
|-----------|-----------|
| Язык | Java 17 |
| Тест-фреймворк | JUnit 5 |
| API-тестирование | Rest Assured 5.4 |
| UI-тестирование | Selenium 4.18 |
| Управление драйверами | WebDriverManager 5.7 |
| Паттерн | Page Object Model |
| Отчётность | Allure 2.27 |
| Сборщик | Maven |
| CI/CD | GitHub Actions |
| Трекинг | Jira |

---

## Как запустить

### Локально

```bash
# Клонировать репозиторий
git clone https://github.com/VVladimirPetrosyan/qa-portfolio.git
cd qa-portfolio

# API-тесты
mvn test -Papi-tests

# UI-тесты (нужен Chrome)
mvn test -Pui-tests

# Все тесты
mvn test

# Allure-отчёт
mvn allure:serve
```

### CI/CD
Тесты запускаются автоматически при пуше в `main` или `develop`.
Прод-слой `api/slushayu` запускается только там, где задана переменная
окружения `SLUSHAYU_BASE_URL` (в CI — секрет репозитория, не виден
форк-PR по умолчанию в GitHub Actions); без неё локально этот пакет тихо
пропускается — `mvn test` остаётся зелёным.

---

## Структура проекта

```
qa-portfolio/
├── pom.xml                          # Maven конфигурация
├── .github/workflows/ci.yml         # GitHub Actions
├── src/
│   ├── main/java/qa/
│   │   ├── pages/                   # Page Objects
│   │   │   ├── LoginPage.java
│   │   │   ├── CatalogPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   └── models/                  # Модели данных
│   │       ├── Post.java
│   │       └── User.java
│   └── test/
│       ├── java/qa/
│       │   ├── api/                 # API-тесты (учебный слой — JSONPlaceholder)
│       │   │   ├── ApiTest.java
│       │   │   ├── UsersApiTest.java
│       │   │   ├── MiscApiTest.java
│       │   │   └── slushayu/        # Прод-слой — контракт живого бэкенда
│       │   │       ├── BaseSlushayuApiTest.java
│       │   │       ├── SlushayuHealthcheckTest.java
│       │   │       ├── SlushayuWebhookTest.java
│       │   │       └── SlushayuStateAuthTest.java
│       │   ├── ui/                  # UI-тесты (учебный слой — SauceDemo)
│       │   │   ├── LoginTest.java
│       │   │   ├── CatalogTest.java
│       │   │   ├── CartTest.java
│       │   │   └── dooffera/        # Прод-слой — живой do-offera.github.io
│       │   │       ├── BaseDooferaUiTest.java
│       │   │       ├── DooferaResumeBuilderTest.java
│       │   │       └── DooferaConsentTest.java
│       │   └── base/                # Базовые классы (учебный слой)
│       │       ├── BaseApiTest.java
│       │       └── BaseUiTest.java
│       └── resources/testdata/
│           └── users.json           # Тестовые данные
└── 📄 test-cases.md                     # 224 тест-кейса
📄 bug-reports.md                    # 6 баг-репортов
```

---

## Покрытие требований (QA Engineer)

| Требование вакансии | Покрытие |
|----------------------|----------|
| Опыт работы в тестировании от 2 лет | Проекты + сертификаты Zerocoder |
| Написание автотестов на Java | API и UI тесты (61 тест) |
| Jira, Test IT | Баг-репорты в формате Jira |
| Тест-дизайн | 224 тест-кейса с приоритизацией |
| Git | GitHub-репозиторий |
| CI/CD | GitHub Actions |

---

## Все тесты (сводка)

| Модуль | Тестов | Покрытие |
|--------|--------|----------|
| API: Posts | 12 | CRUD + edge cases |
| API: Users | 8 | Структура + валидация |
| API: Misc | 14 | Comments, Albums, Todos, Performance |
| UI: Login | 10 | Позитив + негатив |
| UI: Catalog | 13 | Отображение, сортировка, корзина |
| UI: Cart & Checkout | 14 | E2E + валидация |
| **API: Слушаю (прод)** | **9** | Healthcheck-роутинг, контракт вебхука ЮKassa, auth `/state` |
| **UI: до оффера. (прод)** | **9** | Форма и превью резюме, прогресс, теги, cookie-consent |
| **Итого** | **79** | |

---

## Контакты

- **GitHub:** https://github.com/VVladimirPetrosyan/qa-portfolio
