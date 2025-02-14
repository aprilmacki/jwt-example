
# Intro

This service shows how to use JSON Web Tokens (JWT) for user authentication in Spring Boot.

The passwords are hashed using Bcrypt.
The user data is stored in a MySQL database. The unit tests use the H2 in-memory database instead.
Any errors are returned as problem details (RFC 9457).

This service has the following API endpoints:
- /auth/signup: Create a new user. Accepts a name, email, and password. 
- /auth/login: Authenticate an exising user. Accepts an email and password. Returns a JWT token.
- /users/me: Returns the currently logged-in user.

The call to /users/me must include a JWT token in the auth header. The service will validate the token is valid and 
not expired.

# Commands 

Run MySQL in a Docker container: 

``` shell
docker run -d -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=authexample --name mysqldb -p 3307:3306 mysql:8.0
```

Run the app
```shell
mvn spring-boot:run

```
# Todo

- Add full unit test coverage 
- Create QA tests using httpie 
- Use MapStruct 
- Use ProblemDetail type

