# Используем официальный образ с OpenJDK 17
FROM openjdk:23-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR файл в контейнер
COPY Alarm.jar /app/Alarm.jar

# Запускаем приложение при старте контейнера
CMD ["sh", "-c", "java -jar /app/Alarm.jar && tail -f /dev/null"]
