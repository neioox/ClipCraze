FROM openjdk:18-jdk-slim

RUN apt-get update && \
    apt-get install -y git python3 python3-pip

WORKDIR /app

COPY transcript transcript

RUN cd transcript && pip install -r requirements.txt

COPY target/backend-1.0-SNAPSHOT.jar backend.jar

CMD ["sh", "-c", "cd transcript && python3 app.py & java -jar backend.jar"]

EXPOSE 8080
