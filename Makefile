default: build run

build:
	mvn clean package

run:
	mvn spring-boot:run

benchmark:
	mvn clean gatling:test -Dgatling.simulationClass=com.upgrade.benchmark.Benchmark