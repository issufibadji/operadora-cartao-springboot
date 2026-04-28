# Modelo de Dados

## Diagrama Entidade-Relacionamento

```
┌──────────────────────────────┐
│         cliente              │
├──────────────────────────────┤
│ PK  id            BIGINT     │
│     nome          VARCHAR    │
│     email         VARCHAR    │
│     cpf           VARCHAR    │
│     idade         INTEGER    │
│     renda_mensal  DOUBLE     │
│ FK  endereco_id   BIGINT ────┼─────┐
│ FK  cartao_id     BIGINT ────┼──┐  │
└──────────────────────────────┘  │  │
                                  │  │
┌──────────────────────────────┐  │  │
│           cartao             │  │  │
├──────────────────────────────┤  │  │
│ PK  id                BIGINT │◄─┘  │
│     numero            VARCHAR│     │
│     data_expiracao    DATE   │     │
│     cvv               VARCHAR│     │
│     limite            DOUBLE │     │
│     available_limit   DOUBLE │     │
│     ultima_alt_limite DATE   │     │
│     dt_vencto_fatura  INTEGER│     │
│ FK  cliente_id        BIGINT │     │
└──────────────────────────────┘     │
                                     │
┌──────────────────────────────┐     │
│          endereco            │     │
├──────────────────────────────┤     │
│ PK  id           BIGINT      │◄────┘
│     rua          VARCHAR     │
│     numero       BIGINT      │
│     complemento  VARCHAR(10) │
│     cidade       VARCHAR(150)│
│     estado       VARCHAR(2)  │
│     cep          VARCHAR(9)  │
│ FK  cliente_id   BIGINT      │
└──────────────────────────────┘
```

## Entidades JPA

### ClienteEntity

| Campo         | Tipo    | Restrições       | Descrição              |
|--------------|---------|-----------------|------------------------|
| id           | Long    | PK, auto        | Identificador interno  |
| nome         | String  | -               | Nome completo          |
| email        | String  | -               | E-mail (único por negócio) |
| cpf          | String  | -               | CPF do cliente         |
| idade        | Integer | -               | Idade em anos          |
| rendaMensal  | double  | -               | Renda mensal bruta     |
| endereco     | EnderecoEntity | OneToOne | Endereço residencial |
| cartao       | CartaoEntity   | OneToOne | Cartão de crédito    |

### CartaoEntity

| Campo                | Tipo      | Restrições | Descrição                    |
|---------------------|-----------|-----------|------------------------------|
| id                  | Long      | PK, auto  | Identificador interno        |
| numero              | String    | -         | Número de 16 dígitos (prefixo 4000) |
| dataExpiracao       | LocalDate | -         | Data de expiração do cartão  |
| cvv                 | String    | -         | Código de segurança (3 dígitos) |
| limite              | double    | -         | Limite total aprovado        |
| availableLimit      | double    | -         | Limite disponível atual      |
| ultimaAlteracaoLimite | LocalDate | -       | Última alteração de limite   |
| dataVencimentoFatura | Integer  | -         | Dia do vencimento da fatura  |
| cliente             | ClienteEntity | OneToOne | Cliente proprietário      |

### EnderecoEntity

| Campo       | Tipo   | Restrições    | Descrição              |
|------------|--------|--------------|------------------------|
| id         | Long   | PK, auto     | Identificador interno  |
| rua        | String | -            | Logradouro             |
| numero     | Long   | -            | Número do imóvel       |
| complemento | String | max 10 chars | Apto, sala, etc.      |
| cidade     | String | max 150 chars | Cidade                |
| estado     | String | max 2 chars  | UF (ex: SP)            |
| cep        | String | max 9 chars  | CEP (ex: 01310-100)    |
| cliente    | ClienteEntity | OneToOne | Dono do endereço    |

## Relacionamentos

```
ClienteEntity 1 ──── 1 CartaoEntity
ClienteEntity 1 ──── 1 EnderecoEntity
```

Todos os relacionamentos são `@OneToOne` bidirecionais. O lado proprietário é `ClienteEntity` (contém as chaves estrangeiras).

## Banco de Dados

O projeto usa **H2 in-memory** por padrão, adequado para desenvolvimento e testes. O schema é gerado automaticamente pelo Hibernate via `spring.jpa.hibernate.ddl-auto` (padrão `create-drop` para H2).

Para produção, substitua a dependência H2 por PostgreSQL ou MySQL e configure:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/operadora_cartao
spring.datasource.username=usuario
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=validate
```
