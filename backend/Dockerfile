FROM openjdk:18-jdk-slim

# Update package lists and install required packages
RUN apt-get update && \
    apt-get install -y git python3 python3-pip ffmpeg

# Set the working directory
WORKDIR /app

# Copy the transcript directory into the container
COPY transcript transcript

# Install Python dependencies
RUN cd transcript && pip install -r requirements.txt

# Copy the JAR file into the container
COPY target/backend-1.0-SNAPSHOT.jar backend.jar
COPY komika_axis /usr/share/fonts/truetype/

# Ensure the Python script is executable
RUN chmod +x transcript/app.py

# Command to run both the Python script and the JAR file
CMD ["sh", "-c", "cd transcript && python3 app.py & java -jar /app/backend.jar"]

# Expose port 8080
EXPOSE 8080
