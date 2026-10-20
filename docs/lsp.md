# Liskov Substitution Principle (LSP) - Documentação Técnica

## Definição Formal

> "Se S é um subtipo de T, então objetos do tipo T podem ser substituídos por objetos do tipo S sem alterar nenhuma das propriedades desejáveis do programa."
> — Barbara Liskov (1987), formalizado por Liskov e Wing (1994)

## O Problema no Contexto de Pagamentos

### Cenário
Um sistema de pedidos precisa processar pagamentos e estornos através de diferentes meios:
- **Cartão de crédito**: Suporta pagamento e estorno
- **Pix**: Suporta pagamento e estorno (via devolução)
- **Boleto**: Suporta pagamento, **NÃO suporta estorno automático**

### Violação do LSP (Versão BEFORE)

```java
abstract class MeioDePagamento {
    abstract void pagar(double valor);
    abstract void estornar(double valor);  // Contrato: TODOS devem estornar
}

class Boleto extends MeioDePagamento {
    void pagar(double valor) { ... }
    void estornar(double valor) {
        throw new UnsupportedOperationException("Boleto não suporta estorno"); // VIOLAÇÃO
    }
}
```

**Por que viola o LSP:**
1. **Quebra de pós-condição**: A base promete que `estornar()` funciona, mas `Boleto` falha
2. **Exceção inesperada**: Cliente não espera `UnsupportedOperationException` 
3. **Código cliente corrupto**: Precisa de `instanceof` para evitar crash
4. **Viola OCP também**: Novo meio → modificar `CancelarPedido`

### Solução Respeitando LSP (Versão AFTER)

```java
// Contrato mínimo: o que TODOS cumprem
interface MeioDePagamento {
    void pagar(double valor);
}

// Contrato opcional: apenas para quem suporta
interface Estornavel {
    void estornar(double valor);
}

class Cartao implements MeioDePagamento, Estornavel { ... }
class Pix implements MeioDePagamento, Estornavel { ... }
class Boleto implements MeioDePagamento { ... }  // Só o que cumpre
```

**Por que respeita o LSP:**
1. **Contratos segregados**: Cada tipo promete apenas o que cumpre
2. **Substituibilidade garantida**: `Boleto` substitui `MeioDePagamento` perfeitamente
3. **Sem instanceof**: Cliente usa `Estornavel` (pode ser null)
4. **Erro em compilação**: Tentar estornar Boleto = erro de tipo, não runtime

## Regras do Contrato (Design by Contract)

### 1. Pré-condições não podem ser reforçadas
A subclasse não pode exigir mais do que a superclasse.

```java
// VIOLAÇÃO: Subclasse exige mais
class ContaCorrente extends Conta {
    void sacar(double valor) {
        if (valor > 1000) throw new IllegalArgumentException("Limite 1000"); // Reforçou pré-condição
        super.sacar(valor);
    }
}
```

### 2. Pós-condições não podem ser enfraquecidas
A subclasse deve entregar pelo menos o que a base promete.

```java
// VIOLAÇÃO: Subclasse entrega menos
class RepositorioComCache extends Repositorio {
    Usuario buscarPorId(String id) {
        Usuario u = cache.get(id);
        if (u == null) return null; // Base nunca retorna null!
        return u;
    }
}
```

### 3. Invariantes devem ser preservadas
Regras que sempre valem para a base continuam valendo.

```java
// VIOLAÇÃO: Quebra invariante
class ListaComLimite extends ArrayList {
    void add(Object o) {
        if (size() >= 100) throw new IllegalStateException("Limite atingido");
        super.add(o);
    }
}
// Invariante de List: add() sempre aumenta size() em 1
```

### 4. Exceções: sem tipos novos e inesperados
O cliente não deve ser surpreendido por falhas que a base não declara.

```java
// VIOLAÇÃO: Nova exceção não declarada
class ServicoExterno extends Servico {
    void executar() {
        throw new ServicoIndisponivelException(); // Base não declara isso
    }
}
```

### 5. Restrição de histórico
A subclasse não altera estado de forma que a base proibia.

```java
// VIOLAÇÃO: Torna mutável o que era imutável
class PontoImutavel {
    final int x, y;
    PontoImutavel(int x, int y) { this.x = x; this.y = y; }
}

class PontoMutavel extends PontoImutavel {
    void setX(int x) { this.x = x; } // Quebra imutabilidade!
}
```

## Code Smells - Como Identificar Violações

| Smell | Descrição |
|-------|-----------|
| `NotImplementedException` / `UnsupportedOperationException` | Método vazio ou lança exceção |
| `instanceof` / `is` / `typeof` / casts no cliente | Cliente conhece tipo concreto |
| Validação mais restritiva na subclasse | Pré-condição reforçada |
| Retorna `null` / vazio / neutro onde base garante valor | Pós-condição enfraquecida |
| Herança só para reaproveitar código | "Herdo para não copiar" |
| "Não chame método X nesta subclasse" | Documentação de violação |
| Testes da base falham na subclasse | Quebra de contrato comportamental |

## Técnicas de Correção

