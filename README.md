# pistachio

This Backend service handles business domain related data

### To run the app

```
./run.sh
```

### To run the tests

```
./run_tests.sh
```

### To run only a single test suite in the integration tests:

Please remember to include the package/path for the shared resources,
the shared resources is needed to help WeaverTests locate the shared resources needed for the tests

```
./itTestOnly RegistrationControllerISpec controllers.ControllerSharedResource 
```

---

### To populate the postgres sql database using flyway

Please run the docker compose scripts

```
./setup_flyway_migrations.sh
```

### To clear down the database

```
./clear_down_flyway.sh
```

```
./clear_down_postgres.sh
```

### To clear down docker container for app and orphans
```
docker-compose down --volumes --remove-orphans
```

---

### To connect to postgres sql db

```
psql -h localhost -p 5432 -U shared_user -d shared_db
```

### Set base search path for schema

```
ALTER ROLE shared_user SET search_path TO share_schema, public;
```