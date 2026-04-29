# Configuração de Ambiente

## Requisitos

| Ferramenta | Versão   | Observação                                   |
|------------|----------|----------------------------------------------|
| JDK 21     | 21 LTS   | Obrigatório para rodar o Gradle              |
| JDK 26     | 26       | Opcional — para compilação via toolchain     |
| Gradle     | 8.x      | Ou use o wrapper `./gradlew`                 |
| Git        | Qualquer | Opcional, para controle de versão            |

## Compatibilidade Gradle × Java

> **Importante:** O Gradle 8.14 suporta até Java 24. Tentar rodar o Gradle com Java 26 causa o erro:
> `BUG! Unsupported class file major version 70`

| JDK | Class file version | Gradle 8.14       |
|-----|--------------------|-------------------|
| 21  | 65                 | Suportado         |
| 24  | 68                 | Suportado         |
| 26  | 70                 | **Não suportado** |

**Solução:** instale o JDK 21 LTS e aponte o `gradle.properties` para ele. O Gradle roda no JDK 21 enquanto o toolchain do `build.gradle` pode compilar com outra versão se necessário.

## Instalação do JDK 21 (obrigatório para o Gradle)

### Windows (via winget — PowerShell como Administrador)

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

Após instalar, configure o `gradle.properties` na raiz do projeto:

```properties
org.gradle.java.home=C:/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot
```

> Ajuste o número de patch (`21.0.x.x`) conforme a versão instalada em `C:\Program Files\Eclipse Adoptium\`.

### Linux / macOS (via SDKMAN)

```bash
sdk install java 21-tem
sdk use java 21-tem
```

### Verificar instalação

```bash
java -version
# deve exibir: openjdk version "21" ...
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
