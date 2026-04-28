# API e Endpoints

## Base URL

```
http://localhost:8080
```

## Endpoints

---

### POST /cliente — Solicitar cartão de crédito

Registra um novo cliente e emite um cartão de crédito com limite calculado automaticamente.

**Request**

```
POST /cliente
Content-Type: application/json
```

```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cpf": "123.456.789-00",
  "idade": "30",
  "rendaMensal": "8000",
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

**Campos obrigatórios**

| Campo                | Tipo   | Descrição                              |
|---------------------|--------|----------------------------------------|
| nome                | String | Nome completo do cliente               |
| email               | String | E-mail (deve ser único no sistema)     |
| cpf                 | String | CPF no formato 000.000.000-00          |
| idade               | String | Idade em anos (mínimo 18)              |
| rendaMensal         | String | Renda mensal bruta em reais            |
| dataVencimentoFatura| String | Dia do mês para vencimento (1–31)      |
| endereco.rua        | String | Logradouro                             |
| endereco.numero     | Long   | Número do imóvel                       |
| endereco.cidade     | String | Cidade                                 |
| endereco.estado     | String | UF com 2 caracteres (ex: SP)           |
| endereco.cep        | String | CEP com 9 caracteres (ex: 01310-100)   |

**Response 200 OK**

```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cpf": "123.456.789-00",
  "idade": "30",
  "cartao": {
    "numero": "4000839271048163",
    "dataExpiracao": "2027-09-01",
    "cvv": "482",
    "limite": 10000.0
  }
}
```

**Response 400 Bad Request** — quando o e-mail já está cadastrado

```
IllegalArgumentException: Usuário já cadastrado
```

---

### GET /cliente — Buscar cliente por CPF

Retorna os dados do cliente e do cartão associado.

**Request**

```
GET /cliente?cpf=123.456.789-00
```

**Parâmetros de query**

| Parâmetro | Tipo   | Obrigatório | Descrição       |
|-----------|--------|-------------|-----------------|
| cpf       | String | Sim         | CPF do cliente  |

**Response 200 OK**

```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cpf": "123.456.789-00",
  "idade": "30",
  "cartao": {
    "numero": "4000839271048163",
    "dataExpiracao": "2027-09-01",
    "cvv": "482",
    "limite": 10000.0
  }
}
```

**Response 400 Bad Request** — quando o CPF não existe

```
IllegalArgumentException: Usuário não encontrado
```

---

## Exemplos com curl

### Solicitar cartão

```bash
curl -X POST http://localhost:8080/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "cpf": "987.654.321-00",
    "idade": "35",
    "rendaMensal": "9000",
    "dataVencimentoFatura": "15",
    "endereco": {
      "rua": "Avenida Paulista",
      "numero": 1000,
      "complemento": "Sala 10",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01311-000"
    }
  }'
```

### Buscar por CPF

```bash
curl "http://localhost:8080/cliente?cpf=987.654.321-00"
```

---

## Dados gerados automaticamente pelo backend

O backend gera os seguintes dados do cartão sem intervenção do cliente:

| Campo           | Formato                           | Exemplo              |
|----------------|-----------------------------------|----------------------|
| numero          | "4000" + 12 dígitos aleatórios    | 4000839271048163     |
| dataExpiracao   | Ano aleatório 2024–2029, mês 1–12 | 2027-09-01           |
| cvv             | 3 dígitos aleatórios (000–999)    | 482                  |
| limite          | Calculado por idade + renda       | 10000.0              |
