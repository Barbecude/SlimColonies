@echo off
title SlimColonies 1.21 - Indonesian Story, Name & Raid Studio
echo ============================================================================
echo   SlimColonies NeoForge 1.21 - Indonesian Story, Dialogue, Name ^& Raid Studio
echo ============================================================================
echo Starting local auto-save server on http://localhost:8765 ...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\story-studio\server.ps1"
pause
