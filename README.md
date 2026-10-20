# SOLID #3 – L: Liskov Substitution Principle (LSP)

> **Princípio da Substituição de Liskov**: "Se S é um subtipo de T, então objetos do tipo T podem ser substituídos por objetos do tipo S sem alterar nenhuma das propriedades desejáveis do programa." — Barbara Liskov (1987)

## Objetivo

Este repositório demonstra o **Liskov Substitution Principle (LSP)** — o terceiro princípio do SOLID — através de um sistema de pedidos com meios de pagamento.

O repositório implementa o **mesmo sistema em duas versões**:
- **`before/`** — Viola o LSP: `Boleto` herda de `MeioDePagamento` mas não cumpre o contrato (lança exceção em `estornar()`)
- **`after/`** — Respeita o LSP: Interfaces segregadas (`MeioDePagamento` e `Estornavel`), cada tipo implementa apenas o que cumpre

---

## Estrutura do Projeto

```
solid-03-lsp/
├── pom.xml                    # Maven parent POM
├── README.md                  # Este arquivo
├── CHANGELOG.md               # Passo a passo da refatoração
├── docs/
│   └── lsp.md                 # Documentação detalhada do LSP
├── before/                    # Versão que VIOLA o LSP
│   ├── pom.xml
│   ├── src/main/java/.../before/
│   │   ├── Cliente.java
│   │   ├── Item.java
│   │   ├── Pedido.java
│   │   ├── MeioDePagamento.java    # Classe abstrata com pagar() E estornar()
│   │   ├── Cartao.java             # Implementa ambos
│   │   ├── Pix.java                # Implementa ambos
│   │   ├── Boleto.java             # VIOLA LSP: lança UnsupportedOperationException em estornar()
│   │   └── CancelarPedido.java     # Precisa de instanceof Boleto para não quebrar
│   └── src/test/.../before/
│       ├── PedidoTest.java
│       └── MeioDePagamentoTest.java
├── after/                     # Versão que RESPEITA o LSP
│   ├── pom.xml
│   ├── src/main/java/.../after/
│   │   ├── domain/               # Pedido, Cliente, Item (reaproveitados do SRP/OCP)
│   │   ├── payment/              # Contratos segregados
│   │   │   ├── MeioDePagamento.java    # Interface apenas com pagar()
│   │   │   ├── Estornavel.java         # Interface apenas com estornar()
│   │   │   ├── Cartao.java             # Implementa AMBOS
│   │   │   ├── Pix.java                # Implementa AMBOS
│   │   │   └── Boleto.java             # Implementa APENAS MeioDePagamento
│   │   └── application/          # Composição
│   │       └── CancelarPedido.java     # Decide pelo contrato (Estornavel), não pela classe
│   └── src/test/.../after/
│       ├── domain/
│       │   └── PedidoTest.java
│       ├── payment/
│       │   └── MeioDePagamentoTest.java
│       └── application/
│           └── CancelarPedidoTest.java
└── acceptance-test/           # Testes que rodam nas DUAS versões
    ├── pom.xml
    └── src/test/.../acceptance/
        └── PedidoAcceptanceTest.java
```

---

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Build e Testes

```bash
# Build completo (compila, testa, gera relatórios)
mvn clean verify

# Apenas compilar
mvn clean compile

# Rodar testes unitários da versão before
mvn test -pl before

# Rodar testes unitários da versão after
mvn test -pl after

# Rodar testes de aceitação (comparam before vs after)
mvn test -pl acceptance-test

# Rodar todos os testes
mvn test
```

---

## Comparativo: Before vs After

### Versão BEFORE (Viola LSP)

```java
// MeioDePagamento.java - Contrato amplo demais
public abstract class MeioDePagamento {
    public abstract void pagar(double valor);
    public abstract void estornar(double valor);  // Promessa que nem todos cumprem
}

// Boleto.java - VIOLA LSP
public class Boleto extends MeioDePagamento {
    @Override
    public void pagar(double valor) { ... }
    
    @Override
    public void estornar(double valor) {
        throw new UnsupportedOperationException("Boleto não suporta estorno"); // QUEBRA O CONTRATO
    }
}

// CancelarPedido.java - Código cliente precisa conhecer tipo concreto
public class CancelarPedido {
    public void executar(Pedido pedido) {
        if (pedido.getPagamento() instanceof Boleto) {  // instanceof = violação LSP/OCP
            // tratamento especial
        } else {
            pedido.getPagamento().estornar(pedido.calcularTotal());
        }
    }
}
```

**Problemas:**
- ❌ `Boleto` não é substituível por `MeioDePagamento` (quebra o contrato)
- ❌ Cliente precisa de `instanceof` para evitar erro em tempo de execução
- ❌ Viola também OCP: novo meio de pagamento = modificar `CancelarPedido`
- ❌ Exceção em tempo de execução em vez de erro em tempo de compilação
- ❌ Herança usada só para reaproveitar código, não para comportamento

### Versão AFTER (Respeita LSP)

