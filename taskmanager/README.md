# Task Manager – aplikacja wieloserwisowa Docker Compose

Projekt wykonany w ramach przedmiotu Technologie Chmurowe.

Aplikacja udostępnia prosty zasób biznesowy **Task** oraz prezentuje architekturę wieloserwisową opartą o Docker i Docker Compose.

## Architektura

System składa się z czterech usług:

| Usługa  | Technologia             | Rola                                  |
| ------- | ----------------------- | ------------------------------------- |
| nginx   | Nginx Alpine            | Reverse Proxy                         |
| backend | Spring Boot 3 / Java 21 | Logika aplikacji i REST API           |
| db      | PostgreSQL 15           | Trwałe przechowywanie danych          |
| cache   | Redis                   | Pamięć podręczna dla operacji odczytu |

### Przepływ ruchu

```text
Użytkownik
    |
    v
Nginx (80)
    |
    v
Backend (8080)
    |
    +------> PostgreSQL
    |
    +------> Redis
```

## Funkcjonalność aplikacji

Dostępne endpointy:

| Metoda | Endpoint        | Opis                        |
| ------ | --------------- | --------------------------- |
| GET    | /health         | Sprawdzenie stanu aplikacji |
| GET    | /api/tasks      | Lista zadań                 |
| POST   | /api/tasks      | Dodanie zadania             |
| GET    | /api/tasks/{id} | Pobranie zadania            |
| DELETE | /api/tasks/{id} | Usunięcie zadania           |

## Wymagania

Przed uruchomieniem wymagane są:

* Docker
* Docker Compose

Sprawdzenie wersji:

```bash
docker --version
docker compose version
```

## Uruchomienie projektu

1. Utworzyć plik:

```text
dbpassword.txt
```

w katalogu głównym projektu.

2. Wpisać hasło do bazy danych.

Przykład:

```text
postgres123
```

3. Uruchomić środowisko:

```bash
docker compose up -d --build
```

4. Sprawdzić status usług:

```bash
docker compose ps
```

## Konfiguracja Docker

### Sieci

Projekt wykorzystuje dwie sieci:

| Sieć         | Przeznaczenie                            |
| ------------ | ---------------------------------------- |
| web_net      | ruch nginx ↔ backend                     |
| internal_net | komunikacja backend ↔ PostgreSQL ↔ Redis |

### Wolumeny

Named volume:

```text
db_data
```

służy do przechowywania danych PostgreSQL.

### Sekrety

Hasło bazy danych przekazywane jest przy użyciu Docker Secrets:

```text
db_password
```

i montowane do:

```text
/run/secrets/db_password
```

### Healthcheck

Zdefiniowano healthcheck dla:

* backend
* PostgreSQL
* Redis

Backend uruchamia się dopiero po uzyskaniu statusu healthy przez PostgreSQL oraz Redis.

## Redis Cache

Endpoint:

```text
GET /api/tasks
```

wykorzystuje mechanizm cache oparty o Redis.

Przy pierwszym odczycie dane pobierane są z PostgreSQL.

Kolejne odczyty realizowane są z pamięci Redis.

Dowód działania można sprawdzić:

```bash
docker compose logs backend
```

## Trwałość danych

Dane przechowywane są w wolumenie:

```text
db_data
```

Po wykonaniu:

```bash
docker compose down
docker compose up -d
```

rekordy pozostają dostępne.

## Dockerfile

Backend budowany jest przy użyciu własnego Dockerfile.

Zastosowano:

* multi-stage build,
* użytkownika non-root,
* plik `.dockerignore`.

## Dodatkowe elementy

Zrealizowane wymagania dodatkowe:

* limity CPU i pamięci,
* graceful shutdown,
* Docker Secrets.

## Weryfikacja projektu

Szczegółowe procedury testowe znajdują się w pliku:

```text
CHECKLIST.md
```
