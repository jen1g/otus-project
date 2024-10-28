@echo off
REM Устанавливаем путь к папке, в которой находится скрипт
set SCRIPT_DIR=%~dp0

REM Вывод пути для отладки
echo SCRIPT_DIR: %SCRIPT_DIR%

helm install loki-stack grafana/loki-stack --namespace monitoring -f "%SCRIPT_DIR%loki-stack-values.yaml"