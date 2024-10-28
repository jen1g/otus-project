@echo off
REM Создание пространства имен для NGINX
kubectl create namespace nginx

REM Добавление репозитория Helm для Ingress NGINX
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx/

REM Обновление списка репозиториев Helm
helm repo update

REM Устанавливаем путь к папке, в которой находится скрипт
set SCRIPT_DIR=%~dp0

REM Запуск команды Helm с использованием пути к файлу nginx_ingress.yaml
helm install nginx ingress-nginx/ingress-nginx --namespace nginx -f "%SCRIPT_DIR%nginx_ingress.yaml" ^
  --set controller.metrics.enabled=true ^
  --set-string controller.podAnnotations."prometheus\.io/scrape"="true" ^
  --set-string controller.podAnnotations."prometheus\.io/port"="10254" ^
  --set controller.metrics.serviceMonitor.enabled=true ^
  --set controller.metrics.serviceMonitor.additionalLabels.release=prometheus
