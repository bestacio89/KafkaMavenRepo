## Project Setup and Environment Management

### Overview

This project uses Docker Compose to manage multiple environments, including Development, Test, and Production. The goal of separating these environments is to ensure that each stage of development and deployment is handled with the appropriate configurations and resources. This separation helps avoid configuration errors, streamline the development process, and ensure a consistent deployment pipeline.

### Environment Structure

We use three primary environments in our Docker Compose setup:

1. **Development**: Used for day-to-day development and debugging.
2. **Test**: Used for running automated tests and ensuring code quality.
3. **Production**: Used for deploying the application in a live environment.

### Docker Compose Configuration

#### 1. Base Configuration (`docker-compose.yml`)

The base configuration file contains common settings and services that are shared across all environments. It defines the fundamental services required by the application, such as MongoDB, PostgreSQL, Kafka, and the application itself.

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:4.4
    container_name: MongoDB
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_INITDB_ROOT_PASSWORD}
    ports:
      - "27017:27017"
    volumes:
      - ./MongoDBData:/data/db
    networks:
      - app-network

  zookeeper:
    image: confluentinc/cp-zookeeper:8.2.3
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"
    networks:
      - app-network

  kafka:
    image: apache/kafka:3.8.0
    container_name: Franz
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
    volumes:
      - ./KafkaData:/var/lib/kafka/data
    depends_on:
      - zookeeper
    networks:
      - app-network

  postgres:
    image: postgres:14.5
    container_name: PostgreSQL
    environment:
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
    ports:
      - "5432:5432"
    volumes:
      - ./PostgresData:/var/lib/postgresql/data
    networks:
      - app-network

  app:
    build:
      context: ../KafkaTemplate
      dockerfile: KafkaTemplate.Dockerfile
    container_name: TestSpring
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${SPRING_DATASOURCE_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
    depends_on:
      - postgres
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  db_data:
```

#### 2. Development Configuration (`docker-compose.dev.yml`)

This file contains settings specific to the development environment. It might include configurations that are tailored for developers, such as different database names or additional debugging tools.

```yaml
version: '3.8'

services:
  mongodb:
    environment:
      MONGO_INITDB_ROOT_PASSWORD: ${DEV_MONGO_INITDB_ROOT_PASSWORD}

  postgres:
    environment:
      POSTGRES_PASSWORD: ${DEV_POSTGRES_PASSWORD}
      POSTGRES_DB: dev_db
      POSTGRES_USER: ${DEV_POSTGRES_USER}

  app:
    environment:
      SPRING_DATASOURCE_DB: dev_db
```

#### 3. Test Configuration (`docker-compose.test.yml`)

This file is used for testing purposes. It ensures that the test environment mirrors production as closely as possible to catch issues before deployment.

```yaml
version: '3.8'

services:
  mongodb:
    environment:
      MONGO_INITDB_ROOT_PASSWORD: ${TEST_MONGO_INITDB_ROOT_PASSWORD}

  postgres:
    environment:
      POSTGRES_PASSWORD: ${TEST_POSTGRES_PASSWORD}
      POSTGRES_DB: test_db
      POSTGRES_USER: ${TEST_POSTGRES_USER}

  app:
    environment:
      SPRING_DATASOURCE_DB: test_db
```

#### 4. Production Configuration (`docker-compose.prod.yml`)

This file is intended for the production environment. It may include optimizations and settings necessary for running the application in a live environment.

```yaml
version: '3.8'

services:
  mongodb:
    environment:
      MONGO_INITDB_ROOT_PASSWORD: ${PROD_MONGO_INITDB_ROOT_PASSWORD}

  postgres:
    environment:
      POSTGRES_PASSWORD: ${PROD_POSTGRES_PASSWORD}
      POSTGRES_DB: prod_db
      POSTGRES_USER: ${PROD_POSTGRES_USER}

  app:
    environment:
      SPRING_DATASOURCE_DB: prod_db
```

### Benefits of Using Separate Environments

1. **Isolation**: Each environment is isolated from the others, preventing configuration and data conflicts.
2. **Testing**: Different environments allow for robust testing and validation. For example, you can test features in the `test` environment before they are deployed to production.
3. **Consistency**: Ensures that the configuration used in development, testing, and production is consistent and appropriate for each stage.
4. **Security**: Sensitive data can be handled differently across environments, enhancing security.
5. **Ease of Maintenance**: Makes it easier to update and maintain configurations specific to each environment without affecting others.

### Running Docker Compose

To start the application in a specific environment, use the following commands:

- **Development**:
  ```bash
  docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
  ```

- **Test**:
  ```bash
  docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d
  ```

- **Production**:
  ```bash
  docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
  ```

### Conclusion

By using separate Docker Compose files for different environments, we can ensure that our application runs smoothly across various stages of development and deployment. This approach provides a structured way to manage configurations and helps streamline the development and deployment processes.

---

Feel free to customize this template based on your project's specifics and requirements.