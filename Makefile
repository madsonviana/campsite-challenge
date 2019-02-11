default: build run

build:
	mvn clean package

run:
	mvn spring-boot:run