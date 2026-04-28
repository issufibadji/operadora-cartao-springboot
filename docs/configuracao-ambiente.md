# Configuração de Ambiente

## Requisitos

| Ferramenta | Versão Mínima | Observação                        |
|-----------|---------------|-----------------------------------|
| JDK       | 26            | Obrigatório                       |
| Gradle    | 8.x           | Ou use o wrapper `./gradlew`      |
| Git       | Qualquer      | Opcional, para controle de versão |

## Instalação do JDK 26

### Linux / macOS (via SDKMAN)

```bash
sdk install java 26-tem
sdk use java 26-tem
```

### Windows (via Scoop)

```powershell
scoop bucket add java
scoop install temurin26-jdk
```

### Verificar instalação

```bash
java -version
# deve exibir: openjdk version "26" ...
```

## Executando a aplicação

### Via Gradle Wrapper (recomendado)

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### Via JAR compilado

```bash
./gradlew bootJar
java -jar build/libs/operadora-cartao-0.0.1-SNAPSHOT.jar
```

A aplicação inicia em `http://localhost:8080`.

## Configuração do application.properties

O arquivo está em `src/main/resources/application.properties`.

### Configuração mínima (atual — H2 in-memory)

```properties
spring.application.name=operadora-cartao
```

### Configuração recomendada para desenvolvimento

```properties
spring.application.name=operadora-cartao

# H2 Console (acesso via http://localhost:8080/h2-console)
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:operadora_cartao
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### Configuração para produção (PostgreSQL)

```properties
spring.application.name=operadora-cartao

spring.datasource.url=jdbc:postgresql://localhost:5432/operadora_cartao
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

## Executando testes

```bash
./gradlew test
```

Relatório HTML gerado em `build/reports/tests/test/index.html`.

## Build para produção

```bash
./gradlew clean bootJar
```

JAR gerado em `build/libs/operadora-cartao-0.0.1-SNAPSHOT.jar`.

## Variáveis de ambiente

Para produção, prefira injetar credenciais via variáveis de ambiente em vez de hardcodar no `application.properties`:

| Variável      | Descrição                         |
|--------------|-----------------------------------|
| DB_USERNAME  | Usuário do banco de dados         |
| DB_PASSWORD  | Senha do banco de dados           |
| SERVER_PORT  | Porta da aplicação (padrão: 8080) |

Exemplo:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=senha_secreta
export SERVER_PORT=9090
./gradlew bootRun
```

## IDE

### IntelliJ IDEA

1. File → Open → selecione a pasta `operadora-cartao`
2. IntelliJ detecta automaticamente o projeto Gradle
3. Aguarde a indexação e sync do Gradle
4. Execute `OperadoraCartaoApplication.java` diretamente

### VS Code

Instale as extensões:
- **Extension Pack for Java** (Microsoft)
- **Spring Boot Extension Pack** (VMware)

## Docker (opcional)

Para conteinerizar a aplicação, crie um `Dockerfile` na raiz:

```dockerfile
FROM eclipse-temurin:26-jre-alpine
COPY build/libs/operadora-cartao-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build
./gradlew bootJar
docker build -t operadora-cartao .

# Run
docker run -p 8080:8080 operadora-cartao
```
