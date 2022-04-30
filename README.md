[![Test and Deploy](https://github.com/arcones/isst-grupo08-track-controller/actions/workflows/testAndDeploy.yml/badge.svg?branch=main)](https://github.com/arcones/isst-grupo08-track-controller/actions/workflows/testAndDeploy.yml)

# isst-grupo08-track-controller :articulated_lorry:

Set the `JDBC_DATABASE_URL` environment variable with the full URL of the postgres database.

Then test and run :runner: the application with:
```bash
./mvnw clean install test spring-boot:run
```

Once launched :rocket:, you can check the docs of the API and interact with it :arrows_counterclockwise: [here](http://localhost:8080/swagger-ui/index.html)

There is also a Postman Collection :bookmark_tabs: included with some integration tests :heavy_check_mark:, check it out [here](integrationTests/Trackermaster.postman_collection.json)