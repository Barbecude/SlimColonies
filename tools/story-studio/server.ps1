param(
    [int]$Port = 8765,
    [switch]$NoBrowser
)

$ErrorActionPreference = "Continue"
$StudioDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = (Resolve-Path (Join-Path $StudioDir "..\..")).Path
$ResourcesRoot = Join-Path $ProjectRoot "src\main\resources"
$QuestsDir = Join-Path $ResourcesRoot "data\slimcolonies\colony\quests"
$NamesDir = Join-Path $ResourcesRoot "data\slimcolonies\citizennames"
$RaidsFile = Join-Path $ResourcesRoot "data\slimcolonies\raids\custom_raids.json"
$LangEnFile = Join-Path $ResourcesRoot "assets\slimcolonies\lang\en_us.json"
$LangIdFile = Join-Path $ResourcesRoot "assets\slimcolonies\lang\id_id.json"
$DatagenLangFile = Join-Path $ProjectRoot "src\datagen\generated\slimcolonies\assets\slimcolonies\lang\default.json"

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Send-Json($Response, $Data, [int]$StatusCode = 200) {
    $Response.StatusCode = $StatusCode
    $Response.ContentType = "application/json; charset=utf-8"
    $Response.Headers.Add("Access-Control-Allow-Origin", "*")
    $json = $Data | ConvertTo-Json -Depth 30
    $bytes = $utf8NoBom.GetBytes($json)
    $Response.ContentLength64 = $bytes.Length
    $Response.OutputStream.Write($bytes, 0, $bytes.Length)
    $Response.OutputStream.Close()
}

function Read-BodyText($Request) {
    $reader = New-Object System.IO.StreamReader($Request.InputStream, [System.Text.Encoding]::UTF8)
    $text = $reader.ReadToEnd()
    $reader.Close()
    return $text
}

$listener = New-Object System.Net.HttpListener
$url = "http://localhost:$Port/"
$listener.Prefixes.Add($url)
$listener.Start()

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  SlimColonies Story & Raid Studio running at $url" -ForegroundColor Green
Write-Host "  Project Root : $ProjectRoot" -ForegroundColor Gray
Write-Host "  Auto-Save    : ACTIVE (Directly updates src/main/resources)" -ForegroundColor Yellow
Write-Host "================================================================" -ForegroundColor Cyan

