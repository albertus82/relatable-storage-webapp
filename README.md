RelaTable Storage Web App
=========================

## Getting started

1. Install a RDBMS of your choice (MariaDB, PostgreSQL, ...)
2. Create `USERS` table:

   ```sql
   create table USERS (
       USERNAME        varchar(128 /* char */) not null primary key,
       PASSWORD        varchar(60 /* byte */) not null,
       CREATION_TIME   timestamp default current_timestamp not null
   );
   ```

3. Insert a new user:
   1. Generate a Bcrypt encrypted hash of your desired password, e.g. using [bcrypt-generator.com](https://bcrypt-generator.com)
   2. Insert a row into `USERS` table:

      ```sql
      insert into USERS (USERNAME, PASSWORD) values ('admin', <the password hash>);
      ```

4. Create `STORAGE` table:

   ```sql
   create table STORAGE (
       UUID_BASE64URL   varchar(22 /* byte */) not null primary key,
       FILENAME         varchar(1024 /* char */) not null unique,
       CONTENT_LENGTH   numeric(19, 0) /* not null deferrable initially deferred */ check (CONTENT_LENGTH >= 0),
       LAST_MODIFIED    timestamp not null,
       COMPRESSED       numeric(1, 0) not null check (COMPRESSED in (0, 1)),
       ENCRYPTED        numeric(1, 0) not null check (ENCRYPTED in (0, 1)),
       FILE_CONTENTS    blob not null,
       CREATION_TIME    timestamp default current_timestamp not null
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
