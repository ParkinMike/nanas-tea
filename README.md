# nanas-tea
A small project to show if tea is on at nanas tonight.

## Run locally

```powershell
$env:APP_PASSCODE="your-secure-passcode"
$env:CLEAR_NANAS_SCHEDULE="false"
mvn spring-boot:run
```

Local runs default to an in-memory H2 database, so no `DB_URL` is required. If you want to point the app at Postgres instead, set `DB_URL` (and `DB_USERNAME` / `DB_PASSWORD` if needed) before starting it.

## Build Docker image

```powershell
docker build -t nanas-tea .
```

## Run Docker image

```powershell
docker run --rm -p 8080:8080 `
  -e APP_PASSCODE="your-secure-passcode" `
  -e DB_URL="jdbc:postgresql://..." `
  -e DB_USERNAME="..." `
  -e DB_PASSWORD="..." `
  -e CLEAR_NANAS_SCHEDULE="false" `
  nanas-tea
```
