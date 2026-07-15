# Report Service (Сервис формирования отчётов)

Сервис для генерации отчёта по случаям оказания медицинской помощи в формате Excel (.xlsx) с возможностью скачивания самого файла или ZIP‑архива с ним.


## Сборка и запуск
- Перейти в корень проекта

### Docker

```bash
#запуск
docker compose up -d
```


```bash
#остановить
docker compose down -v
```
___
### Вручную

```bash
cd frontend
npm install
npm run build
cp -r ./build/* ../src/main/resources/static
cd ../
mvn clean package
java -jar target/report-service-0.0.1-SNAPSHOT.jar
```


### После запуска приложение будет доступно по адресу: `http://localhost:8080`

___
## Доступные эндпоинты

| Метод | Путь                           | Описание                                                      |
|-------|--------------------------------|---------------------------------------------------------------|
| GET   | `/api/v1/report/download/file` | Скачать отчёт в формате Excel (файл report.xlsx)              |
| GET   | `/api/v1/report/download/zip`  | Скачать ZIP‑архив, содержащий report.xlsx (файл report.zip)   |

#### Например:
- http://localhost:8080/api/v1/report/download/file
- http://localhost:8080/api/v1/report/download/zip

## Запуск тестов

```bash
mvn clean test
```

## Clean

```bash
mvn clean
cd frontend && npm run clean
```
