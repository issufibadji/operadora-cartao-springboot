# Arquitetura Geral

## Visão Geral

O sistema segue a arquitetura em camadas (Layered Architecture), com separação clara de responsabilidades entre Controller, Service e Infrastructure (Repository + Entities).

```
┌─────────────────────────────────────────────────────┐
│                   HTTP Client                        │
└────────────────────────┬────────────────────────────┘
                         │ HTTP Request
┌────────────────────────▼────────────────────────────┐
│               Controller Layer                       │
│          ClienteController  (/cliente)               │
│     DTOs: ClienteRequestDTO / ClienteResponseDTO     │
│             Mapper: ClienteMapper (MapStruct)        │
└────────────────────────┬────────────────────────────┘
                         │ ClienteEntity
┌────────────────────────▼────────────────────────────┐
│               Business Layer (Services)              │
│  ClienteService          GeraDadosCartaoService      │
│  - solicitarCartao()     - gerarParaCliente()        │
│  - buscarPorCpf()        - gerarNumeroCartao()       │
│                          - gerarDataExpiracao()      │
│                          - gerarCVV()                │
│                          - determinarLimiteCredito() │
└────────────────────────┬────────────────────────────┘
                         │ JPA
┌────────────────────────▼────────────────────────────┐
│             Infrastructure Layer                     │
│   ClienteJpaRepository  (extends JpaRepository)     │
│   Entities: ClienteEntity, CartaoEntity,             │
│             EnderecoEntity                           │
│   Database: H2 (in-memory)                          │
└─────────────────────────────────────────────────────┘
```

## Estilo Arquitetural

- **REST API**: comunicação stateless via HTTP/JSON
- **Layered Architecture**: Controller → Service → Repository
- **Domain Model**: entidades JPA mapeadas para H2 em memória
- **DTO Pattern**: separação entre modelo interno (Entity) e contrato externo (DTO)
- **MapStruct**: geração de código de mapeamento em tempo de compilação (zero overhead em runtime)
- **Lombok**: eliminação de boilerplate (getters, setters, construtores)

## Fluxo Principal: Solicitação de Cartão

```
POST /cliente
    │
    ▼
ClienteController.solicitaCartao(ClienteRequestDTO)
    │
    ├─ ClienteMapper.toEntity(dto)           → ClienteEntity
    │
    ▼
ClienteService.solicitarCartao(ClienteEntity)
    │
    ├─ ClienteJpaRepository.existsByEmail()  → verifica duplicidade
    │
    ├─ GeraDadosCartaoService.gerarParaCliente()
    │       ├─ gerarNumeroCartao()           → "4000" + 12 dígitos aleatórios
    │       ├─ gerarDataExpiracao()          → data entre 2024-2029
    │       ├─ gerarCVV()                   → 3 dígitos aleatórios
    │       └─ determinarLimiteCredito()    → baseado em idade e renda
    │
    ├─ ClienteJpaRepository.save(cliente)
    │
    ▼
ClienteController
    └─ ClienteMapper.toResponse(entity)     → ClienteResponseDTO
```

## Fluxo Secundário: Busca por CPF

```
GET /cliente?cpf={cpf}
    │
    ▼
ClienteController.buscaClientePorCpf(cpf)
    │
    ▼
ClienteService.buscarPorCpf(cpf)
    │
    ├─ ClienteJpaRepository.findByCpf(cpf)
    │       └─ Optional.orElseThrow() → IllegalArgumentException se não encontrado
    │
    ▼
ClienteMapper.toResponse(entity)            → ClienteResponseDTO
```

## Decisões de Design

| Decisão | Justificativa |
|--------|---------------|
| H2 in-memory | Simplicidade para desenvolvimento e testes sem dependência de banco externo |
| MapStruct | Mapeamento type-safe gerado em compile-time, sem reflexão em runtime |
| Records para DTOs | Imutabilidade e concisão para objetos de transferência |
| OneToOne entre entidades | Modelagem 1:1 real entre Cliente, Cartão e Endereço |
| Geração de dados do cartão no backend | Segurança — o cliente nunca define número, CVV ou limite |
