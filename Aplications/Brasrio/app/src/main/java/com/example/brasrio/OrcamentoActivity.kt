package com.example.brasrio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil
import kotlin.math.roundToInt
import com.example.brasrio.CalculoUtils.*
import com.example.brasrio.ProdutoLoader
import android.util.Log

class OrcamentoActivity : AppCompatActivity() {

    private lateinit var step1: LinearLayout
    private lateinit var step1Drywall: LinearLayout
    private lateinit var step2: LinearLayout
    private lateinit var step3Metragem: LinearLayout
    private lateinit var step3Lista: LinearLayout
    private lateinit var resultado: LinearLayout
    private lateinit var resultContent: TextView
    private lateinit var materialInfo: TextView
    private lateinit var metragemInput: EditText
    private lateinit var peDireitoInput: EditText
    private lateinit var peDireitoLayout: LinearLayout
    private lateinit var materialsList: LinearLayout
    private lateinit var placaSelectionLayout: LinearLayout
    private lateinit var spinnerPlaca: Spinner

    private var selectedMaterial: String? = null
    private var drywallSubtype: String? = null
    private var selectedPlacaType: String? = null
    private val materiaisSelecionados = mutableListOf<CalculoUtils.MaterialItem>()

    // Lista de produtos - será carregada do JSON
    private lateinit var produtos: List<CalculoUtils.MaterialItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orcamento)

        // Carregar produtos do JSON
        ProdutoLoader.carregarProdutos(this)
        produtos = ProdutoLoader.getAllProducts()
        
        initializeViews()
        setupClickListeners()
        showStep1()
    }

    private fun initializeViews() {
        step1 = findViewById(R.id.step1)
        step1Drywall = findViewById(R.id.step1_drywall)
        step2 = findViewById(R.id.step2)
        step3Metragem = findViewById(R.id.step3_metragem)
        step3Lista = findViewById(R.id.step3_lista)
        resultado = findViewById(R.id.resultado)
        resultContent = findViewById(R.id.result_content)
        materialInfo = findViewById(R.id.material_info)
        metragemInput = findViewById(R.id.metragem_input)
        peDireitoInput = findViewById(R.id.pe_direito_input)
        peDireitoLayout = findViewById(R.id.pe_direito_layout)
        materialsList = findViewById(R.id.materials_list)
        placaSelectionLayout = findViewById(R.id.placa_selection_layout)
        spinnerPlaca = findViewById(R.id.spinner_placa)
    }

    private fun setupClickListeners() {
        // Botões do Step 1 - Tipo de material
        findViewById<Button>(R.id.btn_drywall).setOnClickListener { selectMaterialType("Drywall") }
        findViewById<Button>(R.id.btn_pvc).setOnClickListener { selectMaterialType("PVC") }
        findViewById<Button>(R.id.btn_isopor).setOnClickListener { selectMaterialType("Isopor") }
        findViewById<Button>(R.id.btn_painel).setOnClickListener { selectMaterialType("Painel") }
        findViewById<Button>(R.id.btn_piso).setOnClickListener { selectMaterialType("Piso") }

        // Botões do Step 1 Drywall
        findViewById<Button>(R.id.btn_teto).setOnClickListener { selectDrywallSubtype("Teto") }
        findViewById<Button>(R.id.btn_parede).setOnClickListener { selectDrywallSubtype("Parede") }
        findViewById<Button>(R.id.btn_back_material).setOnClickListener { backToMaterialChoice() }

        // Botões do Step 2 - Método de cálculo
        findViewById<Button>(R.id.btn_metragem).setOnClickListener { selectCalcMethod("metragem") }
        findViewById<Button>(R.id.btn_lista).setOnClickListener { selectCalcMethod("lista") }
        findViewById<Button>(R.id.btn_back_step2).setOnClickListener { backToStep2() }

        // Botões do Step 3
        findViewById<Button>(R.id.btn_calcular).setOnClickListener { 
            hideKeyboard()
            calcularPorMetragem() 
        }
        findViewById<Button>(R.id.btn_finalizar).setOnClickListener { 
            hideKeyboard()
            finalizarLista() 
        }
        findViewById<Button>(R.id.btn_back_step3_metragem).setOnClickListener { backToStep3Metragem() }
        findViewById<Button>(R.id.btn_back_step3_lista).setOnClickListener { backToStep3Lista() }
        findViewById<Button>(R.id.btn_novo_orcamento).setOnClickListener { novoOrcamento() }
        findViewById<Button>(R.id.btn_fazer_compra).setOnClickListener { fazerCompra() }

        // Configurar Spinner de placa
        setupPlacaSpinner()
    }

    private fun showStep1() {
        step1.visibility = View.VISIBLE
        step1Drywall.visibility = View.GONE
        step2.visibility = View.GONE
        step3Metragem.visibility = View.GONE
        step3Lista.visibility = View.GONE
        resultado.visibility = View.GONE
        materialInfo.text = ""
    }

    private fun selectMaterialType(type: String) {
        selectedMaterial = type
        drywallSubtype = null
        if (type == "Drywall") {
            step1.visibility = View.GONE
            step1Drywall.visibility = View.VISIBLE
        } else {
            step1.visibility = View.GONE
            step2.visibility = View.VISIBLE
        }
    }

    private fun selectDrywallSubtype(sub: String) {
        drywallSubtype = sub
        step1Drywall.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun backToMaterialChoice() {
        step1Drywall.visibility = View.GONE
        step1.visibility = View.VISIBLE
        selectedMaterial = null
        drywallSubtype = null
        selectedPlacaType = null
    }

    private fun backToStep2() {
        step2.visibility = View.GONE
        if (selectedMaterial == "Drywall") {
            step1Drywall.visibility = View.VISIBLE
        } else {
            step1.visibility = View.VISIBLE
        }
    }

    private fun backToStep3Metragem() {
        step3Metragem.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun backToStep3Lista() {
        step3Lista.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun setupPlacaSpinner() {
        val placas = listOf(
            "Selecione o tipo de placa...",
            "Placa drywall comum",
            "Drywall RU (Resistente à Umidade) 1,80 x 1,20",
            "Drywall RF (Resistente à fogo) 1,80 x 1,20"
        )
        
        val codigosPlacas = listOf("", "280", "177", "193")
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, placas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaca.adapter = adapter
        
        spinnerPlaca.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0) {
                    selectedPlacaType = codigosPlacas[position]
                } else {
                    selectedPlacaType = null
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedPlacaType = null
            }
        }
    }

    private fun selectCalcMethod(method: String) {
        step2.visibility = View.GONE
        if (method == "metragem") {
            step3Metragem.visibility = View.VISIBLE
            // Mostrar seleção de placa apenas para Drywall
            if (selectedMaterial == "Drywall") {
                placaSelectionLayout.visibility = View.VISIBLE
                // Mostrar campo de pé direito apenas para Drywall Parede
                if (drywallSubtype == "Parede") {
                    peDireitoLayout.visibility = View.VISIBLE
                } else {
                    peDireitoLayout.visibility = View.GONE
                }
            } else {
                placaSelectionLayout.visibility = View.GONE
                peDireitoLayout.visibility = View.GONE
            }
        } else {
            carregarListaMateriais()
            step3Lista.visibility = View.VISIBLE
        }
    }

    private fun carregarListaMateriais() {
        materialsList.removeAllViews()
        produtos.forEach { produto ->
            val itemView = layoutInflater.inflate(R.layout.item_material, materialsList, false)
            val quantidadeInput = itemView.findViewById<EditText>(R.id.quantidade_input)
            val nomeText = itemView.findViewById<TextView>(R.id.nome_material)
            
            nomeText.text = "[${produto.codigo}] ${produto.nome}"
            quantidadeInput.tag = produto.codigo
            
            materialsList.addView(itemView)
        }
    }



    private fun calcularPorMetragem() {
        val m2Text = metragemInput.text.toString()
        if (m2Text.isEmpty()) {
            Toast.makeText(this, "Digite uma metragem válida!", Toast.LENGTH_SHORT).show()
            return
        }
        
        val m2 = m2Text.toFloatOrNull()
        if (m2 == null || m2 <= 0) {
            Toast.makeText(this, "Digite uma metragem válida!", Toast.LENGTH_SHORT).show()
            return
        }
        


        // Obter pé direito se for Drywall Parede
        var peDireito = 2.7f // Valor padrão
        if (selectedMaterial == "Drywall" && drywallSubtype == "Parede") {
            val peDireitoText = peDireitoInput.text.toString()
            if (peDireitoText.isNotEmpty()) {
                peDireito = peDireitoText.toFloatOrNull() ?: 2.7f
            }
        }

        materiaisSelecionados.clear()

        when (selectedMaterial) {
            "Drywall" -> {
                if (drywallSubtype == null) {
                    Toast.makeText(this, "Escolha Teto ou Parede", Toast.LENGTH_SHORT).show()
                    return
                }
                
                if (selectedPlacaType == null) {
                    Toast.makeText(this, "Selecione o tipo de placa!", Toast.LENGTH_SHORT).show()
                    return
                }

                if (drywallSubtype == "Parede") {
                    // Usar a nova classe ParedeCalculator
                    try {
                        val calculator = ParedeCalculator(m2, peDireito, selectedPlacaType!!, produtos)
                        materiaisSelecionados.addAll(calculator.calcularMateriais())
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro no cálculo: ${e.message}", Toast.LENGTH_SHORT).show()
                        return
                    }
                } else {
                    // Cálculo para teto usando as funções melhoradas - EXATAMENTE como JavaScript
                    val sistema = "forro"
                    
                    // Placa escolhida - JavaScript: m2 / 2.88
                    addMaterialByCode(selectedPlacaType!!, ceil(m2 / 2.88f).toInt())
                    
                    // Parafusos (vêm em mil unidades - código 1521)
                    val parafusosNecessarios = calculateParafusos(m2, sistema)
                    addMaterialByCode("1521", ceil(parafusosNecessarios / 1000f).toInt())
                    
                    // Fita telada (vêm em rolos de 90m - código 1516)
                    val fitaNecessaria = calculateFita(m2, sistema)
                    addMaterialByCode("1516", ceil(fitaNecessaria / 90f).toInt())
                    
                    // Massa para acabamento
                    val massaNecessaria = calculateMassa(m2, sistema)
                    if (massaNecessaria <= 5) {
                        addMaterialByCode("698", 1) // 5kg
                    } else {
                        val qtd28kg = ceil(massaNecessaria / 28f).toInt()
                        addMaterialByCode("431", qtd28kg) // 28kg
                    }

                    // JavaScript: (m2 * 0.5) / 12 - sem ceil
                    addMaterialByCode("33", ((m2 * 0.5f) / 12f).toInt()) // Arame
                    
                    // JavaScript: m2 / 2 - sem ceil (o ceil é aplicado na função addMaterialByCode)
                    addMaterialByCode("366", (m2 / 2f).toInt()) // Perfil F530 - m2/2 sem ceil (como JavaScript)
                    
                    // JavaScript: m2 * 0.05 - sem ceil
                    addMaterialByCode("667", (m2 * 0.05f).toInt()) // Cantoneira
                    // JavaScript: m2 * 0.02 - sem ceil
                    addMaterialByCode("32", (m2 * 0.02f).toInt()) // Regulador
                    // JavaScript: m2 * 0.02 - sem ceil
                    addMaterialByCode("668", (m2 * 0.02f).toInt()) // Tabica
                }
            }
            "PVC" -> {
                addMaterialByCode("163", (m2 / 1.2f).toInt()) // Forro PVC
                addMaterialByCode("574", (m2 / 6f).toInt()) // Roda forro
                addMaterialByCode("146", (m2 / 6f).toInt()) // Roda forro U
                addMaterialByCode("173", ((m2 * 0.5f) / 100f).toInt()) // Parafuso Frangeado
            }
            "Isopor" -> {
                addMaterialByCode("68", (m2 / 1.2f).toInt()) // Forro isopor
                addMaterialByCode("19", ((m2 * 5) / 100f).toInt()) // Parafuso ponta agulha
                addMaterialByCode("267", (m2 * 2).toInt()) // Presilha bigodinho
                addMaterialByCode("164", (m2 * 0.5f).toInt()) // Pino Cadeirinha
                addMaterialByCode("216", (m2 / 4f).toInt()) // Travessa perfil clicado
                addMaterialByCode("1365", (m2 / 4f).toInt()) // Travessa clicado 1,25
                addMaterialByCode("1366", (m2 / 4f).toInt()) // Travessa clicado 0,625
                addMaterialByCode("1175", (m2 / 15f).toInt()) // Cola Selante PU
            }
            "Painel" -> {
                // Sistema divisória
                val sistema = "divisoria"
                
                addMaterialByCode("79", (m2 / 2.88f).toInt()) // Painel Eucatex
                
                // Perfis para divisória
                val perfisNecessarios = calculatePerfis(m2, sistema)
                addMaterialByCode("89", ceil(perfisNecessarios * 0.3f).toInt()) // Guia Baixa
                addMaterialByCode("87", ceil(perfisNecessarios * 0.4f).toInt()) // NTR Travessa 1185M
                
                addMaterialByCode("107", (m2 / 0.84f).toInt()) // Batente Horizontal
                addMaterialByCode("110", (m2 / 2.14f).toInt()) // Batente Vertical
                addMaterialByCode("95", (m2 / 1.18f).toInt()) // Leito Branco
                addMaterialByCode("98", (m2 / 1.18f).toInt()) // Baguete Branco
                
                // Parafusos para divisória
                val parafusosNecessarios = calculateParafusos(m2, sistema)
                addMaterialByCode("142", ceil(parafusosNecessarios / 100f).toInt()) // Parafuso ponta agulha 13 cento
                
                // Fita telada para divisória
                val fitaNecessaria = calculateFita(m2, sistema)
                addMaterialByCode("1516", ceil(fitaNecessaria / 90f).toInt()) // Fita telada branca 90m
                
                // Massa para acabamento divisória
                val massaNecessaria = calculateMassa(m2, sistema)
                if (massaNecessaria <= 5) {
                    addMaterialByCode("698", 1) // 5kg
                } else {
                    val qtd28kg = ceil(massaNecessaria / 28f).toInt()
                    addMaterialByCode("431", qtd28kg) // 28kg
                }
            }
            "Piso" -> {
                val melhor = escolherMelhorPiso(m2)
                addMaterialByCode(melhor.codigo, melhor.quantidade)
            }
        }

        step3Metragem.visibility = View.GONE
        mostrarResultado()
    }

    private fun addMaterialByCode(code: String, quantidade: Int) {
        CalculoUtils.addMaterialByCode(code, quantidade, materiaisSelecionados, produtos)
    }

    private fun addMaterialByCode(code: String, quantidade: Int, materiaisList: MutableList<CalculoUtils.MaterialItem>) {
        CalculoUtils.addMaterialByCode(code, quantidade, materiaisList, produtos)
    }

    private fun finalizarLista() {
        materiaisSelecionados.clear()
        
        for (i in 0 until materialsList.childCount) {
            val itemView = materialsList.getChildAt(i)
            val quantidadeInput = itemView.findViewById<EditText>(R.id.quantidade_input)
            val nomeText = itemView.findViewById<TextView>(R.id.nome_material)
            
            val quantidade = quantidadeInput.text.toString().toFloatOrNull() ?: 0f
            if (quantidade > 0) {
                val codigo = quantidadeInput.tag as String
                val nome = nomeText.text.toString().substringAfter("] ")
                materiaisSelecionados.add(
                    MaterialItem(
                        codigo = codigo,
                        nome = nome,
                        quantidade = quantidade.toInt()
                    )
                )
            }
        }
        
        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um material!", Toast.LENGTH_SHORT).show()
            return
        }
        
        step3Lista.visibility = View.GONE
        mostrarResultado()
    }

    private fun mostrarResultado() {
        // Configurar informações do material
        val materialInfoText = StringBuilder()
        if (selectedMaterial != null && drywallSubtype != null) {
            materialInfoText.append("Material: $selectedMaterial - $drywallSubtype")
        } else if (selectedMaterial != null) {
            materialInfoText.append("Material: $selectedMaterial")
        }
        
        // Verificar se é cálculo por metragem ou seleção manual
        val isMetragem = metragemInput.text.isNotEmpty() || peDireitoInput.text.isNotEmpty()
        
        if (isMetragem) {
            // Adicionar informações específicas para Drywall Parede
            if (selectedMaterial == "Drywall" && drywallSubtype == "Parede") {
                val peDireito = peDireitoInput.text.toString().toFloatOrNull() ?: 2.7f
                val m2 = metragemInput.text.toString().toFloatOrNull() ?: 0f
                if (m2 > 0) {
                    val comprimento = m2 / peDireito
                    materialInfoText.append(" | ${m2}m² - Pé direito: ${peDireito}m (Comprimento: ${"%.2f".format(comprimento)}m)")
                }
            } else {
                val m2 = metragemInput.text.toString().toFloatOrNull() ?: 0f
                if (m2 > 0) {
                    materialInfoText.append(" | Metragem: ${m2}m²")
                }
            }
        } else {
            // Seleção manual da lista
            materialInfoText.append(" | Seleção manual")
        }
        
        materialInfo.text = materialInfoText.toString()
        
        // Configurar resultado
        val resultText = StringBuilder()
        resultText.append("Materiais necessários:\n\n")
        
        materiaisSelecionados.forEach { material ->
            resultText.append("• [${material.codigo}] ${material.quantidade}x ${material.nome}\n")
        }
        
        resultText.append("\n⚠️ ATENÇÃO:\n")
        resultText.append("Este cálculo é apenas uma estimativa e não considera características específicas do local de instalação nem possíveis perdas.\n\n")
        resultText.append("Utilize-o apenas como referência. Para informações precisas, recomenda-se consultar um instalador de confiança.")
        
        resultContent.text = resultText.toString()
        resultado.visibility = View.VISIBLE
    }

    private fun novoOrcamento() {
        selectedMaterial = null
        drywallSubtype = null
        selectedPlacaType = null
        materiaisSelecionados.clear()
        metragemInput.text.clear()
        peDireitoInput.text.clear()
        peDireitoLayout.visibility = View.GONE
        materialInfo.text = ""
        
        // Resetar Spinner de placa
        spinnerPlaca.setSelection(0)
        
        showStep1()
    }

    private fun fazerCompra() {
        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(this, "Nenhum material selecionado!", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter dados do cliente das SharedPreferences
        val prefs = getSharedPreferences("BRASRIO_PREFS", MODE_PRIVATE)
        val nome = prefs.getString("nome", "") ?: ""
        val cpfCnpj = prefs.getString("cpfCnpj", "") ?: ""
        val telefone = prefs.getString("telefone", "") ?: ""

        if (nome.isEmpty() || cpfCnpj.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Dados do cliente não encontrados!", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar mensagem para o WhatsApp
        val mensagem = criarMensagemWhatsApp(nome, cpfCnpj, telefone)
        
        // Abrir WhatsApp
        abrirWhatsApp(mensagem)
    }

    private fun criarMensagemWhatsApp(nome: String, cpfCnpj: String, telefone: String): String {
        val sb = StringBuilder()
        
        sb.append("🛒 *PEDIDO DE COMPRA - BRASRIO*\n\n")
        sb.append("*Dados do Cliente:*\n")
        sb.append("👤 Nome: $nome\n")
        sb.append("📄 CPF/CNPJ: $cpfCnpj\n")
        sb.append("📱 Telefone: $telefone\n\n")
        
        sb.append("*Materiais Solicitados:*\n")
        materiaisSelecionados.forEach { material ->
            sb.append("• [${material.codigo}] ${material.quantidade}x ${material.nome}\n")
        }
        
        sb.append("\n📋 *Total de itens:* ${materiaisSelecionados.size}\n")
        sb.append("📅 Data: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.forLanguageTag("pt-BR")).format(java.util.Date())}\n\n")
        
        sb.append("⚠️ *Observação:* Este é um orçamento estimativo. Para informações precisas sobre preços e disponibilidade, entre em contato conosco.")
        
        return sb.toString()
    }

    private fun abrirWhatsApp(mensagem: String) {
        try {
            val numeroWhatsApp = "5521971252304"
            val mensagemCodificada = Uri.encode(mensagem)
            
            // Tentar primeiro com o esquema whatsapp://
            try {
                val uri = Uri.parse("whatsapp://send?phone=$numeroWhatsApp&text=$mensagemCodificada")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                return
            } catch (e: Exception) {
                // Se falhar, tentar com https://wa.me
                val uri = Uri.parse("https://wa.me/$numeroWhatsApp?text=$mensagemCodificada")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp não está instalado. Instale o WhatsApp para continuar.", Toast.LENGTH_LONG).show()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
