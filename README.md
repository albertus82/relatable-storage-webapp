RelaTable Storage Web App
=========================

## Getting started

1. Install a RDBMS of your choice, e.g. PostgreSQL, MariaDB (see https://stackoverflow.com/a/59561496/3260495), ...
2. Create `USERS` table:
   
   ```sql
   CREATE TABLE users (
       username        VARCHAR(128 /* CHAR */) NOT NULL PRIMARY KEY,
       password        VARCHAR(60 /* BYTE */) NOT NULL,
       role            VARCHAR(2 /* BYTE */) NOT NULL CHECK (role IN ('RO', 'RW')),
       creation_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
   );
   ```

3. Insert a new user:
   1. Generate a Bcrypt encrypted hash of your desired password, e.g. using [bcrypt-generator.com](https://bcrypt-generator.com)
   2. Insert a row into `USERS` table:
      
      ```sql
      INSERT INTO users (username, password, role) VALUES ('admin', <the password hash>, 'RW');
      COMMIT WORK;
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
       file_contents    /*LONG*/BLOB NOT NULL,
       creation_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
   );
   ```

5. Create your custom `application.yml` configuration file in a directory of your choice:
   
   ```yml
   spring:
     datasource:
       url: '<RDBMS JDBC URL>'      # mandatory
       username: '<RDBMS username>'
       password: '<RDBMS password>'
     servlet:
       multipart:
         max-file-size: 25MB    # optional, default is only 1MB
         max-request-size: 26MB # optional, default is 10MB
   relatable-storage:
     table-name: 'STORAGE'       # mandatory
     schema-name: 'MYSCHEMA'     # optional, the schema containing the STORAGE table; omit if no schema prefix is needed
     compression: MEDIUM         # optional, one of the following: (NONE|LOW|MEDIUM|HIGH); default is LOW
     password: 'P4$$w0Rd'        # optional, enables data encryption; default is null (no encryption)
     content-disposition: INLINE # optional, one of the following: (INLINE|ATTACHMENT); default is ATTACHMENT
     directory: '/opt/relatable-storage/files/'
   http:
     auth:
       fail-delay-millis: 5000   # optional, default is 4000
     hsts:
       enabled: true             # optional, default is true
       max-age: 63072000         # optional, default is 31536000 (one year)
       include-sub-domains: true # optional, default is true
       preload: false            # optional, default is false
   ```

### Standalone JAR

1. Build the JAR:
   
   ```console
   ./mvnw clean verify
   ```

2. Start the web app:
   
   ```console
   java -Dspring.config.additional-location="<your custom configuration file>" -jar relatable-storage-webapp.jar
   ```

### WAR

1. Set an environment or context variable named `spring.config.additional-location` referencing your custom configuration file.
2. Build the WAR:
   
   ```console
   ./mvnw clean verify -P war
   ```

3. Deploy the WAR.
