# Operadora Cartão

API REST para gestão de cartões de crédito. Permite que clientes solicitem cartões, com geração automática de número, CVV, data de expiração e limite de crédito com base em perfil de renda e idade.

## Tecnologias

| Tecnologia       | Versão    |
|-----------------|-----------|
| Java            | 26        |
| Spring Boot     | 3.5.0     |
| Spring Data JPA | 3.5.0     |
| MapStruct       | 1.6.0     |
| Lombok          | -         |
| H2 Database     | runtime   |
| JUnit 5         | -         |
| Gradle          | Wrapper   |

## Pré-requisitos

- JDK 26+
- Gradle 8.x (ou use o wrapper incluído `./gradlew`)

## Executando o projeto

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

## Endpoints

### Solicitar cartão

```
POST /cliente
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpf": "123.456.789-00",
  "idade": "30",
  "rendaMensal": "6000",
  "dataVencimentoFatura": "10",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": 42,
    "complemento": "Apto 5",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  }
}
```

**Resposta 200:**

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpf": "123.456.789-00",
  "idade": "30",
  "cartao": {
    "numero": "4000XXXXXXXXXXXX",
    "dataExpiracao": "2028-07-01",
    "cvv": "374",
    "limite": 10000.0
  }
}
```

### Buscar cliente por CPF

```
GET /cliente?cpf=123.456.789-00
```

**Resposta 200:** mesmo formato do `ClienteResponseDTO` acima.

## Regras de limite de crédito

| Faixa de Idade | Renda Mensal      | Limite  |
|---------------|-------------------|---------|
| 18–25 anos    | < R$ 3.000        | R$ 1.000|
| 18–25 anos    | R$ 3.000–5.999    | R$ 3.000|
| 18–25 anos    | ≥ R$ 6.000        | R$ 5.000|
| 26–40 anos    | < R$ 4.000        | R$ 2.000|
| 26–40 anos    | R$ 4.000–7.999    | R$ 5.000|
| 26–40 anos    | ≥ R$ 8.000        | R$10.000|
| 40+ anos      | < R$ 5.000        | R$ 3.000|
| 40+ anos      | R$ 5.000–9.999    | R$ 8.000|
| 40+ anos      | ≥ R$ 10.000       | R$15.000|

## Estrutura do projeto

```
src/main/java/com/issufibadji/operadoracartao/
├── OperadoraCartaoApplication.java
├── business/
│   └── services/
│       ├── ClienteService.java
│       └── GeraDadosCartaoService.java
├── controller/
│   ├── ClienteController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ClienteRequestDTO.java
│   │   │   └── EnderecoRequestDTO.java
│   │   └── response/
│   │       ├── ClienteResponseDTO.java
│   │       └── CartaoResponseDTO.java
│   └── mappers/
│       └── ClienteMapper.java
└── infrastructure/
    ├── entities/
    │   ├── ClienteEntity.java
    │   ├── CartaoEntity.java
    │   └── EnderecoEntity.java
    └── repositories/
        └── ClienteJpaRepository.java
```

## Documentação de arquitetura

Consulte o diretório [docs/](docs/) para diagramas e decisões de design:

- [Arquitetura Geral](docs/arquitetura-geral.md)
- [Modelo de Dados](docs/modelo-dados.md)
- [API e Endpoints](docs/api-endpoints.md)
- [Regras de Negócio](docs/regras-negocio.md)
- [Configuração de Ambiente](docs/configuracao-ambiente.md)

## Testes

```bash
./gradlew test
```
