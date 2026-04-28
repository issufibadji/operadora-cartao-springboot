# Regras de Negócio

## 1. Solicitação de Cartão

### RN-001 — Unicidade por e-mail

Um cliente não pode ter mais de um cadastro. A verificação de duplicidade é feita pelo **e-mail** antes de qualquer persistência.

```
SE existsByEmail(email) == true
    ENTÃO lançar IllegalArgumentException("Usuário já cadastrado")
```

### RN-002 — Geração automática do cartão

Todo cartão é inteiramente gerado pelo sistema. O cliente não define nenhum dado do cartão. O sistema gera:

- **Número**: prefixo `4000` (padrão Visa) + 12 dígitos aleatórios → total 16 dígitos
- **CVV**: 3 dígitos aleatórios no intervalo 000–999
- **Data de expiração**: ano aleatório entre 2024 e 2029, mês aleatório entre 1 e 12, dia fixo em 01

### RN-003 — Limite de crédito por perfil

O limite é determinado pela combinação de **idade** e **renda mensal** do cliente:

| Faixa Etária | Renda Mensal          | Limite Aprovado |
|-------------|----------------------|-----------------|
| 18–25 anos  | < R$ 3.000           | R$ 1.000        |
| 18–25 anos  | R$ 3.000 a R$ 5.999  | R$ 3.000        |
| 18–25 anos  | ≥ R$ 6.000           | R$ 5.000        |
| 26–40 anos  | < R$ 4.000           | R$ 2.000        |
| 26–40 anos  | R$ 4.000 a R$ 7.999  | R$ 5.000        |
| 26–40 anos  | ≥ R$ 8.000           | R$ 10.000       |
| 40+ anos    | < R$ 5.000           | R$ 3.000        |
| 40+ anos    | R$ 5.000 a R$ 9.999  | R$ 8.000        |
| 40+ anos    | ≥ R$ 10.000          | R$ 15.000       |

**Implementado em:** `GeraDadosCartaoService.determinarLimiteCredito()`

O limite disponível inicial (`availableLimit`) é igual ao limite total aprovado.

---

## 2. Consulta de Cliente

### RN-004 — Busca por CPF obrigatória

A consulta de cliente exige CPF como parâmetro. Se nenhum cliente for encontrado, o sistema lança:

```
IllegalArgumentException("Usuário não encontrado")
```

---

## 3. Modelo de Dados

### RN-005 — Relacionamento 1:1 entre Cliente, Cartão e Endereço

- Cada cliente possui exatamente **um** cartão e **um** endereço.
- Não é possível ter um cartão sem cliente associado.
- Não é possível ter um endereço sem cliente associado.

### RN-006 — Restrições de tamanho no endereço

| Campo       | Limite     |
|-------------|-----------|
| complemento | 10 chars  |
| cidade      | 150 chars |
| estado      | 2 chars   |
| cep         | 9 chars   |

---

## 4. Fluxograma de Solicitação

```
INÍCIO
  │
  ▼
Recebe ClienteRequestDTO
  │
  ▼
Mapeia para ClienteEntity (MapStruct)
  │
  ▼
E-mail já existe no banco?
  ├─ SIM → IllegalArgumentException("Usuário já cadastrado") → FIM (erro)
  │
  └─ NÃO
       │
       ▼
    Gera número do cartão (4000 + 12 dígitos)
       │
       ▼
    Gera data de expiração (2024–2029)
       │
       ▼
    Gera CVV (000–999)
       │
       ▼
    Calcula limite (por idade + renda)
       │
       ▼
    Associa cartão ao cliente
       │
       ▼
    Salva cliente no banco (cascade salva cartão e endereço)
       │
       ▼
    Mapeia para ClienteResponseDTO
       │
       ▼
FIM (sucesso, HTTP 200)
```

---

## 5. Pontos de Evolução Identificados

| Item | Situação Atual | Recomendação |
|------|---------------|-------------|
| Validação de entrada | Ausente | Adicionar `@Valid` + Bean Validation (JSR-303) |
| Tratamento de erros | `IllegalArgumentException` direto | Implementar `@ControllerAdvice` com respostas padronizadas |
| Geração de números aleatórios | `Random` | Usar `SecureRandom` ou `ThreadLocalRandom` |
| Expiração do cartão | Anos hardcoded (2024–2029) | Calcular dinamicamente (ex: now + 5 anos) |
| Unicidade | Apenas por e-mail | Adicionar unicidade por CPF também |
| Banco de dados | H2 in-memory | Migrar para PostgreSQL em produção |
