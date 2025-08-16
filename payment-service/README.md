# Приложение payment-service
## Версия: v3.0
Модуль, разработанный в рамках проектной рабты представляет из себя Сервис платежей.
Приложение предоставляет возможность получения фиатного баланса и проведения платежа.

## Генерация README.md
Для генерации README.md необходимо выполнить команду `./gradlew generateReadme`

## Сборка
Для сбоки необходимо в корне проекта выполнить команду    `./gradlew clean bootJar`

После этого jar-файл будет собран и расположен по пути `./build/libs`

## Запуск
### Unix-подобные системы
#### Запуск
Выполнить команду `./build/libs/payment-service-v3.0.jar start`

#### Остановка
Выполнить команду `./build/libs/payment-service-v3.0.jar stop`

### Windows
#### Запуск
Выполнить команду `./build/libs/start.bat start.bat`

> [!WARNING]
> Можно также запустить напрямую исполняемый jar-файл командой `./build/libs/payment-service-v3.0.jar`,
> Однако остановка будет возможна только последовательным выполнением команд
> 1. `jps -lv | findstr /i payment-service`
> 2. `taskkill /PID <PID> /f`

#### Остановка
Выполнить команду `./build/libs/stop.bat` (либо вручную открыть файл)

### Из исходного кода
#### Windows
Выполнить команду `./gradlew :payment-service:bootRun`

#### Unix-подобные системы `./gradlew :payment-service:bootRun`

### В Intellij IDEA
Открыть Run/Debug Configurations, добавить в конфигурацию запуска Environment variables с актуальным для запускаемой конфигурации содержимым
`DB_HOST=<DB_HOST>;DB_PORT=<DB_PORT>;DB_NAME=<DB_NAME>;POSTGRES_USER=<POSTGRES_USER>;POSTGRES_PASSWORD=<POSTGRES_PASSWORD>;REDIS_HOST=<REDIS_HOST>;REDIS_PORT=<REDIS_PORT>;PAYMENT_SERVICE_HOST=<PAYMENT_SERVICE_HOST>;PAYMENT_SERVICE_PORT=<PAYMENT_SERVICE_PORT>`

> [!NOTE]
>После запуска любым из вышеописанных способов API будет доступно по адресу http://localhost:8080/payment/<METHOD_PATH>

### Тестирование
Для запуска тестов необходимо выполнить команду `./gradlew test`