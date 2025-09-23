# Correção dos Erros de Compilação - Funções de Pisos

## Problema Identificado

**Erros de compilação:** As funções para cálculo de pisos não estavam definidas, causando erros de "Unresolved reference":

- `getAreaPisoVinilico`
- `escolherMelhorPisoVinilico`
- `getAreaPisoLaminado`
- `escolherMelhorPisoLaminado`

## Solução Implementada

### 1. **Funções de Área de Pisos**

#### **getAreaPisoVinilico**
```kotlin
private fun getAreaPisoVinilico(codigo: String): Double {
    val areas = mapOf(
        "1574" to 3.90,
        "1570" to 3.90,
        "1599" to 2.6,
        "1575" to 3.90,
        "1576" to 3.90
    )
    return areas[codigo] ?: 3.90 // Default para 3.90 se não encontrar
}
```

#### **getAreaPisoLaminado**
```kotlin
private fun getAreaPisoLaminado(codigo: String): Double {
    val areas = mapOf(
        "1102" to 2.41,
        "1236" to 2.51,
        "1401" to 2.84
    )
    return areas[codigo] ?: 2.41 // Default para 2.41 se não encontrar
}
```

### 2. **Funções de Escolha Automática**

#### **escolherMelhorPisoVinilico**
```kotlin
private fun escolherMelhorPisoVinilico(m2: Double): PisoResult {
    val opcoes = listOf(
        PisoOption("1574", 3.90, "PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA"),
        PisoOption("1570", 3.90, "PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA"),
        PisoOption("1599", 2.6, "PISO VINILICO RUFFINO BRAVO COR ANGELIM"),
        PisoOption("1575", 3.90, "PISO VINILICO RUFFINO NOBILE COLADO BAOBA"),
        PisoOption("1576", 3.90, "PISO VINILICO RUFFINO NOBILE COLADO DAMASCO")
    )
    
    // Lógica para escolher o piso com menor sobra
    var melhor: PisoOption? = null
    var menorSobra = Double.MAX_VALUE
    
    opcoes.forEach { op ->
        val quantidade = kotlin.math.ceil(m2 / op.area).toInt()
        val coberta = quantidade * op.area
        val sobra = coberta - m2
        
        if (sobra < menorSobra) {
            menorSobra = sobra
            melhor = op
        }
    }
    
    val quantidade = kotlin.math.ceil(m2 / (melhor?.area ?: 3.90)).toInt()
    return PisoResult(melhor?.codigo ?: "1574", quantidade)
}
```

#### **escolherMelhorPisoLaminado**
```kotlin
private fun escolherMelhorPisoLaminado(m2: Double): PisoResult {
    val opcoes = listOf(
        PisoOption("1102", 2.41, "PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM"),
        PisoOption("1236", 2.51, "PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO"),
        PisoOption("1401", 2.84, "PISO LAMINADO QUICK STEP PREMIERE MOCHA")
    )
    
    // Lógica para escolher o piso com menor sobra
    var melhor: PisoOption? = null
    var menorSobra = Double.MAX_VALUE
    
    opcoes.forEach { op ->
        val quantidade = kotlin.math.ceil(m2 / op.area).toInt()
        val coberta = quantidade * op.area
        val sobra = coberta - m2
        
        if (sobra < menorSobra) {
            menorSobra = sobra
            melhor = op
        }
    }
    
    val quantidade = kotlin.math.ceil(m2 / (melhor?.area ?: 2.41)).toInt()
    return PisoResult(melhor?.codigo ?: "1102", quantidade)
}
```

### 3. **Classes de Suporte**

#### **PisoOption**
```kotlin
data class PisoOption(
    val codigo: String,
    val area: Double,
    val nome: String
)
```

#### **PisoResult**
```kotlin
data class PisoResult(
    val codigo: String,
    val quantidade: Int
)
```

## Funcionalidades Implementadas

### **Pisos Vinílicos**
- **Códigos:** 1574, 1570, 1599, 1575, 1576
- **Áreas:** 3.90m² (exceto 1599 com 2.6m²)
- **Escolha automática:** Seleciona o piso com menor sobra
- **Massa niveladora:** Código 947 (4kg MAPEI)

### **Pisos Laminados**
- **Códigos:** 1102, 1236, 1401
- **Áreas:** 2.41m², 2.51m², 2.84m²
- **Escolha automática:** Seleciona o piso com menor sobra
- **Manta para piso:** Código 447 (1,20ML)

## Status

✅ **Funções de área implementadas**
✅ **Funções de escolha automática implementadas**
✅ **Classes de suporte criadas**
✅ **Erros de compilação corrigidos**

O aplicativo agora deve compilar sem erros e os cálculos de pisos devem funcionar corretamente.