if (-not $NoBrowser) {
    Start-Process $url
}

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $req = $context.Request
        $res = $context.Response
        $path = $req.Url.AbsolutePath

        try {
            if ($req.HttpMethod -eq "OPTIONS") {
                $res.Headers.Add("Access-Control-Allow-Origin", "*")
                $res.Headers.Add("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                $res.Headers.Add("Access-Control-Allow-Headers", "Content-Type")
                $res.StatusCode = 200
                $res.OutputStream.Close()
                continue
            }

            if ($path -eq "/" -or $path -eq "/index.html") {
                $htmlPath = Join-Path $StudioDir "index.html"
                $bytes = [System.IO.File]::ReadAllBytes($htmlPath)
                $res.ContentType = "text/html; charset=utf-8"
                $res.ContentLength64 = $bytes.Length
                $res.OutputStream.Write($bytes, 0, $bytes.Length)
                $res.OutputStream.Close()
                continue
            }

            if ($path -eq "/api/quests" -and $req.HttpMethod -eq "GET") {
                $questList = @()
                if (Test-Path $QuestsDir) {
                    $files = Get-ChildItem -Path $QuestsDir -Recurse -Filter "*.json"
                    foreach ($f in $files) {
                        $rel = $f.FullName.Substring($QuestsDir.Length + 1).Replace("\", "/")
                        $id = "slimcolonies:" + ($rel -replace "\.json$", "")
                        try {
                            $raw = [System.IO.File]::ReadAllText($f.FullName, $utf8NoBom)
                            $parsed = $raw | ConvertFrom-Json
                            $questList += @{
                                id = $id
                                relPath = $rel
                                data = $parsed
                            }
                        } catch {
                            Write-Warning "Failed to parse quest $rel : $_"
                        }
                    }
                }
                Send-Json $res @{ ok = $true; quests = $questList }
                continue
            }

            if ($path -eq "/api/quests/save" -and $req.HttpMethod -eq "POST") {
                $body = Read-BodyText $req | ConvertFrom-Json
                $relPath = $body.relPath.Replace("/", "\")
                if (-not $relPath.EndsWith(".json")) { $relPath += ".json" }
                $targetFile = Join-Path $QuestsDir $relPath
                $parentDir = Split-Path -Parent $targetFile
                if (-not (Test-Path $parentDir)) {
                    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
                }
                $jsonContent = $body.data | ConvertTo-Json -Depth 30
                [System.IO.File]::WriteAllText($targetFile, $jsonContent, $utf8NoBom)

                # Ensure datagen duplicate does not shadow custom quest JSON
                $datagenQuest = Join-Path $ProjectRoot "src\datagen\generated\slimcolonies\data\slimcolonies\colony\quests\$relPath"
                if (Test-Path $datagenQuest) {
                    Remove-Item $datagenQuest -Force -ErrorAction SilentlyContinue
                }

                Send-Json $res @{ ok = $true; saved = $targetFile }
                continue
            }

            if ($path -eq "/api/quests/delete" -and $req.HttpMethod -eq "POST") {
                $body = Read-BodyText $req | ConvertFrom-Json
                $relPath = $body.relPath.Replace("/", "\")
                $targetFile = Join-Path $QuestsDir $relPath
                if (Test-Path $targetFile) {
                    Remove-Item $targetFile -Force
                }
                Send-Json $res @{ ok = $true; deleted = $targetFile }
                continue
            }

            if ($path -eq "/api/names" -and $req.HttpMethod -eq "GET") {
                $packs = @{}
                if (Test-Path $NamesDir) {
                    $files = Get-ChildItem -Path $NamesDir -Filter "*.json"
                    foreach ($f in $files) {
                        $packName = $f.BaseName
                        $raw = [System.IO.File]::ReadAllText($f.FullName, $utf8NoBom)
                        $packs[$packName] = ($raw | ConvertFrom-Json)
                    }
                }
                Send-Json $res @{ ok = $true; packs = $packs }
                continue
            }

            if ($path -eq "/api/names/save" -and $req.HttpMethod -eq "POST") {
                $body = Read-BodyText $req | ConvertFrom-Json
                $packName = $body.packName
                if (-not (Test-Path $NamesDir)) {
                    New-Item -ItemType Directory -Path $NamesDir -Force | Out-Null
                }
                $targetFile = Join-Path $NamesDir "$packName.json"
                $jsonContent = $body.data | ConvertTo-Json -Depth 10
                [System.IO.File]::WriteAllText($targetFile, $jsonContent, $utf8NoBom)

                if ($body.setAsDefault -eq $true -and $packName -ne "default") {
                    $defaultFile = Join-Path $NamesDir "default.json"
                    [System.IO.File]::WriteAllText($defaultFile, $jsonContent, $utf8NoBom)
                }
                Send-Json $res @{ ok = $true; saved = $targetFile }
                continue
            }

            if ($path -eq "/api/raids" -and $req.HttpMethod -eq "GET") {
                if (Test-Path $RaidsFile) {
                    $raw = [System.IO.File]::ReadAllText($RaidsFile, $utf8NoBom)
                    $data = $raw | ConvertFrom-Json
                } else {
                    $data = @{
                        enabled = $true
                        nights_between_raids = 3
                        raids = @()
                    }
                }
                Send-Json $res @{ ok = $true; data = $data }
                continue
            }

            if ($path -eq "/api/raids/save" -and $req.HttpMethod -eq "POST") {
                $body = Read-BodyText $req | ConvertFrom-Json
                $parentDir = Split-Path -Parent $RaidsFile
                if (-not (Test-Path $parentDir)) {
                    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
                }
                $jsonContent = $body.data | ConvertTo-Json -Depth 15
                [System.IO.File]::WriteAllText($RaidsFile, $jsonContent, $utf8NoBom)
                Send-Json $res @{ ok = $true; saved = $RaidsFile }
                continue
            }

            if ($path -eq "/api/lang" -and $req.HttpMethod -eq "GET") {
                $targetLang = if (Test-Path $LangEnFile) { $LangEnFile } elseif (Test-Path $DatagenLangFile) { $DatagenLangFile } else { $null }
                $entries = @{}
                if ($targetLang) {
                    $raw = [System.IO.File]::ReadAllText($targetLang, $utf8NoBom)
                    $entries = $raw | ConvertFrom-Json
                }
                Send-Json $res @{ ok = $true; entries = $entries }
                continue
            }

            if ($path -eq "/api/lang/save" -and $req.HttpMethod -eq "POST") {
                $body = Read-BodyText $req | ConvertFrom-Json
                $updates = $body.updates

                foreach ($langPath in @($LangEnFile, $LangIdFile, $DatagenLangFile)) {
                    if (Test-Path $langPath) {
                        $raw = [System.IO.File]::ReadAllText($langPath, $utf8NoBom)
                        $obj = $raw | ConvertFrom-Json
                        foreach ($prop in $updates.PSObject.Properties) {
                            $obj | Add-Member -NotePropertyName $prop.Name -NotePropertyValue $prop.Value -Force
                        }
                        $outJson = $obj | ConvertTo-Json -Depth 5
                        [System.IO.File]::WriteAllText($langPath, $outJson, $utf8NoBom)
                    }
                }
                # Create id_id.json if it doesn't exist yet
                if (-not (Test-Path $LangIdFile) -and (Test-Path $LangEnFile)) {
                    Copy-Item $LangEnFile $LangIdFile -Force
                }
                Send-Json $res @{ ok = $true; updatedCount = @($updates.PSObject.Properties).Count }
                continue
            }

            Send-Json $res @{ ok = $false; error = "Not Found: $path" } 404
        } catch {
            Send-Json $res @{ ok = $false; error = $_.Exception.Message } 500
        }
    }
} finally {
    $listener.Stop()
}
