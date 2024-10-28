@echo off
REM Устанавливаем путь к папке, в которой находится скрипт
set SCRIPT_DIR=%~dp0

REM Вывод пути для отладки
echo SCRIPT_DIR: %SCRIPT_DIR%

REM Обновление списка репозиториев Helm
helm repo update

REM Установка Prometheus с использованием settings.yaml
helm install prometheus prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace -f "%SCRIPT_DIR%setting.yaml"
