# TaskManager: Cloud-Native OIDC Secured Platform

Rozproszona aplikacja webowa typu Single Page Application (SPA) zintegrowana z architekturą mikroserwisów, demonstrująca nowoczesne wzorce bezpieczeństwa (OAuth 2.0, OIDC, PKCE) oraz konteneryzację.

System zarządza zadaniami i spotkaniami (CRUD), implementując pełną izolację danych użytkowników oraz system kontroli dostępu oparty na rolach (RBAC). Głównym naciskiem w projekcie jest bezpieczna autoryzacja bezstanowa z wykorzystaniem zewnętrznego dostawcy tożsamości (Zitadel).

## 🚀 Technologie i Architektura

Projekt został zbudowany z wykorzystaniem nowoczesnego stosu technologicznego, z podziałem na specjalistyczne kontenery:

*   **Backend:** Java 21, Spring Boot 3 (OAuth2 Resource Server, Spring Data JPA)
*   **Frontend:** React (Vite), `react-oidc-context`
*   **Identity Provider (IdP):** Zitadel (alternatywa dla Keycloak, napisana w Go)
*   **Bazy danych:** PostgreSQL 15 (niezależne instancje dla aplikacji i IdP)
*   **Cache:** Redis
*   **Infrastruktura:** Docker, Docker Compose, Nginx (API Gateway / Reverse Proxy)
*   **CI/CD:** GitHub Actions (MockMvc, H2, Docker build verification)

## 🔐 Architektura Bezpieczeństwa (OIDC & PKCE)

Aplikacja wdraża rygorystyczne standardy bezpieczeństwa dla klientów publicznych (SPA) i poufnych (Backend):

1.  **Proof Key for Code Exchange (PKCE):** Aplikacja React (Public Client) nie przechowuje `client_secret`. Zamiast tego wykorzystuje Authorization Code Flow z PKCE. Generuje `Code Verifier` i `Code Challenge` w przeglądarce, chroniąc przepływ przed atakami typu *Authorization Code Interception*.
2.  **Stateless Resource Server:** Backend Spring Boot nie posiada własnego mechanizmu logowania ani sesji. Weryfikuje jedynie asymetryczne podpisy kryptograficzne tokenów JWT (Access Tokens) za pomocą kluczy publicznych (JWKS) pobieranych z serwera Zitadel.
3.  **Role-Based Access Control (RBAC):** Dostęp do specyficznych zasobów globalnych chroniony jest przez filtry Spring Security (`hasAuthority`), które weryfikują zagnieżdżone claimy (uprawnienia) wyciągane bezpośrednio z tokena JWT.
4.  **Bezpieczeństwo Sieciowe Dockera:**
    *   Stworzono odseparowane sieci: `web_net` (publiczna) oraz `internal_net` (prywatna).
    *   Bazy PostgreSQL oraz Redis nie wystawiają portów na zewnątrz maszyny hosta – są dostępne wyłącznie dla backendu.
    *   Problem nagłówków `Host` przy pobieraniu kluczy JWKS przez wewnętrzną sieć Dockera został profesjonalnie rozwiązany za pomocą tunelowania ruchu przez własny kontener Nginx.

## ⚙️ CI/CD Pipeline

Projekt posiada w pełni zautomatyzowany rurociąg CI/CD skonfigurowany w GitHub Actions, który uruchamia się przy każdym pushu na główną gałąź:
*   Wydzielony profil testowy wstrzykuje bazę danych in-memory (**H2**) w celu uniezależnienia testów jednostkowych i integracyjnych od infrastruktury Dockerowej.
*   Wykonanie zestawu testów bezpieczeństwa API za pomocą **MockMvc** (symulacja żądań z różnymi wariantami tokenów JWT).
*   Weryfikacja procesu budowania obrazów (*Multi-stage Docker builds*).

## 📦 Uruchomienie lokalne

Środowisko zostało w pełni skonteneryzowane i nie wymaga instalacji Javy, Node.js ani baz danych na maszynie hosta.

### Wymagania
* Zainstalowany Docker i Docker Compose.
* Wolne porty: `80` (Nginx/App), `8081` (Zitadel UI), `8080`, `5432`, `6379`.