### 1. Segregar Interfaces (Interface Segregation Principle)
```java
// ANTES: Interface gorda
interface Ave {
    void voar();
    void nadar();
}

// DEPOIS: Interfaces segregadas
interface Ave { }
interface Voador { void voar(); }
interface Nadador { void nadar(); }

class Pato implements Ave, Voador, Nadador { }
class Pinguim implements Ave, Nadador { }  // Não voa!
class Avestruz implements Ave { }  // Não voa, não nada
```

### 2. Composição em vez de Herança
```java
// ANTES: Herança problemática
class Quadrado extends Retangulo { ... }

// DEPOIS: Composição
class Quadrado {
    private final Retangulo retangulo;
    Quadrado(int lado) { this.retangulo = new Retangulo(lado, lado); }
    int area() { return retangulo.area(); }
}
```

### 3. Abstração Mais Genérica
```java
// ANTES: Base promete demais
class Retangulo { void setLargura(int); void setAltura(int); }

// DEPOIS: Abstração mínima
interface Forma { double area(); }
class Retangulo implements Forma { ... }
class Quadrado implements Forma { ... }
class Circulo implements Forma { ... }
```

### 4. Objetos Imutáveis (Value Objects)
```java
// Elimina efeitos colaterais de setters na hierarquia
record Retangulo(int largura, int altura) implements Forma {
    public double area() { return largura * altura; }
}
record Quadrado(int lado) implements Forma {
    public double area() { return lado * lado; }
}
```

### 5. Testes de Contrato
```java
// Mesma suíte roda contra TODAS as implementações
@ParameterizedTest
@MethodSource("todasImplementacoes")
void contrato_pagar_deveFuncionar(MeioDePagamento pagamento) {
    pagamento.pagar(100.0); // Não deve lançar exceção
}

@ParameterizedTest
@MethodSource("apenasEstornaveis")
void contrato_estornar_deveFuncionar(Estornavel estornavel) {
    estornavel.estornar(100.0); // Só roda para quem implementa
}
```

## Exemplo Clássico: Retângulo vs Quadrado

### O Problema
Na geometria: Quadrado **é um** Retângulo.
Em código com mutabilidade: Quadrado **NÃO é** substituível por Retângulo.

```java
class Retangulo {
    protected int largura, altura;
    void setLargura(int l) { largura = l; }
    void setAltura(int a) { altura = a; }
    int area() { return largura * altura; }
}

class Quadrado extends Retangulo {
    void setLargura(int l) { largura = l; altura = l; } // Efeito colateral!
    void setAltura(int a) { largura = a; altura = a; }
}

void teste(Retangulo r) {
    r.setLargura(5);
    r.setAltura(4);
    assert r.area() == 20; // FALHA com Quadrado (área = 16)
}
```

### Soluções

**Opção 1: Imutabilidade**
```java
record Retangulo(int largura, int altura) implements Forma { ... }
record Quadrado(int lado) implements Forma { ... }
```

**Opção 2: Composição**
```java
class Quadrado {
    private final Retangulo retangulo;
    Quadrado(int lado) { this.retangulo = new Retangulo(lado, lado); }
}
```

**Opção 3: Abstração comum**
```java
interface Forma { double area(); }
class Retangulo implements Forma { ... }
class Quadrado implements Forma { ... }
```

## LSP vs Outros Princípios SOLID

| Princípio | Foco | Relação com LSP |
|-----------|------|-----------------|
| **SRP** | Uma responsabilidade por classe | LSP garante que responsabilidades não vazem pela hierarquia |
| **OCP** | Aberto para extensão, fechado para modificação | LSP é pré-requisito: se subtipos não são substituíveis, polimorfismo não é seguro para extensão |
| **LSP** | Subtipos substituíveis | Base para polimorfismo confiável |
| **ISP** | Interfaces específicas | ISP ajuda LSP: interfaces segregadas = contratos menores = mais fáceis de cumprir |
| **DIP** | Depender de abstrações | LSP garante que abstrações são confiáveis |

## Checklist de Validação LSP

- [ ] Toda subclasse pode ser usada onde a superclasse é esperada?
- [ ] Nenhum método lança `UnsupportedOperationException` ou `NotImplementedException`?
- [ ] Código cliente **não** usa `instanceof` / casts para decidir comportamento?
- [ ] Pré-condições não são mais restritivas nas subclasses?
- [ ] Pós-condições não são mais fracas nas subclasses?
- [ ] Invariantes da base são mantidas?
- [ ] Nenhuma exceção nova/surpreendente é lançada?
- [ ] Estado não é alterado de forma proibida pela base?
- [ ] Testes da superclasse passam para todas as subclasses?
- [ ] Herança modela relação "é-um" **comportamental**, não só estrutural?

## Referências

1. **Barbara Liskov** — *Data Abstraction and Hierarchy* (SIGPLAN Notices, 1987)
2. **Barbara Liskov & Jeannette Wing** — *A Behavioral Notion of Subtyping* (TOPLAS, 1994)
3. **Robert C. Martin** — *Agile Software Development: Principles, Patterns, and Practices* (Cap. 9)
4. **Robert C. Martin** — *Clean Architecture* (Cap. 8)
5. **Martin Fowler** — *Refactoring: Replace Inheritance with Delegation*
6. **Bertrand Meyer** — *Object-Oriented Software Construction* (Design by Contract)
7. **Gang of Four** — *Design Patterns* (Strategy, Decorator, Composite)