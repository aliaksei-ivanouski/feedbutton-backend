# Feedbutton service

### Compilation
This should be done after adding any sql migration scripts for database (generates JOOQ structure)
```shell
   mvn clean compile
```

### Running locally

* start mysql
    ```shell
    docker-compose -f docker-compose.dev.yml up -d db
    ```

* start redis
    ```shell
    docker-compose -f docker-compose.dev.yml up -d redis
    ```

* run server
    ```shell
    mvn spring-boot:run -Dspring-boot.run.profiles=local
    ```
