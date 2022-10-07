# Pull base image
FROM python:3.10.2-slim-bullseye

# Set environment variables
ENV PIP_DISABLE_PIP_VERSION_CHECK 1
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Set work directory
WORKDIR /code

# Copy project
COPY . .

# Install dependencies
COPY ./requirements.txt .
RUN apt-get update && apt-get install -y cron
RUN apt-get install -y cron && touch /var/log/cron.log
RUN pip install -r requirements.txt
EXPOSE 8000
EXPOSE 22

# Service cron restart. Crontab add. Migrate
COPY ./docker-entrypoint.sh .
RUN chmod 755 ./docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["start"]
