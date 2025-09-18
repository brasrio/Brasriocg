package com.example.brasrio

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.IOException

object ProdutoLoader {
    
    private var produtos: Map<String, List<MaterialItem>> = emptyMap()
    
    // Função para carregar produtos do JSON
    fun carregarProdutos(context: Context): Map<String, List<MaterialItem>> {
        if (produtos.isNotEmpty()) {
            return produtos
        }
        
        try {
            val jsonString = context.assets.open("produtos.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            
            val produtosMap = mutableMapOf<String, List<MaterialItem>>()
            
            // Processar cada categoria
            val categorias = listOf("comuns", "drywall", "cimenticia", "pvc", "isopor", "painel", "pisos")
            
            categorias.forEach { categoria ->
                if (jsonObject.has(categoria)) {
                    val array = jsonObject.getJSONArray(categoria)
                    val produtosCategoria = mutableListOf<MaterialItem>()
                    
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val codigo = item.getString("codigo")
                        val nome = item.getString("nome")
                        
                        produtosCategoria.add(MaterialItem(codigo, nome))
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
    fun getAllProducts(): List<MaterialItem> {
        return CalculoUtils.getAllProducts(produtos)
    }
    
    // Função para encontrar produto por código
    fun findProductByCode(code: String): MaterialItem? {
        val todosProdutos = getAllProducts()
        return CalculoUtils.findProductByCode(code, todosProdutos)
    }
    
    // Produtos padrão caso o JSON não seja encontrado - baseados no site
    private fun carregarProdutosPadrao(): Map<String, List<MaterialItem>> {
        val produtosPadrao = mapOf(
            "comuns" to listOf(
                MaterialItem("33", "Arame de 10"),
                MaterialItem("99", "Baguete Preto"),
                MaterialItem("192", "Bucha 6"),
                MaterialItem("667", "Cantoneira 25x30"),
                MaterialItem("1363", "Cantoneira Branca Home"),
                MaterialItem("1175", "Cola Selante PU"),
                MaterialItem("190", "Conector Perfil"),
                MaterialItem("158", "Fechadura preta volga"),
                MaterialItem("166", "Fincapino amarelo"),
                MaterialItem("1518", "Fita Cimenticia 51MM"),
                MaterialItem("1515", "Fita telada azul 90mt Home"),
                MaterialItem("1516", "Fita telada branca 90mt Home"),
                MaterialItem("68", "Forro isopor 20mm"),
                MaterialItem("431", "Massa kolimar 28kg"),
                MaterialItem("388", "Guia 48"),
                MaterialItem("256", "La de Pet"),
                MaterialItem("1040", "La de Rocha"),
                MaterialItem("1362", "La de vidro"),
                MaterialItem("96", "Leito preto"),
                MaterialItem("698", "Massa kolimar 5kg"),
                MaterialItem("387", "Montante 48"),
                MaterialItem("81", "NTR Preto"),
                MaterialItem("1547", "Painel divisoria cristal (cinza)"),
                MaterialItem("1546", "Painel divisória"),
                MaterialItem("142", "Parafuso ponta agulha 13 cento"),
                MaterialItem("1521", "Parafuso ponta agulha GN25 CX com mil"),
                MaterialItem("173", "Parafuso Frangeado 45"),
                MaterialItem("1364", "Perfil Clicado"),
                MaterialItem("366", "Perfil F530 barra"),
                MaterialItem("164", "Pino Cadeirinha"),
                MaterialItem("280", "Placa drywall comum"),
                MaterialItem("222", "Porta divisoria cristal"),
                MaterialItem("267", "Presilha bigodinho para forro isopor"),
                MaterialItem("32", "Regulador F530"),
                MaterialItem("668", "Tabica barra"),
                MaterialItem("216", "Travessa perfil clicado branco"),
                MaterialItem("1365", "Travessa clicado 1,25"),
                MaterialItem("1366", "Travessa clicado 0,625"),
                // Novos produtos baseados no site
                MaterialItem("464", "Painel wall"),
                MaterialItem("582", "Massa para projeto cimentícia"),
                MaterialItem("947", "MASSA NIVELADORA PISO SC/ 4KG MAPEI"),
                MaterialItem("447", "MANTA P/ PISO LAMINADO 1,20ML")
            ),
            "drywall" to listOf(
                MaterialItem("280", "Drywall ST Branco 1,80 x 1,20"),
                MaterialItem("177", "Drywall RU (Resistente à Umidade) 1,80 x 1,20"),
                MaterialItem("193", "Drywall RF (Resistente à fogo) 1,80 x 1,20")
            ),
            "cimenticia" to listOf(
                MaterialItem("181", "PLACA CIMENTICIA 2.40 X 1.20 8MM DECORLIT"),
                MaterialItem("182", "PLACA CIMENTICIA 2.40 X 1.20 10MM DECORLIT"),
                MaterialItem("263", "PLACA CIMENTICIA 2.40 X 1.20 6MM DECORLIT"),
                MaterialItem("1172", "PLACA CIMENTICIA 2.40 X 1.20 12MM DECORLIT")
            ),
            "pvc" to listOf(
                MaterialItem("574", "RODA FORRO MOLDURA 6 MTS"),
                MaterialItem("146", "Roda forro U"),
                MaterialItem("163", "Forro pvc Modular 10mm"),
                MaterialItem("1138", "FORRO PVC 1,00 METROS ( 7MM )"),
                MaterialItem("1139", "FORRO PVC 2,00 METROS ( 7MM )"),
                MaterialItem("740", "FORRO PVC 6,50 METROS ( 7MM )"),
                MaterialItem("566", "FORRO PVC 3,00 METROS ( 7MM )"),
                MaterialItem("571", "FORRO PVC 6,00 METROS ( 7MM )"),
                MaterialItem("567", "FORRO PVC 3,50 METROS ( 7MM )"),
                MaterialItem("741", "FORRO PVC 5.50 METROS ( 7MM )"),
                MaterialItem("568", "FORRO PVC 4,00 METROS ( 7MM )"),
                MaterialItem("570", "FORRO PVC 5.00 METROS ( 7MM )"),
                MaterialItem("569", "FORRO PVC 4,50 METROS ( 7MM )"),
                MaterialItem("572", "FORRO PVC 7,00 METROS ( 7MM )"),
                MaterialItem("573", "FORRO PVC 8,00 METROS ( 7MM )"),
                MaterialItem("1251", "FORRO PVC 8,50 METROS ( 7MM )")
            ),
            "isopor" to listOf(
                MaterialItem("19", "Parafuso ponta agulha 13")
            ),
            "painel" to listOf(
                MaterialItem("79", "Painel Eucatex (Divisória Naval)"),
                MaterialItem("89", "Guia Baixa (U) Branca 3.00 mts"),
                MaterialItem("87", "NTR Travessa 1185 M"),
                MaterialItem("81", "NTR Travessa 3M"),
                MaterialItem("107", "Batente Horizontal 0,84 M"),
                MaterialItem("110", "Batente Vertical 2,14 M"),
                MaterialItem("95", "Leito Branco 1,18 mts"),
                MaterialItem("98", "Baguete Branco 1,18 mts"),
                MaterialItem("102", "Requadro Horizontal 0,81 M"),
                MaterialItem("101", "Requadro Vertical 2,11 M")
            ),
            "pisos" to listOf(
                // Pisos Vinílicos
                MaterialItem("1574", "PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA - 18 REGUAS - 2MM - 3,90 M2"),
                MaterialItem("1570", "PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA - 18 REGUAS - 2MM - 3,90 M2"),
                MaterialItem("1599", "PISO VINILICO RUFFINO BRAVO COR ANGELIM - 3MM - 2,6 M2"),
                MaterialItem("1575", "PISO VINILICO RUFFINO NOBILE COLADO BAOBA 2MM - 3,90M2"),
                MaterialItem("1576", "PISO VINILICO RUFFINO NOBILE COLADO DAMASCO 2MM - 3,90M2"),
                // Pisos Laminados
                MaterialItem("1102", "PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM - CAIXA C/ 2,41M2"),
                MaterialItem("1236", "PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO CX C/ 2,51M2"),
                MaterialItem("1401", "PISO LAMINADO QUICK STEP PREMIERE MOCHA - 2,84M2")
            )
        )
        
        produtos = produtosPadrao
        return produtosPadrao
    }
}
