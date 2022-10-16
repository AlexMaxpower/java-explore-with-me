# Приложение Explore With Me (дипломный проект)

Explore With Me — приложение, которое дает возможность делиться информацией об интересных событиях и помогает найти компанию для участия в них.

## Основные возможности приложения

* Мероприятия - Создание событий с различными параметрами (платные/бесплатные, место проведения, дата, количество участников, категория и т.д.).
* Заявки на посещение мероприятия - Можно выбирать события и оставлять заявку на посещение.
* Доступ к информации - Разные возможности по редактированию и получению информации для пользователей и администраторов, модерация событий, фильтрация по параметрам (дата проведения, категория и т.д.)
* Подборки событий - Администраторы могут группировать события по каким-либо признакам, а пользователи знакомиться с подборками.
* Сбор статистики по просмотрам событий для выбора самых популярных.

## Архитектура

Приложение состоит из 2 сервисов:

* Основной сервис — содержит всё необходимое для работы с событиями
* Сервис статистики — хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

## Инструкция по запуску приложения
Для корректной работы приложения необходимо наличие на компьютере Docker. Оба сервиса запускаются следующей командой:

```Bash
mvn install
docker-compose up
```

## Спецификация API

API основного сервиса разделено на три части. Первая — публичная, доступна без регистрации любому пользователю сети. Вторая — закрытая, доступна только авторизованным пользователям. Третья — административная, для администраторов сервиса.

- [API основного сервиса](https://github.com/AlexMaxpower/java-explore-with-me/blob/main/ewm-main-service-spec.json)
- [API сервиса по сбору статистики](https://github.com/AlexMaxpower/java-explore-with-me/blob/main/ewm-stats-service-spec.json)

Для просмотра спецификаций используйте [редактор Swagger](https://editor-next.swagger.io/).

## Используемые технологии

* Spring Boot 2.7.3
* Maven
* MapStruct 1.5.2
* Lombok
* OpenFeign
* Hibernate ORM
* PostgreSQL 13.7
* Docker

## Спецификация дополнительной возможности
### Public: Комментарии

<details>
  <summary> GET /comments <br />
     Получение комментариев с возможностью фильтрации
</summary>

* это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные
комментарии
* текстовый поиск должен быть без учета регистра букв
* сортировка должна быть по времени создания комментария

```
    text – для поиска в комментариях
    events – список идентификаторов событий, для которых будет вестись поиск комментариев
    rangeStart – поиск только начиная с этого времени размещения комментария
    rangeEnd – поиск комментариев, размещенных не позднее этого времени
    from - количество комментариев, которые нужно пропустить для формирования текущего набора
    size – количество комментариев в наборе
```
</details>

### Private: Комментарии
<details>
  <summary> PUT /users/{userId}/comments <br />
     Размещение комментария пользователем
</summary>
Пример запроса:

```json
{
  "text": "Новый комментарий длинной больше 20 символов",
  "event": 1
}
```
Пример ответа:
```json
{
"id": 1,
"text": "Новый комментарий длинной больше 20 символов",
"created": "2022-10-15 07:56:45",
"status": "PENDING",
"event": 1,
"commentator": 1
}
```
</details>  
<details>
  <summary> PATCH /users/{userId}/comments/{commentId}/cancel <br />
     Снятие с публикации комментария
</summary>

Пример ответа:

```json
{
 "id": 1,
 "text": "Новый комментарий длинной больше 20 символов",
 "created": "2022-10-15 07:51:36",
 "status": "CANCELED",
 "event": 1,
 "commentator": 1
}
```
</details> 
<details>
  <summary> PATCH /users/{userId}/comments <br />
     Редактирование комментария
</summary>
Пример запроса:

```json
{
 "id": 1,
 "text": "Отредактированный комментарий длинной больше 20 символов"
}
```
</details> 
<details>
  <summary> GET /users/{userId}/comments <br />
     Получение всех комментариев пользователя
</summary>
</details> 

### Admin: Комментарии
<details>
  <summary> PATCH /admin/comments/{commentId}/publish <br />
     Одобрение комментария и публикация
</summary>

Пример ответа:
```json
{
"id": 1,
"text": "Новый комментарий длинной больше 20 символов",
"created": "2022-10-15 08:56:36",
"status": "CONFIRMED",
"event": 1,
"commentator": 1
}
```
</details> 
<details>
  <summary> PATCH /admin/comments/{commentId}/reject <br />
     Отклонение комментария и снятие с публикации
</summary>

Пример ответа:
```json
{
"id": 1,
"text": "Новый комментарий длинной больше 20 символов",
"created": "2022-10-15 09:12:52",
"status": "REJECTED",
"event": 1,
"commentator": 1
}
```
</details>
<details>
  <summary> PATCH /admin/comments <br />
     Редактирование комментария
</summary>
Пример запроса:

```json
{
 "id": 1,
 "text": "Отредактированный комментарий длинной больше 20 символов"
}
```
</details>
<details>
  <summary> GET /admin/comments <br />
     Получение комментариев с возможностью фильтрации
</summary>

```
text – для поиска в комментариях
events – список идентификаторов событий, для которых будет вестись поиск комментариев
rangeStart – поиск только начиная с этого времени размещения комментария
rangeEnd – поиск комментариев, размещенных не позднее этого времени
status – получение комментариев с определенным статусом
from - количество комментариев, которые нужно пропустить для формирования текущего набора
size – количество комментариев в набор
```
</details>
