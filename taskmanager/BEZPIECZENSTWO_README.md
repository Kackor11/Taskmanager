# TaskManager – OAuth2 & PKCE Security Project

**Autor:** Kacper Korzekwa

Projekt został wykonany w ramach zaliczenia przedmiotu **Technologie Chmurowe**. Jest to rozwinięcie wcześniej stworzonej aplikacji TaskManager, wzbogacone o mechanizmy bezpieczeństwa oparte na standardzie **OAuth 2.0** oraz **PKCE**. Cała aplikacja działa w środowisku kontenerowym Docker i wykorzystuje serwer autoryzacji Zitadel.

---

# Opis projektu

Aplikacja umożliwia zarządzanie zadaniami oraz spotkaniami użytkowników. Dostęp do zasobów zabezpieczony jest za pomocą tokenów JWT wydawanych przez serwer autoryzacji Zitadel.

Projekt został przygotowany zgodnie z wymaganiami przedmiotu i obejmuje:

* zabezpieczony backend Spring Boot,
* frontend React,
* bazę danych PostgreSQL,
* serwer autoryzacji Zitadel,
* mechanizm OAuth 2.0 z PKCE,
* konteneryzację przy użyciu Dockera.

---

# Uruchomienie projektu

Przed uruchomieniem należy upewnić się, że porty:

```
80
8080
8081
5432
```

nie są zajęte przez inne aplikacje.

W głównym katalogu projektu należy wykonać polecenie:

```bash
docker compose up -d --build
```

Przy pierwszym uruchomieniu trzeba odczekać około 1–2 minut, ponieważ Zitadel inicjalizuje swoją bazę danych.

Status uruchamiania można sprawdzić komendą:

```bash
docker compose logs -f zitadel
```

Po zakończeniu konfiguracji w logach powinien pojawić się komunikat informujący o zakończeniu procesu inicjalizacji.

---

# Logowanie

Frontend aplikacji dostępny jest pod adresem:

```
http://localhost/
```

Panel administracyjny Zitadel:

```
http://localhost:8081/ui/console/
```

Przy pierwszym uruchomieniu tworzone jest konto administratora.

Domyślne dane logowania:

**Login:**

```
zitadel-admin@zitadel.localhost
```

**Hasło:**

```
Password123!
```

Przy pierwszym logowaniu Zitadel wymusi zmianę hasła.

---

# Wykorzystane technologie

## Backend

* Spring Boot
* Spring Security
* OAuth2 Resource Server
* JWT
* JUnit oraz MockMvc

## Frontend

* React
* Vite
* react-oidc-context

## Baza danych

* PostgreSQL

W projekcie wykorzystywane są osobne bazy danych dla aplikacji oraz serwera autoryzacji.

## Cache

* Redis

## Reverse Proxy

* Nginx

## Authorization Server

* Zitadel

## Konteneryzacja

* Docker
* Docker Compose

## CI/CD

* GitHub Actions

---

# Funkcjonalności związane z bezpieczeństwem

W projekcie zaimplementowano:

* OAuth 2.0,
* OpenID Connect,
* Authorization Code Flow,
* PKCE,
* uwierzytelnianie za pomocą JWT,
* zabezpieczone endpointy REST,
* autoryzację opartą o role użytkowników,
* publiczny endpoint `/health`,
* komunikację frontendu z backendem przy użyciu tokenów Bearer.

---

# Spełnione wymagania projektu

Projekt zawiera:

* backend zabezpieczony OAuth 2.0,
* endpoint uwzględniający role użytkowników,
* co najmniej cztery zabezpieczone endpointy,
* publiczny endpoint `/health`,
* frontend korzystający z zabezpieczonego backendu,
* bazę danych PostgreSQL,
* skonfigurowany serwer autoryzacji,
* obsługę PKCE.

Dodatkowo projekt wykorzystuje:

* Docker Compose,
* serwer autoryzacji inny niż Keycloak,
* rozbudowaną logikę biznesową,
* automatyczne testy,
* CI/CD,
* trwałe wolumeny danych.

---

# Jak działa PKCE?

PKCE (Proof Key for Code Exchange) jest dodatkowym zabezpieczeniem dla aplikacji typu SPA, takich jak React.

W praktyce cały proces wygląda następująco:

1. Frontend generuje losowy ciąg znaków zwany **Code Verifier**.

2. Na jego podstawie tworzony jest zaszyfrowany skrót, czyli **Code Challenge**.

3. Użytkownik zostaje przekierowany do logowania w Zitadel, a serwer otrzymuje tylko Code Challenge.

4. Po poprawnym zalogowaniu Zitadel zwraca jednorazowy kod autoryzacyjny.

5. Frontend przesyła ten kod razem z oryginalnym Code Verifier.

6. Jeżeli wartości się zgadzają, Zitadel wydaje token JWT.

Dzięki temu nawet przechwycenie kodu autoryzacyjnego przez osobę trzecią nie pozwala na uzyskanie dostępu do aplikacji.

---

# Architektura projektu

```
React SPA
     |
     | OAuth2 + PKCE
     |
     v
Zitadel
     |
     | JWT
     |
     v
Spring Boot API
     |
     +---- PostgreSQL
     |
     +---- Redis
```

Całość działa w środowisku Docker Compose, a Nginx pełni rolę bramy wejściowej.

---

# Przydatne polecenia

Uruchomienie projektu:

```bash
docker compose up -d --build
```

Zatrzymanie projektu:

```bash
docker compose down
```

Podgląd logów:

```bash
docker compose logs -f
```

Podgląd logów Zitadel:

```bash
docker compose logs -f zitadel
```

Usunięcie wszystkich kontenerów wraz z wolumenami:

```bash
docker compose down -v
```

**Uwaga:** usunięcie wolumenów powoduje utratę danych aplikacji oraz konfiguracji Zitadel.

---

# Podsumowanie

Celem projektu było rozszerzenie istniejącej aplikacji TaskManager o mechanizmy bezpieczeństwa zgodne ze standardem OAuth 2.0. W projekcie wykorzystano serwer autoryzacji Zitadel, mechanizm PKCE oraz tokeny JWT do zabezpieczenia komunikacji pomiędzy frontendem i backendem.

Projekt został przygotowany z myślą o spełnieniu wymagań przedmiotu Technologie Chmurowe oraz zaprezentowaniu praktycznego wykorzystania współczesnych mechanizmów uwierzytelniania i autoryzacji w aplikacjach webowych.

---

**Autor:** Kacper Korzekwa

**Przedmiot:** Bezpieczenstwo Aplikacji Webowych
