#!/bin/sh
set -e
# Cron
service cron restart
python manage.py crontab add
# Django
#python manage.py collectstatic --noinput
python manage.py migrate
#python manage.py runserver 0.0.0.0:8000
exec "$@"
