# GOLSYSTEM V2 - Script de Prueba de Integración
# Ejecutar con: .\test-integration.ps1

$baseUrl = "http://localhost:8080/api"
$results = @()

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null
    )
    
    $url = "$baseUrl$Endpoint"
    $headers = @{ "Content-Type" = "application/json" }
    
    try {
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 3
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $headers -Body $jsonBody -TimeoutSec 5
        } else {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $headers -TimeoutSec 5
        }
        
        Write-Host "✅ $Name - OK" -ForegroundColor Green
        return @{ Test = $Name; Status = "PASS"; Response = $response }
    }
    catch {
        Write-Host "❌ $Name - FAIL: $($_.Exception.Message)" -ForegroundColor Red
        return @{ Test = $Name; Status = "FAIL"; Error = $_.Exception.Message }
    }
}

Write-Host "`n🚀 INICIANDO PRUEBAS DE INTEGRACIÓN`n" -ForegroundColor Cyan
Write-Host "Base URL: $baseUrl`n" -ForegroundColor Gray

# Test 1: Health Check Torneos
$results += Test-Endpoint -Name "GET Torneos (Listar)" -Method "GET" -Endpoint "/torneos"

# Test 2: Crear Torneo
$torneoBody = @{
    nombre = "Torneo Test PowerShell"
    categoria = "MASCULINO"
    minJugadores = 7
    maxJugadores = 12
    estado = "CONFIGURACION"
}
$torneoResult = Test-Endpoint -Name "POST Torneo (Crear)" -Method "POST" -Endpoint "/torneos" -Body $torneoBody
$results += $torneoResult

$torneoId = $null
if ($torneoResult.Status -eq "PASS" -and $torneoResult.Response.id) {
    $torneoId = $torneoResult.Response.id
}

# Test 3: Crear Equipo sin torneoId
$equipoBody1 = @{
    nombre = "Equipo Sin Torneo"
    codigoEquipo = "ST001"
    logoUrl = "https://example.com/logo.png"
}
$results += Test-Endpoint -Name "POST Equipo (sin torneoId)" -Method "POST" -Endpoint "/equipos" -Body $equipoBody1

# Test 4: Crear Equipo con torneoId (si se creó el torneo)
if ($torneoId) {
    $equipoBody2 = @{
        nombre = "Equipo Con Torneo"
        codigoEquipo = "CT001"
        logoUrl = "https://example.com/logo2.png"
        torneoId = $torneoId
    }
    $results += Test-Endpoint -Name "POST Equipo (con torneoId=$torneoId)" -Method "POST" -Endpoint "/equipos" -Body $equipoBody2
} else {
    Write-Host "⚠️  POST Equipo (con torneoId) - SKIP (No se pudo crear torneo)" -ForegroundColor Yellow
    $results += @{ Test = "POST Equipo (con torneoId)"; Status = "SKIP" }
}

# Test 5: CORS Preflight
Write-Host "`n🌐 Test CORS..." -ForegroundColor Cyan
try {
    $corsResponse = Invoke-WebRequest -Uri "$baseUrl/torneos" -Method OPTIONS -Headers @{
        "Origin" = "http://localhost:5173"
        "Access-Control-Request-Method" = "GET"
    } -TimeoutSec 5
    Write-Host "✅ CORS Preflight - OK" -ForegroundColor Green
    $results += @{ Test = "CORS Preflight"; Status = "PASS" }
}
catch {
    Write-Host "❌ CORS Preflight - FAIL: $($_.Exception.Message)" -ForegroundColor Red
    $results += @{ Test = "CORS Preflight"; Status = "FAIL"; Error = $_.Exception.Message }
}

# Resumen
Write-Host "`n📊 RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "====================" -ForegroundColor Gray
$passed = ($results | Where-Object { $_.Status -eq "PASS" }).Count
$failed = ($results | Where-Object { $_.Status -eq "FAIL" }).Count
$skipped = ($results | Where-Object { $_.Status -eq "SKIP" }).Count

Write-Host "✅ Pasaron: $passed" -ForegroundColor Green
Write-Host "❌ Fallaron: $failed" -ForegroundColor Red
Write-Host "⚠️  Omitidos: $skipped" -ForegroundColor Yellow
Write-Host "====================" -ForegroundColor Gray

if ($failed -eq 0) {
    Write-Host "`n🎉 TODAS LAS PRUEBAS PASARON - Backend listo para integración!`n" -ForegroundColor Green
    exit 0
} else {
    Write-Host "`n⚠️  HAY FALLAS - Revisar errores arriba`n" -ForegroundColor Yellow
    exit 1
}
