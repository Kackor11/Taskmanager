# CHECKLIST.md

## Projekt

Task Manager – aplikacja wieloserwisowa uruchamiana przy pomocy Docker Compose.

Usługi:

* Nginx (reverse proxy)
* Spring Boot Backend
* PostgreSQL
* Redis

---

# Architektura

```text
                HTTP :80
                    │
                    ▼
              ┌─────────┐
              │  Nginx  │
              └────┬────┘
                   │
             web_network
                   │
                   ▼
          ┌────────────────┐
          │ Spring Backend │
          └───────┬────────┘
                  │
           internal_network
          ┌───────┴────────┐
          ▼                ▼
   ┌──────────┐     ┌──────────┐
   │PostgreSQL│     │  Redis   │
   └──────────┘     └──────────┘
```

---

# Usługi i porty

| Usługa  | Funkcja       | Port kontenera | Port hosta |
| ------- | ------------- | -------------- | ---------- |
| nginx   | Reverse Proxy | 80             | 80         |
| backend | REST API      | 8080           | brak       |
| db      | PostgreSQL    | 5432           | brak       |
| cache   | Redis         | 6379           | brak       |

Baza danych oraz Redis nie są wystawione na hosta.

Cały ruch zewnętrzny przechodzi przez Nginx.

---

# Sieci

Projekt wykorzystuje dwie sieci Docker:

### web_network

Usługi:

* nginx
* backend

Przeznaczenie:

* ruch HTTP z hosta do aplikacji

### internal_network

Usługi:

* backend
* db
* cache

Przeznaczenie:

* komunikacja wewnętrzna pomiędzy backendem, PostgreSQL oraz Redis

---

# Wolumeny

Baza danych wykorzystuje named volume:

```bash
docker volume ls
```

Oczekiwany wynik:

widoczny wolumen PostgreSQL (np. `task-manager_db_data`).

Dane pozostają zachowane po:

```bash
docker compose down
docker compose up -d
```

bez użycia flagi `-v`.

---

# Konfiguracja i sekrety

Projekt wykorzystuje:

* `.env.example`
* Docker Secrets

Przed uruchomieniem należy utworzyć plik:

```text
dbpassword.txt
```

z hasłem użytkownika PostgreSQL.

Przykład:

```text
postgres123
```

Hasło nie jest zapisane w kodzie źródłowym ani w Dockerfile.

---

# Obrazy

Obrazy aplikacyjne budowane są z własnych Dockerfile.

Backend:

* multi-stage build
* uruchamianie jako użytkownik non-root

Przykładowe tagi:

```text
task-manager-backend:1.0.0
task-manager-nginx:1.0.0
```

---

# Uruchomienie projektu

## 1. Utworzenie pliku z hasłem

W katalogu głównym projektu:

```bash
echo "postgres123" > dbpassword.txt
```

lub utworzenie pliku ręcznie.

---

## 2. Budowanie i uruchomienie

```bash
docker compose up -d --build
```

---

## 3. Sprawdzenie konfiguracji Compose

```bash
docker compose config
```

Polecenie powinno zakończyć się bez błędów.

---

## 4. Sprawdzenie uruchomionych usług

```bash
docker compose ps
```

Oczekiwany wynik:

* nginx – Up
* backend – Up (healthy)
* db – Up (healthy)
* cache – Up (healthy)

---

# Test funkcjonalny

## 1. Healthcheck

```bash
curl http://localhost/health
```

Oczekiwany wynik:

```text
OK
```

lub kod HTTP:

```text
200 OK
```

---

## 2. Dodanie zadania

```bash
curl -X POST http://localhost/api/tasks \
-H "Content-Type: application/json" \
-d '{"title":"Sprawdzic projekt Dockera","completed":false}'
```

Przykładowy wynik:

```json
{
  "id": 1,
  "title": "Sprawdzic projekt Dockera",
  "completed": false
}
```

---

## 3. Pobranie listy zadań

```bash
curl http://localhost/api/tasks
```

Przykładowy wynik:

```json
[
  {
    "id": 1,
    "title": "Sprawdzic projekt Dockera",
    "completed": false
  }
]
```

---

## 4. Pobranie pojedynczego zadania

```bash
curl http://localhost/api/tasks/1
```

---

## 5. Usunięcie zadania

```bash
curl -X DELETE http://localhost/api/tasks/1
```

---

# Test trwałości danych

## 1. Dodanie rekordu

```bash
curl -X POST http://localhost/api/tasks \
-H "Content-Type: application/json" \
-d '{"title":"Test trwałości","completed":false}'
```

---

## 2. Restart środowiska

```bash
docker compose down
docker compose up -d
```

---

## 3. Weryfikacja danych

```bash
curl http://localhost/api/tasks
```

Oczekiwany wynik:

rekord dodany przed restartem nadal znajduje się w bazie danych.

---

# Test działania Redis

Wykonać kilkukrotnie:

```bash
curl http://localhost/api/tasks
```

Następnie sprawdzić logi backendu:

```bash
docker compose logs backend
```

Oczekiwany wynik:

komunikat:

```text
Pobieram z PostgreSQL (Brak w cache!)
```

pojawia się tylko przy pierwszym odczycie.

Kolejne odczyty są realizowane z Redis.

---

# Healthchecki

Skonfigurowane dla:

* backend
* db
* cache

Backend uruchamiany jest po uzyskaniu statusu healthy przez PostgreSQL i Redis.

---

# Wymagania dodatkowe

Zrealizowane elementy dodatkowe (+6% do oceny):

* limity CPU i pamięci dla usług aplikacyjnych (sekcja `deploy.resources.limits` dla backendu)
* graceful shutdown (`SIGTERM`, `stop_grace_period` we flagach uruchomieniowych Javy i Dockerfile)

---

# Zatrzymanie środowiska

```bash
docker compose down
```

Usunięcie również wolumenów:

```bash
docker compose down -v
```
