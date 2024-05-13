# Instructions

Instructions about bow to use current project

* Clone current project
* Checkout to branch: development, and pull
* Browse folder fintech

#### Run MySQL database container

Create and start containers with command:
```bash
docker compose up
```

List running Compose project with command:
```bash
docker compose ls 
```

List containers with command:
```bash
docker compose ps 
```
*  Build the project's artifact with command:
```bash
mvn package 
``` 
or 
``` bash 
mvn clean package
```
* Deploy locally the app with:
```bash
./mvnw spring-boot:run
```
* Open a new tab in the blowser with the [endpoint](http://localhost:8080/swagger-ui/index.html#/)
* Start interacting with the app, through available endpoints