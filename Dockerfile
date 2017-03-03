FROM openjdk:7

# Copy files into image
COPY . /usr/src/myapp

# Run app
WORKDIR /usr/src/myapp
RUN javac MineSweeper.java
CMD ["java", "MineSweeper"]