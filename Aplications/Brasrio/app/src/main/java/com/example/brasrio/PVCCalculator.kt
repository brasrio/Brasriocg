package com.example.brasrio

import kotlin.math.ceil

// Classe para cálculo de PVC
class PVCCalculator(
        private val metrosQuadrados: Float,
        private val tipoRegua: String,
        private val produtos: List<MaterialItem>
    ) {
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            
            // Cálculo baseado no tipo de régua
            val larguraRegua = when (tipoRegua) {
                "163" -> 0.10f // 10cm
                "1138" -> 0.10f // 10cm
                "1139" -> 0.10f // 10cm
                "740" -> 0.10f // 10cm
                "566" -> 0.10f // 10cm
                "571" -> 0.10f // 10cm
                "567" -> 0.10f // 10cm
                "741" -> 0.10f // 10cm
                "568" -> 0.10f // 10cm
                "570" -> 0.10f // 10cm
                "569" -> 0.10f // 10cm
                "572" -> 0.10f // 10cm
                "573" -> 0.10f // 10cm
                "1251" -> 0.10f // 10cm
                else -> 0.10f
            }
            
            val comprimentoRegua = obterComprimentoRegua(tipoRegua)
            val areaRegua = larguraRegua * comprimentoRegua
            val quantidadeReguas = ceil(area / areaRegua * 1.1f).toInt()
            
            // Adicionar réguas
            com.example.brasrio.CalculoUtils.addMaterialByCode(tipoRegua, quantidadeReguas, materiaisSelecionados, produtos)
            
            // Parafusos para PVC
            val parafusosNecessarios = ceil(area * 0.5f).toInt()
            com.example.brasrio.CalculoUtils.addMaterialByCode("19", ceil(parafusosNecessarios / 100f).toInt(), materiaisSelecionados, produtos)
            
            return materiaisSelecionados
        }
        
        private fun obterComprimentoRegua(tipoRegua: String): Float {
            return when (tipoRegua) {
                "163" -> 6.0f // 6 metros
                "1138" -> 1.0f // 1 metro
                "1139" -> 2.0f // 2 metros
                "740" -> 6.5f // 6.5 metros
                "566" -> 3.0f // 3 metros
                "571" -> 6.0f // 6 metros
                "567" -> 3.5f // 3.5 metros
                "741" -> 5.5f // 5.5 metros
                "568" -> 4.0f // 4 metros
                "570" -> 5.0f // 5 metros
                "569" -> 4.5f // 4.5 metros
                "572" -> 7.0f // 7 metros
                "573" -> 8.0f // 8 metros
                "1251" -> 8.5f // 8.5 metros
                else -> 6.0f
            }
        }
        
        fun getResumo(): Map<String, Any> {
            return mapOf(
                "metrosQuadrados" to metrosQuadrados,
                "tipoRegua" to tipoRegua,
                "larguraRegua" to 0.10f,
                "comprimentoRegua" to obterComprimentoRegua(tipoRegua)
            )
        }
    }
