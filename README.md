# nanas-tea
A small project to show if tea is on at nanas tonight.

## Run locally

```powershell
$env:APP_PASSCODE="your-secure-passcode"
$env:DB_URL="jdbc:postgresql://..."
$env:CLEAR_NANAS_SCHEDULE="false"
mvn spring-boot:run
```

## Build Docker image

```powershell
docker build -t nanas-tea .
```

## Run Docker image

```powershell
docker run --rm -p 8080:8080 `
  -e APP_PASSCODE="your-secure-passcode" `
  -e DB_URL="jdbc:postgresql://..." `
  -e CLEAR_NANAS_SCHEDULE="false" `
  nanas-tea
```
