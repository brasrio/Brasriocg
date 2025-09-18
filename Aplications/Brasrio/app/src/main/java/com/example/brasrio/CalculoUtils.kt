package com.example.brasrio

import kotlin.math.ceil
import kotlin.math.roundToInt

object CalculoUtils {
    
    // Configurações de materiais para parede
    private val MATERIAIS_PAREDE = mapOf(
        "280" to PlacaConfig("Drywall ST Branco", 2.16f), // 1,80 x 1,20
        "177" to PlacaConfig("Drywall RU (Resistente à Umidade)", 2.16f), // 1,80 x 1,20
        "193" to PlacaConfig("Drywall RF (Resistente à fogo)", 2.16f), // 1,80 x 1,20
        "181" to PlacaConfig("Placa Cimentícia 8MM", 2.88f), // 2,40 x 1,20
        "182" to PlacaConfig("Placa Cimentícia 10MM", 2.88f), // 2,40 x 1,20
        "263" to PlacaConfig("Placa Cimentícia 6MM", 2.88f), // 2,40 x 1,20
        "1172" to PlacaConfig("Placa Cimentícia 12MM", 2.88f) // 2,40 x 1,20
    )
    
    data class PlacaConfig(val nome: String, val area: Float)
    
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
        
        if (quantidade > 0) {
            materiaisSelecionados.add(
                MaterialItem(
                    codigo = code,
                    nome = produto?.nome ?: "NÃO ENCONTRADO",
                    quantidade = ceil(quantidade.toFloat()).toInt().coerceAtLeast(1)
                )
            )
        }
    }
    
    // Escolhe o piso vinílico com menor sobra (empate: menor quantidade; depois maior área por caixa)
    fun escolherMelhorPisoVinilico(m2: Float): PisoOption {
        val opcoes = listOf(
            PisoOption("1574", 3.90f), // PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA
            PisoOption("1570", 3.90f), // PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA
            PisoOption("1599", 2.6f),  // PISO VINILICO RUFFINO BRAVO COR ANGELIM
            PisoOption("1575", 3.90f), // PISO VINILICO RUFFINO NOBILE COLADO BAOBA
            PisoOption("1576", 3.90f)  // PISO VINILICO RUFFINO NOBILE COLADO DAMASCO
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
    
    // Escolhe o piso laminado com menor sobra
    fun escolherMelhorPisoLaminado(m2: Float): PisoOption {
        val opcoes = listOf(
            PisoOption("1102", 2.41f), // PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM
            PisoOption("1236", 2.51f), // PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO
            PisoOption("1401", 2.84f)  // PISO LAMINADO QUICK STEP PREMIERE MOCHA
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
        return melhor ?: PisoOption("1102", 2.41f, ceil(m2 / 2.41f).toInt(), 0f)
    }
    
    // Função auxiliar para obter área do piso vinílico por código
    fun getAreaPisoVinilico(codigo: String): Float {
        val areas = mapOf(
            "1574" to 3.90f,
            "1570" to 3.90f,
            "1599" to 2.6f,
            "1575" to 3.90f,
            "1576" to 3.90f
        )
        return areas[codigo] ?: 3.90f // Default para 3.90 se não encontrar
    }
    
    // Função auxiliar para obter área do piso laminado por código
    fun getAreaPisoLaminado(codigo: String): Float {
        val areas = mapOf(
            "1102" to 2.41f,
            "1236" to 2.51f,
            "1401" to 2.84f
        )
        return areas[codigo] ?: 2.41f // Default para 2.41 se não encontrar
    }
    
    // Função para escolher o melhor piso com menor sobra (compatibilidade)
    fun escolherMelhorPiso(m2: Float): PisoOption {
        return escolherMelhorPisoVinilico(m2)
    }
    
    // Funções de cálculo melhoradas - baseadas no site
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
            "parede" to 1.25f, // Reduzido pela metade (era 2.5)
            "forro" to 1.4f,   // Reduzido pela metade (era 2.8)
            "divisoria" to 1.3f // Reduzido pela metade (era 2.6)
        )
        return ceil(area * (quantidades[sistema] ?: 1.25f) * 1.08f).toInt()
    }
    
    fun calculateMassa(area: Float, sistema: String): Float {
        val quantidades = mapOf(
            "parede" to 0.4f, // Reduzido pela metade (era 0.8)
            "forro" to 0.45f, // Reduzido pela metade (era 0.9)
            "divisoria" to 0.425f // Reduzido pela metade (era 0.85)
        )
        return ((area * (quantidades[sistema] ?: 0.4f) * 1.12f) * 10).roundToInt() / 10f
    }
    
    // Classe para cálculo de parede - baseada em m² + pé direito (exata do site)
    class ParedeCalculator(
        private val metrosQuadrados: Float,
        private val peDireito: Float,
        private val tipoPlaca: String,
        private val produtos: List<MaterialItem>
    ) {
        private val comprimento: Float = metrosQuadrados / peDireito // Calcula comprimento automaticamente
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val placaConfig = MATERIAIS_PAREDE[tipoPlaca]
            
            if (placaConfig == null) {
                throw IllegalArgumentException("Tipo de placa não encontrado")
            }
            
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            val sistema = "parede"
            
            // Placa escolhida - cálculo baseado na área real (aumentada em 20% para parede)
            val quantidadePlacas = ceil((area / placaConfig.area) * 4.45f).toInt()
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
            
            addMaterialByCode("388", ceil(guiasNecessarias * 3f).toInt(), materiaisSelecionados, produtos) // Guia 48 (aumentada em 200%)
            addMaterialByCode("387", ceil(montantesNecessarios * 1.1f).toInt(), materiaisSelecionados, produtos) // Montante 48 (aumentado em 10%)
            addMaterialByCode("192", ceil((area * 2f) / 100f).toInt(), materiaisSelecionados, produtos) // Bucha 6
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
    
    // Classe para cálculo de placas cimentícias
    class CimenticiaCalculator(
        private val metrosQuadrados: Float,
        private val peDireito: Float,
        private val tipoPlaca: String?,
        private val sistema: String, // 'teto' ou 'parede'
        private val produtos: List<MaterialItem>
    ) {
        private val comprimento: Float = metrosQuadrados / peDireito // Calcula comprimento automaticamente
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            
            // Materiais específicos do sistema cimentícia
            if (sistema == "teto") {
                // Para teto: painel wall + fita e massa (sem placas cimentícias)
                val quantidadePlacas = ceil(area / 2.88f).toInt()
                addMaterialByCode("464", quantidadePlacas, materiaisSelecionados, produtos) // Painel wall
                addMaterialByCode("582", ceil(area / 15f).toInt(), materiaisSelecionados, produtos) // Massa para projeto cimentícia (1 para cada 15m²)
                addMaterialByCode("1518", ceil(area / 15f).toInt(), materiaisSelecionados, produtos) // Fita cimentícia (1 para cada 15m²)
            } else {
                // Para parede: uma placa + fita e massa (sem bucha e parafuso)
                if (tipoPlaca != null) {
                    val quantidadePlacas = ceil(area / 2.88f).toInt()
                    val quantidadeComAcrescimo = ceil(quantidadePlacas * 1.1f).toInt() // Aumenta 10%
                    addMaterialByCode(tipoPlaca, quantidadeComAcrescimo, materiaisSelecionados, produtos) // Placa cimentícia
                }
                addMaterialByCode("582", ceil(area / 15f).toInt(), materiaisSelecionados, produtos) // Massa para projeto cimentícia (1 para cada 15m²)
                addMaterialByCode("1518", ceil(area / 15f).toInt(), materiaisSelecionados, produtos) // Fita cimentícia (1 para cada 15m²)
            }
            
            return materiaisSelecionados
        }
        
        fun getResumo(): Map<String, Any> {
            return mapOf(
                "metrosQuadrados" to metrosQuadrados,
                "peDireito" to peDireito,
                "comprimento" to "%.2f".format(comprimento),
                "tipoPlaca" to (tipoPlaca ?: "N/A"),
                "sistema" to sistema
            )
        }
    }
    
    // Classe para cálculo de réguas PVC
    class PVCCalculator(
        private val metrosQuadrados: Float,
        private val tipoRegua: String,
        private val produtos: List<MaterialItem>
    ) {
        private val larguraRegua = 0.20f // 20cm = 0.20m
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            
            // Calcula quantas réguas são necessárias
            // Área ÷ largura da régua (0.20m) = comprimento total necessário
            val comprimentoTotal = area / larguraRegua
            
            // Comprimento total ÷ comprimento da régua escolhida = quantidade de réguas
            val comprimentoRegua = obterComprimentoRegua(tipoRegua)
            val quantidadeReguas = ceil(comprimentoTotal / comprimentoRegua).toInt()
            
            // Adiciona 20% de margem
            val quantidadeComMargem = ceil(quantidadeReguas * 1.2f).toInt()
            
            addMaterialByCode(tipoRegua, quantidadeComMargem, materiaisSelecionados, produtos)
            
            return materiaisSelecionados
        }
        
        private fun obterComprimentoRegua(codigoRegua: String): Float {
            val comprimentos = mapOf(
                "1138" to 1.00f,   // FORRO PVC 1,00 METROS
                "1139" to 2.00f,   // FORRO PVC 2,00 METROS
                "740" to 6.50f,    // FORRO PVC 6,50 METROS
                "566" to 3.00f,    // FORRO PVC 3,00 METROS
                "571" to 6.00f,    // FORRO PVC 6,00 METROS
                "567" to 3.50f,    // FORRO PVC 3,50 METROS
                "741" to 5.50f,    // FORRO PVC 5.50 METROS
                "568" to 4.00f,    // FORRO PVC 4,00 METROS
                "570" to 5.00f,    // FORRO PVC 5.00 METROS
                "569" to 4.50f,    // FORRO PVC 4,50 METROS
                "572" to 7.00f,    // FORRO PVC 7,00 METROS
                "573" to 8.00f,    // FORRO PVC 8,00 METROS
                "1251" to 8.50f    // FORRO PVC 8,50 METROS
            )
            return comprimentos[codigoRegua] ?: 1.00f
        }
        
        fun getResumo(): Map<String, Any> {
            return mapOf(
                "metrosQuadrados" to metrosQuadrados,
                "tipoRegua" to tipoRegua,
                "larguraRegua" to larguraRegua,
                "comprimentoRegua" to obterComprimentoRegua(tipoRegua)
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
