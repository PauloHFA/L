# Changelog - SOLID #3 LSP

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-10-20

### Adicionado
- Estrutura base do projeto Maven multi-módulo (parent, before, after, acceptance-test)
- Versão BEFORE demonstrando violação do LSP:
  - `MeioDePagamento` classe abstrata com `pagar()` e `estornar()`
  - `Cartao` e `Pix` implementam ambos os métodos
  - `Boleto` lança `UnsupportedOperationException` em `estornar()` (VIOLA LSP)
  - `CancelarPedido` usa `instanceof Boleto` para tratamento especial
- Versão AFTER respeitando o LSP:
  - Interface `MeioDePagamento` apenas com `pagar()`
  - Interface `Estornavel` apenas com `estornar()`
  - `Cartao` e `Pix` implementam AMBAS as interfaces
  - `Boleto` implementa APENAS `MeioDePagamento`
  - `CancelarPedido` decide pelo contrato `Estornavel`, não pela classe concreta
- Testes unitários para ambas as versões
- Testes de aceitação comparando comportamento entre before e after
- Documentação completa no README.md
- Documentação técnica em docs/lsp.md

### Técnicas Aplicadas
- **Interface Segregation**: Separação de `MeioDePagamento` e `Estornavel`
- **Composição sobre herança**: `CancelarPedido` recebe contratos via parâmetros
- **Design by Contract**: Pré/pós-condições, invariantes, exceções
- **Testes de contrato**: Mesma suíte validando todas implementações