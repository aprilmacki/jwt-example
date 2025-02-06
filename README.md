
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
- Encrypt passwords 
- Create QA tests using httpie 
- Use MapStruct 
- Use Problem converters
- Use ProblemDetail type