```java
// MeioDePagamento.java - Contrato mínimo: apenas o que TODOS cumprem
public interface MeioDePagamento {
    void pagar(double valor);
}

// Estornavel.java - Contrato separado para quem suporta estorno
public interface Estornavel {
    void estornar(double valor);
}

// Cartao.java - Cumpre AMBOS os contratos
public class Cartao implements MeioDePagamento, Estornavel { ... }

// Pix.java - Cumpre AMBOS os contratos
public class Pix implements MeioDePagamento, Estornavel { ... }

// Boleto.java - Cumpre APENAS o que promete
public class Boleto implements MeioDePagamento { ... }  // Não implementa Estornavel

// CancelarPedido.java - Decide pelo CONTRATO, não pela classe
public class CancelarPedido {
    public void executar(Pedido pedido, MeioDePagamento pagamento, Estornavel estorno) {
        if (estorno != null) {
            estorno.estornar(pedido.calcularTotal());  // Polimorfismo seguro
        } else {
            // Fluxo alternativo sem estorno (crédito na loja, etc.)
        }
    }
}
```

**Vantagens:**
- ✅ `Boleto` é substituível por `MeioDePagamento` (cumpre o que promete)
- ✅ Sem `instanceof`, sem `if (tipo === ...)`, sem surpresas em runtime
- ✅ Respeita OCP: novo meio de pagamento = nova classe, código existente não muda
- ✅ Erro em tempo de compilação se tentar estornar um Boleto
- ✅ Composição de contratos via interfaces (Interface Segregation Principle)

---

## Regras do Contrato (Design by Contract)

| Regra | Significado | Violação Típica |
|-------|-------------|-----------------|
| Pré-condições não podem ser reforçadas | Subclasse não pode exigir mais que a base | Subclasse recusa entradas que a base aceitava |
| Pós-condições não podem ser enfraquecidas | Subclasse deve entregar pelo menos o que a base promete | Retorna `null` onde a base nunca retornava |
| Invariantes devem ser preservadas | Regras que sempre valem para a base continuam valendo | Estado que a base garantia deixa de ser garantido |
| Exceções: sem tipos novos e inesperados | Cliente não deve ser surpreendido por falhas que a base não declara | `NotImplementedException` ou `UnsupportedOperationException` |
| Restrição de histórico | Subclasse não altera estado de forma que a base proibia | Tornar mutável algo que a base tratava como imutável |

---

## Code Smells de Violação do LSP

- ✅ Métodos sobrescritos que lançam `NotImplementedException`, `UnsupportedOperationException` ou ficam vazios
- ✅ Código cliente com `instanceof`, `is`, `typeof` ou casts para decidir o que fazer com a abstração
- ✅ Subclasse que valida entradas de forma mais restritiva que a base
- ✅ Subclasse que retorna `null`, lista vazia ou valor neutro onde a base garante resultado válido
- ✅ Herança usada só para reaproveitar código, sem relação de comportamento ("herdo para não copiar")
- ✅ Documentação do tipo "não chame o método X nesta subclasse"
- ✅ Testes da superclasse que falham quando executados contra a subclasse

---

## Técnicas para Corrigir Violações do LSP

| Técnica | Quando Usar | Exemplo |
|---------|-------------|---------|
| **Segregar a interface** | Nem todas as implementações suportam todas as operações | Separar `Estornavel` de `MeioDePagamento` |
| **Composição em vez de herança** | Herança só reaproveita código | `Quadrado` que usa um `Retangulo` internamente |
| **Abstração mais genérica** | Base prometia mais do que devia | Criar `Forma` em vez de `Quadrado extends Retangulo` |
| **Tornar objetos imutáveis** | Setters causam efeitos colaterais na hierarquia | Value objects imutáveis |
| **Testes de contrato** | Garantir que toda implementação cumpra a promessa | Mesma suíte de testes executada contra cada implementação |

---

## Exemplo Clássico Complementar: Retângulo e Quadrado

```java
// VIOLA LSP: violação silenciosa de pós-condição
class Retangulo {
    protected int largura, altura;
    public void setLargura(int l) { this.largura = l; }
    public void setAltura(int a) { this.altura = a; }
    public int area() { return largura * altura; }
}

class Quadrado extends Retangulo {
    public void setLargura(int l) { this.largura = l; this.altura = l; } // Efeito colateral!
    public void setAltura(int a) { this.largura = a; this.altura = a; }
}

void teste(Retangulo r) {
    r.setLargura(5);
    r.setAltura(4);
    assert r.area() == 20; // FALHA com Quadrado (resultado 16)
}
```

**Solução:** Não usar herança aqui; modelar `Forma` com `area()` e deixar `Retangulo` e `Quadrado` como implementações independentes e imutáveis.

---

## Métricas de Ganho

| Métrica | Before | After |
|---------|--------|-------|
| `instanceof` no código cliente | 1 | 0 |
| Exceções em runtime no estorno | Sim (Boleto) | Não (erro de compilação) |
| Contratos cumpridos por todas implementações | Não | Sim |
| Novos meios de pagamento exigem modificar `CancelarPedido` | Sim | Não |

---

## Próximos Passos

Com o LSP pronto, o próximo repositório da série será **I – Interface Segregation Principle**, explorando como interfaces grandes forçam implementações a depender de métodos que não usam.

---

## Referências

- Barbara Liskov — *Data Abstraction and Hierarchy* (1987)
- Barbara Liskov & Jeannette Wing — *A Behavioral Notion of Subtyping* (1994)
- Robert C. Martin — *Agile Software Development: Principles, Patterns, and Practices*
- Robert C. Martin — *Clean Architecture*
- Martin Fowler — *Refactoring: Replace Inheritance with Delegation*