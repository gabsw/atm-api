# ATM API
## Abstract
This is a take-home challenge from Zinkworks where I built a REST API that simulates a basic ATM machine.

## Author
Gabriela Santos

## Resources
* [ATM REST API documentation](http://localhost:8080/swagger-ui/index.html)

## Main features
1. Check your account balance and the maximum amount of money that you can withdraw.
2. Perform a withdrawal from your bank account.
3. Detailed error handling for common banking operations.

## Requirements
* Java 17
* Maven

## How to run using Docker

To build the containers for the first time, make sure that you are in the same folder as `docker-compose.yml` and use following command:
`docker-compose up --build`

To start and stop without rebuilding the containers, use the following commands:
`docker-compose start` or `docker-compose start -d` (run in detached mode)
`docker-compose stop` or `docker-compose stop -d` (run in detached mode)

When the containers are running properly, you will be able to consume the *ATM REST API* according to the documentation below:

* [ATM REST API documentation](http://localhost:8080/swagger-ui.html#/)

The web service will be available on `http://localhost:8080/api/v1/account`.

## How to run common HTTP Requests
If you are using an IDE such as IntelliJ Ultimate, you can run the most common requests for this API using the `requests.http` in the project root.

## Tests
In order to run all the tests:
1. Start the database with `docker-compose up db`.
2. Run `mvn clean test integration-test`. Note that this will truncate tables in the database.

## Things that I would like to improve with more time
1. Use Spring Security with JWTs to handle the login
   - Alternatively, the account number could be the username and the PIN could have been a password for Basic Auth
2. Use Flyway for database migrations, which is what I typically use at work
3. Use MapStruct for an out-of-the-box mapping between objects instead of doing that manually
4. Add SonarQube to the pipeline
5. Add a lint to the pom.xml

