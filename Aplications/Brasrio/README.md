# Brasrio - Sistema de Orçamentos

## Atualizações Implementadas

### 🚀 Melhorias no Sistema de Cálculos

O aplicativo foi atualizado com mecânicas de cálculo mais avançadas, baseadas no sistema JavaScript existente. As principais melhorias incluem:

#### 1. **Classe ParedeCalculator**
- Cálculo preciso de paredes baseado em m² + pé direito
- Cálculo automático do comprimento da parede
- Distribuição otimizada de perfis (montantes e guias)
- Cálculo baseado na área real das placas (2.16m²)

#### 2. **Funções de Cálculo Melhoradas**
- `calculatePerfis()`: Cálculo de perfis por sistema (parede/forro/divisória)
- `calculateParafusos()`: Cálculo de parafusos com margem de segurança
- `calculateFita()`: Cálculo de fita telada em rolos de 90m
- `calculateMassa()`: Cálculo de massa com escolha inteligente entre 5kg e 28kg

#### 3. **Sistema de Piso Inteligente**
- `escolherMelhorPiso()`: Algoritmo que escolhe o piso com menor sobra
- Considera múltiplas opções de área por caixa
- Prioriza menor quantidade e maior área por caixa

#### 4. **Carregamento de Produtos via JSON**
- Arquivo `produtos.json` organizado por categorias
- Classe `ProdutoLoader` para carregamento dinâmico
- Fallback para produtos padrão caso JSON não seja encontrado
- Funções utilitárias para busca e manipulação de produtos

#### 5. **Utilitários de Cálculo**
- `CalculoUtils`: Classe com funções reutilizáveis
- `findProductByCode()`: Busca eficiente de produtos
- `addMaterialByCode()`: Adição padronizada de materiais
- `getAllProducts()`: Lista plana de todos os produtos

### 📱 Interface do Usuário

#### Campo de Pé Direito
- Adicionado campo para inserir altura da parede
- Visível apenas para Drywall → Parede
- Valor padrão: 2.7m
- Cálculo automático do comprimento baseado na área e altura

#### Layout Atualizado
- Campo de pé direito integrado ao fluxo de cálculo
- Validação de entrada de dados
- Interface mais intuitiva para cálculos precisos

### 🔧 Estrutura Técnica

#### Arquivos Criados/Modificados:
- `OrcamentoActivity.kt`: Atualizado com novas mecânicas
- `CalculoUtils.kt`: Utilitários de cálculo
- `ProdutoLoader.kt`: Carregamento de produtos
- `produtos.json`: Dados estruturados dos produtos
- `activity_orcamento.xml`: Layout atualizado

#### Melhorias na Arquitetura:
- Separação de responsabilidades
- Código mais modular e reutilizável
- Melhor tratamento de erros
- Logs para debugging

### 📊 Cálculos Implementados

#### Drywall Parede:
- Placas baseadas na área real (2.16m²)
- Perfis calculados por comprimento e pé direito
- Montantes a cada 60cm + extremidades
- Guias superior e inferior

#### Drywall Teto:
- Sistema "forro" com cálculos específicos
- Perfis F530 e acessórios
- Cantoneiras e reguladores

#### PVC:
- Forro modular com rodas
- Cálculo de molduras

#### Isopor:
- Sistema completo de forro
- Travessas clicadas
- Presilhas e pinos

#### Painel Eucatex:
- Sistema de divisória
- Perfis específicos
- Batentes e baguetes

#### Piso:
- Escolha inteligente entre opções
- Cálculo de menor sobra

### 🎯 Benefícios

1. **Precisão**: Cálculos mais precisos baseados em dados reais
2. **Flexibilidade**: Sistema adaptável a diferentes tipos de projeto
3. **Manutenibilidade**: Código organizado e bem estruturado
4. **Escalabilidade**: Fácil adição de novos produtos e cálculos
5. **Experiência do Usuário**: Interface mais intuitiva e informativa

### 🔄 Compatibilidade

- Mantém compatibilidade com funcionalidades existentes
- Sistema de fallback para produtos padrão
- Validação robusta de entrada de dados
- Tratamento de erros gracioso

### 📈 Próximos Passos

1. Implementar cache de produtos para melhor performance
2. Adicionar mais opções de cálculo personalizado
3. Integrar com sistema de preços
4. Implementar histórico de orçamentos
5. Adicionar exportação de relatórios

---

**Desenvolvido para Brasrio Campo Grande**  
*Sistema de orçamentos inteligente e preciso*
