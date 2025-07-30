# Приложение intershop 
## Версия: v2.0
Приложение представляет из себя полукастомный полумагазин, разработанный в рамках проектной рабты.
Приложение предоставляет возможность просмотра товаров, формирования корзины, оформления заказ, а также загрузки новых товаров из условного интерфейса администратора.

## Генерация README.md
Для генерации README.md необходимо выполнить команду `./gradlew generateReadme`

## Сборка
Для сбоки необходимо в корне проекта выполнить команду    `./gradlew clean bootJar`

После этого jar-файл будет собран и расположен по пути `./build/libs`

## Запуск
### Unix-подобные системы
#### Запуск
Выполнить команду `./build/libs/intershop-v2.0.jar start`

#### Остановка
Выполнить команду `./build/libs/intershop-v2.0.jar stop`

### Windows
#### Запуск
Выполнить команду `./build/libs/start.bat start.bat <DB_HOST> <DB_PORT> <DB_NAME> <POSTGRES_USER> <POSTGRES_PASSWORD>`

> [!WARNING]
> Можно также запустить напрямую исполняемый jar-файл командой `./build/libs/intershop-v2.0.jar`, предварительно задав переменные среды
> 1.  `$env:DB_HOST="<DB_HOST>"`
> 2.  `$env:DB_PORT="<DB_PORT>"`
> 3.  `$env:DB_NAME="<DB_NAME>"`
> 4.  `$env:POSTGRES_USER="<POSTGRES_USER>"`
> 5.  `$env:POSTGRES_PASSWORD="<POSTGRES_PASSWORD>"`
> 
> Однако остановка будет возможна только последовательным выполнением команд
> 1. `jps -lv | findstr /i intershop`
> 2. `taskkill /PID <PID> /f`

#### Остановка
Выполнить команду `./build/libs/stop.bat` (либо вручную открыть файл)

### Docker
#### Запуск
В дирректории проекта выполнить команду `docker-compose up`

#### Остановка
В дирректории проекта выполнить команду `docker-compose stop`

### Из исходного кода
#### Windows
Выполнить команды 

    $env:DB_HOST="<DB_HOST>"
    $env:DB_PORT="<DB_PORT>"
    $env:DB_NAME="<DB_NAME>"
    $env:POSTGRES_USER="<POSTGRES_USER>"
    $env:POSTGRES_PASSWORD="<POSTGRES_PASSWORD>"
    ./gradlew bootRun

#### Unix-подобные системы

    export DB_HOST=<DB_HOST>
    export DB_PORT=<DB_PORT>
    export DB_NAME=<DB_NAME>
    export POSTGRES_USER=<POSTGRES_USER>
    export POSTGRES_PASSWORD=<POSTGRES_PASSWORD>
    ./gradlew bootRun

### В Intellij IDEA
Открыть Run/Debug Configurations, добавить в конфигурацию запуска Environment variables с актуальным для запускаемой конфигурации содержимым
`DB_HOST=<DB_HOST>;DB_PORT=<DB_PORT>;DB_NAME=<DB_NAME>;POSTGRES_USER=<POSTGRES_USER>;POSTGRES_PASSWORD=<POSTGRES_PASSWORD>`

> [!NOTE]
>После запуска любым из вышеописанных способов приложение будет доступно по адресу http://localhost:9191/intershop

### Тестирование
Для запуска тестов необходимо выполнить команду `./gradlew test`


## Загрузка товаров
Для загрузки новых товаров в базу необходимо перейти по ссылке http://localhost:9191/intershop/admin