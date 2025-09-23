# Implementação de Serviços com Base JSON - BRASRIO App

## Objetivo

Implementar a busca de dados de fretes e instaladores usando os mesmos arquivos JSON do site na pasta Web.

## Arquivos JSON Originais

### **fretes.json**
```json
{
  "fretes": [
    {
      "nome": "Rafael Ribeiro",
      "telefone": "(21) 97039-7474",
      "numeroWhatsApp": "21970397474",
      "areaAtendimento": "Campo Grande e Região",
      "pesoMaximo": "850kg"
    },
    {
      "nome": "Luis Carlos",
      "telefone": "(21) 96483-6357",
      "numeroWhatsApp": "21964836357",
      "areaAtendimento": "Ramos e Região",
      "pesoMaximo": "1600kg"
    },
    {
      "nome": "Bira",
      "telefone": "(21) 99939-8855",
      "numeroWhatsApp": "21999398855",
      "areaAtendimento": "São Gonçalo e Região",
      "pesoMaximo": "9000kg"
    }
  ]
}
```

### **instaladores.json**
```json
{
  "instaladores": [
    {
      "nome": "Marcio M&T Decor",
      "telefone": "(21) 98144-2542",
      "numeroWhatsApp": "21981442542",
      "especializacao": "PVC e Drywall"
    },
    {
      "nome": "Hygino's Gesso",
      "telefone": "(21) 99063-4358",
      "numeroWhatsApp": "21990634358",
      "especializacao": "Gesso e Drywall"
    },
    {
      "nome": "JG Construtora",
      "telefone": "(21) 99977-3930",
      "numeroWhatsApp": "21999773930",
      "especializacao": "Construtora geral"
    },
    {
      "nome": "Márcio Miranda",
      "telefone": "(21) 98417-5074",
      "numeroWhatsApp": "21984175074",
      "especializacao": "Decorações e Manutenções"
    },
    {
      "nome": "Alexandre Drywall",
      "telefone": "(21) 97862-7781",
      "numeroWhatsApp": "21978627781",
      "especializacao": "Drywall, PVC e Isopor"
    },
    {
      "nome": "LB SERVIÇOS",
      "telefone": "(21) 96493-6389",
      "numeroWhatsApp": "21964936389",
      "especializacao": "Pisos Laminado e Vinílicos"
    }
  ]
}
```

## Implementação

### 1. **ServiceManager.kt**
Criado gerenciador para carregar e buscar dados de serviços:

```kotlin
object ServiceManager {
    private var fretes: List<Frete> = emptyList()
    private var instaladores: List<Instalador> = emptyList()
    
    fun loadServices(context: Context) {
        loadFretes(context)
        loadInstaladores(context)
    }
    
    fun getAllFretes(): List<Frete> = fretes
    fun getAllInstaladores(): List<Instalador> = instaladores
    fun getFretesByArea(area: String): List<Frete>
    fun getInstaladoresBySpecialization(especializacao: String): List<Instalador>
}
```

### 2. **Classes de Dados**

#### **Frete**
```kotlin
data class Frete(
    val nome: String,
    val telefone: String,
    val numeroWhatsApp: String,
    val areaAtendimento: String,
    val pesoMaximo: String
)
```

#### **Instalador**
```kotlin
data class Instalador(
    val nome: String,
    val telefone: String,
    val numeroWhatsApp: String,
    val especializacao: String
)
```

### 3. **Atualização das Activities**

#### **FretesActivity**
- Carrega dados do JSON na inicialização
- Exibe lista completa de fretes disponíveis
- Mostra informações: nome, telefone, área, peso máximo, WhatsApp

#### **InstaladoresActivity**
- Carrega dados do JSON na inicialização
- Exibe lista completa de instaladores credenciados
- Mostra informações: nome, telefone, especialização, WhatsApp

### 4. **Carregamento na LoginActivity**
```kotlin
// Carregar produtos do JSON
ProductManager.loadProducts(this)

// Carregar serviços (fretes e instaladores)
ServiceManager.loadServices(this)
```

## Funcionalidades Implementadas

### **Fretes**
- **3 fretes disponíveis**
- **Áreas de atendimento:** Campo Grande, Ramos, São Gonçalo
- **Peso máximo:** 850kg a 9000kg
- **Contato direto:** Telefone e WhatsApp

### **Instaladores**
- **6 instaladores credenciados**
- **Especializações:** PVC, Drywall, Gesso, Construtora, Decorações, Pisos
- **Contato direto:** Telefone e WhatsApp

## Arquivos Criados/Modificados

### **Novos Arquivos:**
- `ServiceManager.kt`: Gerenciador de serviços
- `app/src/main/assets/fretes.json`: Dados de fretes
- `app/src/main/assets/instaladores.json`: Dados de instaladores

### **Arquivos Modificados:**
- `LoginActivity.kt`: Carregamento dos serviços
- `FretesActivity.kt`: Exibição dos dados de fretes
- `InstaladoresActivity.kt`: Exibição dos dados de instaladores

## Status

✅ **ServiceManager criado**
✅ **Arquivos JSON copiados para assets**
✅ **FretesActivity atualizada**
✅ **InstaladoresActivity atualizada**
✅ **LoginActivity atualizada**
✅ **Dados sincronizados com o site**

Agora as telas de fretes e instaladores exibem os dados reais do JSON, mantendo sincronização com o site.
