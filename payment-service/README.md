# Приложение payment-service
## Версия: v4.0
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
Выполнить команду `./build/libs/payment-service-v4.0.jar start`

#### Остановка
Выполнить команду `./build/libs/payment-service-v4.0.jar stop`

### Windows
#### Запуск
Выполнить команду `./build/libs/start.bat start.bat`

> [!WARNING]
> Можно также запустить напрямую исполняемый jar-файл командой `./build/libs/payment-service-v4.0.jar`, предварительно задав переменные среды
> 1.  `$env:KEYCLOAK_HOST="<KEYCLOAK_HOST>"`
> 2.  `$env:KEYCLOAK_PORT="<KEYCLOAK_PORT>"`
> 
> Однако остановка будет возможна только последовательным выполнением команд
> 1. `jps -lv | findstr /i payment-service`
> 2. `taskkill /PID <PID> /f`

#### Остановка
Выполнить команду `./build/libs/stop.bat` (либо вручную открыть файл)

### Из исходного кода
#### Windows
Выполнить команды

    $env:KEYCLOAK_HOST="<KEYCLOAK_HOST>"
    $env:KEYCLOAK_PORT="<KEYCLOAK_PORT>"
    ./gradlew :payment-service:bootRun

#### Unix-подобные системы 
Выполнить команды

    export KEYCLOAK_HOST=<KEYCLOAK_HOST>
    export KEYCLOAK_PORT=<KEYCLOAK_PORT>
    ./gradlew :payment-service:bootRun

### В Intellij IDEA
Открыть Run/Debug Configurations, добавить в конфигурацию запуска Environment variables с актуальным для запускаемой конфигурации содержимым
`KEYCLOAK_HOST=<KEYCLOAK_HOST>;KEYCLOAK_PORT=<KEYCLOAK_PORT>`

> [!NOTE]
>После запуска любым из вышеописанных способов API будет доступно по адресу http://localhost:9192/api/<METHOD_PATH>

### Тестирование
Для запуска тестов необходимо выполнить команду `./gradlew test`