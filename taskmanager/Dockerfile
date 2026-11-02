# ==========================================
# STAGE 1: Budowanie (Kompilacja kodu)
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiowanie plików projektu
COPY . .

# Uruchomienie kompilacji z pominięciem testów
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Uruchamianie (Lekki obraz produkcyjny)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

# Utworzenie grupy i użytkownika bez uprawnień root ze względów bezpieczeństwa
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Kopiowanie skompilowanego pliku JAR z etapu budowania (multi-stage)
COPY --from=build /app/target/*.jar app.jar

# Zmiana kontekstu na bezpiecznego użytkownika
USER appuser

# Deklaracja nasłuchu na porcie 8080
EXPOSE 8080

# Definicja punktu wejścia dla kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]