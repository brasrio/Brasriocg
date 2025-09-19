package com.example.brasrio

import kotlin.math.ceil

// Classe para cálculo de placas cimentícias
class CimenticiaCalculator(
        private val metrosQuadrados: Float,
        private val peDireito: Float,
        private val tipoPlaca: String,
        private val produtos: List<MaterialItem>
    ) {
        private val comprimento: Float = metrosQuadrados / peDireito // Calcula comprimento automaticamente
        
        fun calcularArea(): Float = metrosQuadrados
        
        fun calcularMateriais(): List<MaterialItem> {
            val area = calcularArea()
            val placaConfig = com.example.brasrio.CalculoUtils.MATERIAIS_PAREDE[tipoPlaca]
            
            if (placaConfig == null) {
                throw IllegalArgumentException("Tipo de placa não encontrado")
            }
            
            val materiaisSelecionados = mutableListOf<MaterialItem>()
            val sistema = "cimenticia"
            
            // Placa escolhida - cálculo baseado na área real
            val quantidadePlacas = ceil((area / placaConfig.area) * 1.2f).toInt()
            com.example.brasrio.CalculoUtils.addMaterialByCode(tipoPlaca, quantidadePlacas, materiaisSelecionados, produtos)
            
            // Parafusos específicos para cimentícia
            val parafusosNecessarios = com.example.brasrio.CalculoUtils.calculateParafusos(area, sistema)
            com.example.brasrio.CalculoUtils.addMaterialByCode("1521", ceil(parafusosNecessarios / 1000f).toInt(), materiaisSelecionados, produtos)
            
            // Fita para cimentícia
            val fitaNecessaria = com.example.brasrio.CalculoUtils.calculateFita(area, sistema)
            com.example.brasrio.CalculoUtils.addMaterialByCode("1518", ceil(fitaNecessaria / 50f).toInt(), materiaisSelecionados, produtos)
            
            // Massa específica para cimentícia
            val massaNecessaria = com.example.brasrio.CalculoUtils.calculateMassa(area, sistema)
            if (massaNecessaria <= 4) {
                com.example.brasrio.CalculoUtils.addMaterialByCode("582", 1, materiaisSelecionados, produtos) // 4kg
            } else {
                val qtd4kg = ceil(massaNecessaria / 4f).toInt()
                com.example.brasrio.CalculoUtils.addMaterialByCode("582", qtd4kg, materiaisSelecionados, produtos) // 4kg
            }
            
            // Perfis para cimentícia
            val montantesNecessarios = ceil(comprimento / 0.6f).toInt() + 1
            val guiasNecessarias = ceil((comprimento * 2) / 3f).toInt()
            
            com.example.brasrio.CalculoUtils.addMaterialByCode("388", ceil(guiasNecessarias * 3f).toInt(), materiaisSelecionados, produtos) // Guia 48
            com.example.brasrio.CalculoUtils.addMaterialByCode("387", ceil(montantesNecessarios * 1.1f).toInt(), materiaisSelecionados, produtos) // Montante 48
            com.example.brasrio.CalculoUtils.addMaterialByCode("192", ceil((area * 2f) / 100f).toInt(), materiaisSelecionados, produtos) // Bucha 6
            com.example.brasrio.CalculoUtils.addMaterialByCode("173", ceil((area * 0.5f) / 100f).toInt(), materiaisSelecionados, produtos) // Parafuso Frangeado
            
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
