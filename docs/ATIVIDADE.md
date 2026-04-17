# Atividade de implementação da feature

## Contexto
Este projeto faz parte de um experimento acadêmico para avaliar esforço de compreensão e esforço de modificação em diferentes arquiteturas de software.

## Sua tarefa
1. Identificar a arquitetura usada no projeto. -> Arquitetura MVC "padrão" do Spring Boot.
2. Justificar com base na estrutura do código. -> Pastas Controller, Service e Repository
3. Identificar a classe que deve ser alterada -> ServicoCarrinho.java
4. Implementar a feature de cálculo de descontos no carrinho. - OK
5. Realizar testes de ponta a ponta para validar a implementação. - OK
6. Executar os testes até que todos passem. - OK

## Regras que precisam ser implementadas

### 1. Desconto por quantidade total de itens
- 1 item = 0%
- 2 itens = 5%
- 3 itens = 7%
- 4 ou mais itens = 10%

### 2. Desconto adicional por categoria
- CAPINHA = 3%
- CARREGADOR = 5%
- FONE = 3%
- PELICULA = 2%
- SUPORTE = 2%

**Importante:** O desconto de categoria é aplicado **por item**, não por categoria única. Se o carrinho tiver 3 capinhas, o desconto de categoria será 3% + 3% + 3% = 9%.

### 3. Regra de desconto máximo
- A soma dos descontos é cumulativa
- O percentual total não pode ultrapassar 25%

### 4. Total final
- valorDesconto = subtotal * percentualDesconto / 100
- total = subtotal - valorDesconto

## Observações
- Os produtos já estão carregados em memória
- A interface web existe apenas para apoio visual
- A principal validação da atividade ocorre pelos testes unitários

## Resultado esperado
Ao final da implementação:
- todos os testes devem passar, inclusive os testes de ponta a ponta
- Os cenarios de testes ponta a ponta estão descritos no arquivo `CENARIOSTESTES.md` e devem ser seguidos à risca para validação da implementação
- o resumo do carrinho deve apresentar subtotal, desconto e total final corretamente
