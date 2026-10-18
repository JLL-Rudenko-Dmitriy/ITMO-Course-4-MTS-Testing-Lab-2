# Автотесты REST и GraphQL API

## Требования

- JDK 21
- Maven 3.9+ в `PATH`

## Запуск из корня проекта
```powershell
mvn clean test
```
```bash
./gradlew clean test
```

## API и сценарии

- REST: `https://playground.nileslabs.com/api/v1`.
- GraphQL: `https://graphql.postman-echo.com/graphql`.
- Данные login из задания: `Bret / password123`. Готовые токены не хранятся.

| Тест | Что проверяется |
| --- | --- |
| `PostsTest.createUpdateAndGetPost` | POST 201, извлечение id, PATCH 200, GET 200; id, новое название, сохранение body и user_id |
| `AuthTest.loginAndGetCurrentUser` | Login 200, непустой строковый access_token, явный Bearer-заголовок, профиль пользователя |
| `AuthTest.rejectRequestWithoutToken` | Без токена: HTTP 401, JSON-статус и описание ошибки авторизации |
| `GraphQlTest.queryHello` | HTTP 200, data.hello = Hello Ivan, отсутствие errors |
| `GraphQlTest.createPerson` | HTTP 200, непустой строковый id, name = Student, age = 20, отсутствие errors |
| `GraphQlTest.rejectUnknownQueryField` | Дополнительный негативный сценарий: HTTP 200, ошибка неизвестного поля в errors, отсутствие успешных data |

Задание с Postman
![img.png](img.png)