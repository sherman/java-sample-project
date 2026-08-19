# Java Sample Project

This repository is a Maven multi-module Java project built with JDK 25.0.1.

## Requirements

- JDK 25.0.1
- Maven 3.6.3 or newer

The Maven Enforcer Plugin verifies the Java and Maven versions at the start of
the build.

## Modules

| Module | Purpose | Direct project dependency |
| --- | --- | --- |
| `base` | Shared foundational code | None |
| `domain` | Domain model | `base` |
| `server` | Server and dependency-injection layer | `domain` |
| `app` | Application assembly | `server` |

The root POM owns all dependency versions. Module POMs declare only the
dependencies they use and inherit their versions from dependency management.
The managed external dependencies are:

- Guava 33.7.1-jre
- Guice 7.0.0
- JUnit Jupiter 5.14.4
- fastutil 8.5.19

Spring Framework and Spring Boot are intentionally excluded. The build rejects
direct and transitive dependencies from their Maven groups.

## Build

Run the complete reactor test phase from the repository root:

```shell
mvn test
```
