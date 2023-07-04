RelaTable Storage Web App
=========================

## Getting started

1. Install a RDBMS of your choice (MariaDB, PostgreSQL, ...)
2. Create `USERS` table:

   ```sql
   CREATE TABLE users (
       username        VARCHAR(128 /* CHAR */) NOT NULL PRIMARY KEY,
       password        VARCHAR(60 /* BYTE */) NOT NULL,
       creation_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
   );
   ```

3. Insert a new user:
   1. Generate a Bcrypt encrypted hash of your desired password, e.g. using [bcrypt-generator.com](https://bcrypt-generator.com)
   2. Insert a row into `USERS` table:

      ```sql
      INSERT INTO users (username, password) VALUES ('admin', <the password hash>);
      ```

4. Create `STORAGE` table:

   ```sql
   CREATE TABLE storage (
       uuid_base64url   VARCHAR(22 /* BYTE */) NOT NULL PRIMARY KEY,
       filename         VARCHAR(1024 /* CHAR */) NOT NULL UNIQUE,
       content_length   NUMERIC(19, 0) /* NOT NULL DEFERRABLE INITIALLY DEFERRED */ CHECK (content_length >= 0),
       last_modified    TIMESTAMP NOT NULL,
       compressed       NUMERIC(1, 0) NOT NULL CHECK (compressed IN (0, 1)),
       encrypted        NUMERIC(1, 0) NOT NULL CHECK (encrypted IN (0, 1)),
       file_contents    BLOB NOT NULL,
       creation_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
   );
   ```

5. Create your custom `application.yml` configuration file in a directory of your choice:

   ```yml
   spring:
     datasource:
       url: '<RDBMS JDBC URL>'
       username: '<RDBMS username>'
       password: '<RDBMS password>'
     servlet:
       multipart:
         max-file-size: 25MB
         max-request-size: 26MB
   relatable-storage:
     table-name: 'STORAGE'
     schema-name: '<Schema containing the STORAGE table; omit if no schema prefix is needed>'
     compression: '<NONE|LOW|MEDIUM|HIGH>'
   ```

### Standalone JAR

1. Build the JAR:

   ```console
   ./mvnw clean verify
   ```

2. Start the web app:
   
   ```console
   java -Dspring.config.location="<directory containing the external application.yml>" -jar relatable-storage-webapp.jar
   ```

### WAR

1. Set a environment or context variable named `spring.config.location` referencing the directory that contains your custom configuration file.
2. Build the WAR:

   ```console
   ./mvnw clean verify -P war
   ```

3. Deploy the WAR.
