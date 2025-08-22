package com.example.brasrio

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.IOException

object ProdutoLoader {
    
    private var produtos: Map<String, List<CalculoUtils.MaterialItem>> = emptyMap()
    
    // Função para carregar produtos do JSON
    fun carregarProdutos(context: Context): Map<String, List<CalculoUtils.MaterialItem>> {
        if (produtos.isNotEmpty()) {
            return produtos
        }
        
        try {
            val jsonString = context.assets.open("produtos.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            
            val produtosMap = mutableMapOf<String, List<CalculoUtils.MaterialItem>>()
            
            // Processar cada categoria
            val categorias = listOf("comuns", "drywall", "pvc", "isopor", "painel", "pisos")
            
            categorias.forEach { categoria ->
                if (jsonObject.has(categoria)) {
                    val array = jsonObject.getJSONArray(categoria)
                    val produtosCategoria = mutableListOf<CalculoUtils.MaterialItem>()
                    
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val codigo = item.getString("codigo")
                        val nome = item.getString("nome")
                        
                        produtosCategoria.add(CalculoUtils.MaterialItem(codigo, nome))
                    }
                    
                    produtosMap[categoria] = produtosCategoria
                }
            }
            
            produtos = produtosMap
            Log.d("ProdutoLoader", "Produtos carregados com sucesso: ${produtos.size} categorias")
            return produtos
            
        } catch (e: IOException) {
            Log.e("ProdutoLoader", "Erro ao ler arquivo produtos.json", e)
            return carregarProdutosPadrao()
        } catch (e: Exception) {
            Log.e("ProdutoLoader", "Erro ao processar produtos.json", e)
            return carregarProdutosPadrao()
        }
    }
    
    // Função para obter todos os produtos em uma lista plana
    fun getAllProducts(): List<CalculoUtils.MaterialItem> {
        return CalculoUtils.getAllProducts(produtos)
    }
    
    // Função para encontrar produto por código
    fun findProductByCode(code: String): CalculoUtils.MaterialItem? {
        val todosProdutos = getAllProducts()
        return CalculoUtils.findProductByCode(code, todosProdutos)
    }
    
    // Produtos padrão caso o JSON não seja encontrado
    private fun carregarProdutosPadrao(): Map<String, List<CalculoUtils.MaterialItem>> {
        val produtosPadrao = mapOf(
            "comuns" to listOf(
                CalculoUtils.MaterialItem("33", "Arame de 10"),
                CalculoUtils.MaterialItem("99", "Baguete Preto"),
                CalculoUtils.MaterialItem("192", "Bucha 6"),
                CalculoUtils.MaterialItem("667", "Cantoneira 25x30"),
                CalculoUtils.MaterialItem("1363", "Cantoneira Branca Home"),
                CalculoUtils.MaterialItem("1175", "Cola Selante PU"),
                CalculoUtils.MaterialItem("190", "Conector Perfil"),
                CalculoUtils.MaterialItem("158", "Fechadura preta volga"),
                CalculoUtils.MaterialItem("166", "Fincapino amarelo"),
                CalculoUtils.MaterialItem("1518", "Fita Cimenticia 51MM"),
                CalculoUtils.MaterialItem("1515", "Fita telada azul 90mt Home"),
                CalculoUtils.MaterialItem("1516", "Fita telada branca 90mt Home"),
                CalculoUtils.MaterialItem("68", "Forro isopor 20mm"),
                CalculoUtils.MaterialItem("431", "Massa kolimar 28kg"),
                CalculoUtils.MaterialItem("388", "Guia 48"),
                CalculoUtils.MaterialItem("256", "La de Pet"),
                CalculoUtils.MaterialItem("1040", "La de Rocha"),
                CalculoUtils.MaterialItem("1362", "La de vidro"),
                CalculoUtils.MaterialItem("96", "Leito preto"),
                CalculoUtils.MaterialItem("698", "Massa kolimar 5kg"),
                CalculoUtils.MaterialItem("387", "Montante 48"),
                CalculoUtils.MaterialItem("81", "NTR Preto"),
                CalculoUtils.MaterialItem("1547", "Painel divisoria cristal (cinza)"),
                CalculoUtils.MaterialItem("1546", "Painel divisória"),
                CalculoUtils.MaterialItem("142", "Parafuso ponta agulha 13 cento"),
                CalculoUtils.MaterialItem("1521", "Parafuso ponta agulha GN25 CX com mil"),
                CalculoUtils.MaterialItem("173", "Parafuso Frangeado 45"),
                CalculoUtils.MaterialItem("1364", "Perfil Clicado"),
                CalculoUtils.MaterialItem("366", "Perfil F530 barra"),
                CalculoUtils.MaterialItem("164", "Pino Cadeirinha"),
                CalculoUtils.MaterialItem("280", "Placa drywall comum"),
                CalculoUtils.MaterialItem("222", "Porta divisoria cristal"),
                CalculoUtils.MaterialItem("267", "Presilha bigodinho para forro isopor"),
                CalculoUtils.MaterialItem("32", "Regulador F530"),
                CalculoUtils.MaterialItem("668", "Tabica barra"),
                CalculoUtils.MaterialItem("216", "Travessa perfil clicado branco"),
                CalculoUtils.MaterialItem("1365", "Travessa clicado 1,25"),
                CalculoUtils.MaterialItem("1366", "Travessa clicado 0,625")
            ),
            "drywall" to listOf(
                CalculoUtils.MaterialItem("177", "Drywall RU (Resistente à Umidade) 1,80 x 1,20"),
                CalculoUtils.MaterialItem("193", "Drywall RF (Resistente à fogo) 1,80 x 1,20")
            ),
            "pvc" to listOf(
                CalculoUtils.MaterialItem("574", "RODA FORRO MOLDURA 6 MTS"),
                CalculoUtils.MaterialItem("146", "Roda forro U"),
                CalculoUtils.MaterialItem("163", "Forro pvc Modular 10mm")
            ),
            "isopor" to listOf(
                CalculoUtils.MaterialItem("19", "Parafuso ponta agulha 13")
            ),
            "painel" to listOf(
                CalculoUtils.MaterialItem("79", "Painel Eucatex (Divisória Naval)"),
                CalculoUtils.MaterialItem("89", "Guia Baixa (U) Branca 3.00 mts"),
                CalculoUtils.MaterialItem("87", "NTR Travessa 1185 M"),
                CalculoUtils.MaterialItem("107", "Batente Horizontal 0,84 M"),
                CalculoUtils.MaterialItem("110", "Batente Vertical 2,14 M"),
                CalculoUtils.MaterialItem("95", "Leito Branco 1,18 mts"),
                CalculoUtils.MaterialItem("98", "Baguete Branco 1,18 mts")
            ),
            "pisos" to listOf(
                CalculoUtils.MaterialItem("1599", "PISO VINILICO RUFFINO BRAVO COR ANGELIM - 3MM - 2,6 M2"),
                CalculoUtils.MaterialItem("1575", "PISO VINILICO RUFFINO NOBILE COLADO BAOBA 2MM - 3,90M2")
            )
        )
        
        produtos = produtosPadrao
        return produtosPadrao
    }
}
