# Google Business Finder 🏢

Uma aplicação Python para buscar informações de empresas usando a API oficial do Google Places, sem necessidade de web scraping.

## ✨ Funcionalidades

- 🔍 Busca empresas por nome ou categoria
- 📍 Busca por localização específica com raio configurável
- 📞 Extrai números de telefone quando disponíveis
- 🌐 Obtém websites das empresas
- 📧 Identifica emails (quando disponíveis no website)
- 📊 Exporta dados para planilha Excel
- 📍 Coordenadas geográficas
- ⭐ Avaliações e total de avaliações
- 🏷️ Tipos de negócio
- 📝 Status do negócio

## 🚀 Instalação

### Pré-requisitos

- Python 3.7 ou superior
- Chave da API do Google Places

### Passo a Passo

1. **Clone ou baixe os arquivos**
   ```bash
   cd Brasriocg/Social/Google
   ```

2. **Execute o script de configuração**
   ```bash
   python setup.py
   ```

3. **Ou instale manualmente as dependências**
   ```bash
   pip install -r requirements.txt
   ```

## 🔑 Configuração da API

### Como obter a chave da API do Google Places

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Ative a **Places API**:
   - Vá para "APIs & Services" > "Library"
   - Procure por "Places API"
   - Clique em "Enable"
4. Crie credenciais:
   - Vá para "APIs & Services" > "Credentials"
   - Clique em "Create Credentials" > "API Key"
5. Copie a chave gerada

### Configurar a chave da API

#### Windows (PowerShell)
```powershell
$env:GOOGLE_PLACES_API_KEY = "sua_chave_aqui"
```

#### Windows (CMD)
```cmd
set GOOGLE_PLACES_API_KEY=sua_chave_aqui
```

#### Linux/Mac
```bash
export GOOGLE_PLACES_API_KEY=sua_chave_aqui
```

## 📖 Como Usar

### Executar a aplicação
```bash
python search.py
```

### Opções disponíveis

1. **Buscar empresas por nome/categoria**
   - Digite o nome da empresa ou categoria (ex: "padaria", "farmácia")
   - Defina o número máximo de resultados

2. **Buscar empresas em localização específica**
   - Digite o nome/categoria da empresa
   - Especifique a localização (ex: "São Paulo, SP")
   - Defina o raio de busca em km

### Exemplos de uso

- **Buscar padarias**: Digite "padaria" como query
- **Buscar farmácias em São Paulo**: Digite "farmácia" e localização "São Paulo, SP"
- **Buscar restaurantes**: Digite "restaurante" como query

## 📊 Formato de Saída

A aplicação gera um arquivo Excel com as seguintes colunas:

| Coluna | Descrição |
|--------|-----------|
| nome | Nome da empresa |
| telefone | Número de telefone formatado |
| email | Email (quando disponível) |
| website | Website da empresa |
| endereco | Endereço completo |
| rating | Avaliação média |
| total_avaliacoes | Número total de avaliações |
| tipo_negocio | Categorias do negócio |
| status_negocio | Status atual do negócio |
| coordenadas | Latitude e longitude |

## ⚠️ Limitações e Considerações

### Rate Limiting
- A API do Google Places tem limites de requisições
- A aplicação inclui delays entre requisições para evitar bloqueios
- Recomendamos não exceder 1000 requisições por dia

### Disponibilidade de Dados
- Nem todas as empresas têm todas as informações disponíveis
- Telefones e websites dependem das empresas atualizarem seus dados no Google
- Emails geralmente não estão disponíveis diretamente na API

### Custos
- A API do Google Places tem custos após o uso gratuito mensal
- Consulte [Google Cloud Pricing](https://cloud.google.com/maps-platform/pricing) para detalhes

## 🛠️ Estrutura do Projeto

```
Social/Google/
├── search.py                 # Aplicação principal
├── setup.py                  # Script de configuração
├── requirements.txt          # Dependências Python
├── test_connection.py        # Teste de conexão
└── README.md                 # Este arquivo
```

## 🔧 Personalização

### Modificar campos de busca
Edite a função `get_place_details` em `search.py` para incluir campos adicionais:

```python
'fields': 'name,formatted_phone_number,website,formatted_address,rating,user_ratings_total,types,business_status,opening_hours,price_level'
```

### Ajustar delays
Modifique o valor em `time.sleep(0.1)` para ajustar o intervalo entre requisições.

## 📝 Logs

A aplicação gera logs detalhados em:
- Console (tempo real)
- Arquivo `google_business_finder.log`

## 🆘 Solução de Problemas

### Erro: "Chave da API não encontrada"
- Verifique se a variável de ambiente está configurada
- Reinicie o terminal após configurar a variável

### Erro: "Quota exceeded"
- Você atingiu o limite de requisições da API
- Aguarde até o próximo mês ou atualize seu plano

### Erro: "Request denied"
- Verifique se a Places API está ativada no seu projeto
- Confirme se a chave da API está correta

## 📚 Recursos Adicionais

- [Google Places API Documentation](https://developers.google.com/maps/documentation/places/web-service)
- [Google Cloud Console](https://console.cloud.google.com/)
- [Places API Reference](https://developers.google.com/maps/documentation/places/web-service/supported_types)

## 🤝 Contribuições

Sinta-se à vontade para:
- Reportar bugs
- Sugerir melhorias
- Contribuir com código

## 📄 Licença

Este projeto é de uso livre para fins educacionais e comerciais.

---

**Desenvolvido com ❤️ para facilitar a busca de informações de empresas de forma confiável e segura.**
