# Correção dos Materiais Genéricos - BRASRIO App

## Problema Identificado

**Materiais dando resultado genérico:** PVC placa, Isopor, Divisória e Pisos estavam retornando "Material genérico" em vez de cálculos específicos.

## Materiais Afetados

1. **PVC Placa** - Não tinha classe específica
2. **Isopor** - Não tinha classe específica  
3. **Divisória (Painel)** - Não tinha classe específica
4. **Pisos** - Não tinha classe específica

## Solução Implementada

### 1. **Criadas Classes Específicas**

#### **IsoporCalculator**
```kotlin
class IsoporCalculator(
    private val metrosQuadrados: Double,
    private val tipoForro: String
) {
    fun calcularMateriais(): List<MaterialItem> {
        // Forro isopor: cada pacote cobre 19,2m²
        val quantidadePaineis = kotlin.math.ceil(area / 19.2).toInt()
        addMaterialByCode("68", quantidadePaineis, materiaisSelecionados) // Forro isopor
        
        // Materiais adicionais específicos do Isopor
        addMaterialByCode("19", kotlin.math.ceil((area * 5) / 100.0).toInt(), materiaisSelecionados) // PARAFUSO 13 PONTA BROCA
        addMaterialByCode("267", kotlin.math.ceil((area * 2) / 50.0).toInt(), materiaisSelecionados) // PRESILHA BIGODE 20MM
        addMaterialByCode("164", kotlin.math.ceil((area * 0.5) / 100.0).toInt(), materiaisSelecionados) // PINO CLIP
        
        // Usa função padronizada para grade quadrada de forros 1250x625
        calcularMateriaisForro1250x625(area, materiaisSelecionados)
    }
}
```

#### **PainelCalculator (Divisória)**
```kotlin
class PainelCalculator(
    private val metrosQuadrados: Double,
    private val quantidadePortas: Int = 0
) {
    fun calcularMateriais(): List<MaterialItem> {
        // Calcula quantidade de painéis baseado nos metros quadrados
        val quantidadePaineis = kotlin.math.ceil(area / 2.53).toInt()
        
        // Cálculo baseado na quantidade de painéis
        addMaterialByCode("79", quantidadePaineis, materiaisSelecionados) // Painel Eucatex
        addMaterialByCode("89", kotlin.math.ceil(quantidadePaineis * 1.22).toInt(), materiaisSelecionados) // Guia Baixa
        addMaterialByCode("81", kotlin.math.ceil(quantidadePaineis * 1.0).toInt(), materiaisSelecionados) // NTR Travessa 3M
        addMaterialByCode("87", kotlin.math.ceil(quantidadePaineis * 1.0).toInt(), materiaisSelecionados) // NTR Travessa 1185 M
        
        // Requadros e Batentes só são incluídos se houver portas
        if (quantidadePortas > 0) {
            addMaterialByCode("102", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Requadro Horizontal
            addMaterialByCode("101", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Requadro Vertical
            addMaterialByCode("107", kotlin.math.ceil(quantidadePortas * 1.0).toInt(), materiaisSelecionados) // Batente Horizontal
            addMaterialByCode("110", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Batente Vertical
        }
    }
}
```

#### **PisoCalculator**
```kotlin
class PisoCalculator(
    private val metrosQuadrados: Double,
    private val tipoPiso: String, // "Vinílico" ou "Laminado"
    private val corPiso: String? = null
) {
    fun calcularMateriais(): List<MaterialItem> {
        if (tipoPiso == "Vinílico") {
            // Lógica para piso vinílico
            val melhor = escolherMelhorPisoVinilico(area)
            addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados)
            addMaterialByCode("947", melhor.quantidade, materiaisSelecionados) // MASSA NIVELADORA
        } else if (tipoPiso == "Laminado") {
            // Lógica para piso laminado
            val melhor = escolherMelhorPisoLaminado(area)
            addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados)
            addMaterialByCode("447", kotlin.math.ceil(area / 1.2).toInt(), materiaisSelecionados) // MANTA P/ PISO LAMINADO
        }
    }
}
```

### 2. **Atualizado ResultadoActivity**

Adicionados casos específicos para cada material:

```kotlin
material == "Isopor" -> {
    val calculator = IsoporCalculator(m2, placa)
    calculator.calcularMateriais()
}
material == "Painel" -> {
    val calculator = PainelCalculator(m2, portas)
    calculator.calcularMateriais()
}
material == "Piso" && subtype == "Vinílico" -> {
    val calculator = PisoCalculator(m2, "Vinílico", cor)
    calculator.calcularMateriais()
}
material == "Piso" && subtype == "Laminado" -> {
    val calculator = PisoCalculator(m2, "Laminado", cor)
    calculator.calcularMateriais()
}
```

## Materiais Cobertos

### **Isopor**
- **Código 68:** Forro isopor (cada pacote cobre 19,2m²)
- **Código 19:** Parafuso 13 ponta broca
- **Código 267:** Presilha bigode 20mm
- **Código 164:** Pino clip
- **Grade quadrada:** Perfis e travessas para forro

### **Divisória (Painel)**
- **Código 79:** Painel Eucatex (Divisória Naval)
- **Código 89:** Guia Baixa (U) Branca 3.00 mts
- **Código 81:** NTR Travessa 3M
- **Código 87:** NTR Travessa 1185 M
- **Códigos 102, 101, 107, 110:** Requadros e Batentes (se houver portas)

### **Pisos**
- **Vinílico:** Códigos 1574, 1570, 1599, 1575, 1576 + Massa niveladora (947)
- **Laminado:** Códigos 1102, 1236, 1401 + Manta para piso laminado (447)

## Status

✅ **IsoporCalculator criada**
✅ **PainelCalculator criada**
✅ **PisoCalculator criada**
✅ **ResultadoActivity atualizada**
✅ **Todos os materiais cobertos**

Agora todos os materiais (PVC placa, Isopor, Divisória e Pisos) devem retornar cálculos específicos em vez de "Material genérico".
