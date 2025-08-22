package com.example.brasrio

import kotlin.math.ceil
import kotlin.math.roundToInt

object CalculoUtils {
    
    // Função para obter todos os produtos em uma lista plana
    fun getAllProducts(produtos: Map<String, List<MaterialItem>>): List<MaterialItem> {
        val todosProdutos = mutableListOf<MaterialItem>()
        for ((_, categoria) in produtos) {
            if (categoria.isNotEmpty()) {
                todosProdutos.addAll(categoria)
            }
        }
        return todosProdutos
    }
    
    // Função para encontrar produto por código
    fun findProductByCode(code: String, produtos: List<MaterialItem>): MaterialItem? {
        return produtos.find { it.codigo == code }
    }
    
    // Função para adicionar material por código
    fun addMaterialByCode(code: String, quantidade: Int, materiaisSelecionados: MutableList<MaterialItem>, produtos: List<MaterialItem>) {
        val produto = findProductByCode(code, produtos)
        
        materiaisSelecionados.add(
            MaterialItem(
                codigo = code,
                nome = produto?.nome ?: "NÃO ENCONTRADO",
                quantidade = ceil(quantidade.toFloat()).toInt().coerceAtLeast(1) // Como JavaScript: Math.ceil(quantidade)
            )
        )
    }
    
    // Função para escolher o melhor piso com menor sobra
    fun escolherMelhorPiso(m2: Float): PisoOption {
        val opcoes = listOf(
            PisoOption("1599", 2.6f),
            PisoOption("1575", 3.90f)
        )
        
        var melhor: PisoOption? = null
        opcoes.forEach { op ->
            val quantidade = ceil(m2 / op.area).toInt()
            val coberta = quantidade * op.area
            val sobra = coberta - m2
            if (
                melhor == null ||
                sobra < melhor!!.sobra ||
                (sobra == melhor!!.sobra && quantidade < melhor!!.quantidade) ||
                (sobra == melhor!!.sobra && quantidade == melhor!!.quantidade && op.area > melhor!!.area)
            ) {
                melhor = PisoOption(op.codigo, op.area, quantidade, sobra)
            }
        }
        return melhor ?: PisoOption("1599", 2.6f, ceil(m2 / 2.6f).toInt(), 0f)
    }
    
    // Funções de cálculo melhoradas
    fun calculatePerfis(area: Float, sistema: String): Int {
        val quantidades = mapOf(
            "parede" to 2.5f,
            "forro" to 3.2f,
            "divisoria" to 2.8f
        )
        return ceil(area * (quantidades[sistema] ?: 2.5f) * 1.05f).toInt()
    }
    
    fun calculateParafusos(area: Float, sistema: String): Int {
        val quantidades = mapOf(
            "parede" to 12f,
            "forro" to 15f,
            "divisoria" to 14f
        )
        return ceil(area * (quantidades[sistema] ?: 12f) * 1.15f).toInt()
    }
    
    fun calculateFita(area: Float, sistema: String): Int {
        val quantidades = mapOf(
            "parede" to 2.5f,
            "forro" to 2.8f,
            "divisoria" to 2.6f
        )
        return ceil(area * (quantidades[sistema] ?: 2.5f) * 1.08f).toInt()
    }
    
    fun calculateMassa(area: Float, sistema: String): Float {
        val quantidades = mapOf(
            "parede" to 0.8f,
            "forro" to 0.9f,
            "divisoria" to 0.85f
        )
        return ((area * (quantidades[sistema] ?: 0.8f) * 1.12f) * 10).roundToInt() / 10f
    }
    
    // Classe para cálculo de parede - baseada em m² + pé direito
    class ParedeCalculator(
        private val metrosQuadrados: Float,
        private val peDireito: Float,
        private val tipoPlaca: String,
        private val produtos: List<MaterialItem>
    ) {
        private val comprimento: Float = metrosQuadrados / peDireito // Calcula comprimento automaticamente
        
        // Configurações de materiais para parede
        companion object {
            private val MATERIAIS_PAREDE = mapOf(
                "280" to PlacaConfig("Placa drywall comum", 2.16f), // 1,80 x 1,20
                "177" to PlacaConfig("Drywall RU (Resistente à Umidade)", 2.16f), // 1,80 x 1,20
                "193" to PlacaConfig("Drywall RF (Resistente à fogo)", 2.16f) // 1,80 x 1,20
            )
        }
        
        data class PlacaConfig(val nome: String, val area: Float)
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val placaConfig = MATERIAIS_PAREDE[tipoPlaca]
            
            if (placaConfig == null) {
                throw IllegalArgumentException("Tipo de placa não encontrado")
            }
            
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            val sistema = "parede"
            
            // Placa escolhida - cálculo baseado na área real
            val quantidadePlacas = ceil(area / placaConfig.area).toInt()
            addMaterialByCode(tipoPlaca, quantidadePlacas, materiaisSelecionados, produtos)
            
            // Parafusos (vêm em mil unidades - código 1521)
            val parafusosNecessarios = calculateParafusos(area, sistema)
            addMaterialByCode("1521", ceil(parafusosNecessarios / 1000f).toInt(), materiaisSelecionados, produtos)
            
            // Fita telada (vêm em rolos de 90m - código 1516)
            val fitaNecessaria = calculateFita(area, sistema)
            addMaterialByCode("1516", ceil(fitaNecessaria / 90f).toInt(), materiaisSelecionados, produtos)
            
            // Massa para acabamento (vêm em kg - código 698 para 5kg, 431 para 28kg)
            val massaNecessaria = calculateMassa(area, sistema)
            if (massaNecessaria <= 5) {
                addMaterialByCode("698", 1, materiaisSelecionados, produtos) // 5kg
            } else {
                val qtd28kg = ceil(massaNecessaria / 28f).toInt()
                addMaterialByCode("431", qtd28kg, materiaisSelecionados, produtos) // 28kg
            }
            
            // Perfis para parede - baseado no comprimento calculado e pé direito
            val montantesNecessarios = ceil(comprimento / 0.6f).toInt() + 1 // A cada 60cm + extremidades
            val guiasNecessarias = ceil((comprimento * 2) / 3f).toInt() // Guias superior e inferior
            
            addMaterialByCode("388", guiasNecessarias, materiaisSelecionados, produtos) // Guia 48
            addMaterialByCode("387", montantesNecessarios, materiaisSelecionados, produtos) // Montante 48
            addMaterialByCode("192", ceil((area * 2) / 100f).toInt(), materiaisSelecionados, produtos) // Bucha 6
            addMaterialByCode("173", ceil((area * 0.5f) / 100f).toInt(), materiaisSelecionados, produtos) // Parafuso Frangeado
            
            return materiaisSelecionados
        }
        
        fun getResumo(): Map<String, Any> {
            return mapOf(
                "metrosQuadrados" to metrosQuadrados,
                "peDireito" to peDireito,
                "comprimento" to "%.2f".format(comprimento),
                "tipoPlaca" to tipoPlaca
            )
        }
    }
    
    data class PisoOption(
        val codigo: String, 
        val area: Float, 
        val quantidade: Int = 0, 
        val sobra: Float = 0f
    )
    
    data class MaterialItem(
        val codigo: String,
        val nome: String,
        val quantidade: Int = 0
    )
}
