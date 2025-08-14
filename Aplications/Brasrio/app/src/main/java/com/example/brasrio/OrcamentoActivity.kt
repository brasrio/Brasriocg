package com.example.brasrio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrcamentoActivity : AppCompatActivity() {

    private lateinit var step1: LinearLayout
    private lateinit var step1Drywall: LinearLayout
    private lateinit var step2: LinearLayout
    private lateinit var step3Metragem: LinearLayout
    private lateinit var step3Lista: LinearLayout
    private lateinit var resultado: LinearLayout
    private lateinit var resultContent: TextView
    private lateinit var metragemInput: EditText
    private lateinit var materialsList: LinearLayout
    private lateinit var placaSelectionLayout: LinearLayout
    private lateinit var spinnerPlaca: Spinner

    private var selectedMaterial: String? = null
    private var drywallSubtype: String? = null
    private var selectedPlacaType: String? = null
    private val materiaisSelecionados = mutableListOf<MaterialItem>()

    // Lista de produtos
    private val produtos = listOf(
        // Itens mais comuns
        MaterialItem("33", "Arame de 10"),
        MaterialItem("99", "Baguete Preto"),
        MaterialItem("192", "Bucha 6"),
        MaterialItem("667", "Cantoneira 25x30"),
        MaterialItem("1363", "Cantoneira Branca Home"),
        MaterialItem("67", "Conector perfil"),
        MaterialItem("158", "Fechadura preta volga"),
        MaterialItem("166", "Fincapino amarelo"),
        MaterialItem("1518", "Fita Cimenticia 51MM"),
        MaterialItem("1515", "Fita telada azul 90mt Home"),
        MaterialItem("1516", "Fita telada branca 90mt Home"),
        MaterialItem("388", "Guia 48"),
        MaterialItem("96", "Leito preto"),
        MaterialItem("431", "Massa kolimar 28kg"),
        MaterialItem("387", "Montante 48"),
        MaterialItem("81", "NTR Preto"),
        MaterialItem("1547", "Painel divisoria cristal (cinza)"),
        MaterialItem("1546", "Painel divisória"),
        MaterialItem("1175", "COLA SELANTE PU"),
        MaterialItem("142", "Parafuso ponta agulha 13 cento"),
        MaterialItem("1521", "Parafuso ponta agulha GN25 CX com mil"),
        MaterialItem("1364", "Perfil Clicado"),
        MaterialItem("366", "Perfil F530 barra"),
        MaterialItem("164", "Pino Cadeirinha"),
        MaterialItem("280", "Drywall ST Branco 1,80 x 1,20"),
        MaterialItem("177", "Drywall RU (Resistente à Umidade) 1,80 x 1,20"),
        MaterialItem("193", "Drywall RF (Resistente à fogo) 1,80 x 1,20"),
        MaterialItem("222", "Porta divisoria cristal"),
        MaterialItem("173", "Parafuso Frangeado 45"),
        MaterialItem("698", "Massa kolimar 5kg"),
        MaterialItem("267", "Presilha bigodinho para forro isopor"),
        MaterialItem("32", "Regulador F530"),
        MaterialItem("668", "Tabica barra"),
        MaterialItem("216", "Travessa perfil clicado branco"),

        // PVC
        MaterialItem("574", "RODA FORRO MOLDURA 6 MTS"),
        MaterialItem("146", "Roda forro U"),
        MaterialItem("163", "Forro pvc Modular 10mm"),

        // Isopor
        MaterialItem("68", "Forro isopor 20mm"),
        MaterialItem("19", "Parafuso ponta agulha 13"),
        MaterialItem("1365", "Travessa clicado 1,25"),
        MaterialItem("1366", "Travessa clicado 0,625"),

        // Painel Eucatex
        MaterialItem("79", "Painel Eucatex (Divisória Naval)"),
        MaterialItem("89", "Guia Baixa (U) Branca 3.00 mts"),
        MaterialItem("81", "NTR Travessa 3M"),
        MaterialItem("87", "NTR Travessa 1185 M"),
        MaterialItem("107", "Batente Horizontal 0,84 M"),
        MaterialItem("110", "Batente Vertical 2,14 M"),
        MaterialItem("95", "Leito Branco 1,18 mts"),
        MaterialItem("98", "Baguete Branco 1,18 mts"),
        MaterialItem("86", "NTR Travessa 1185 M"),

        // Piso
        MaterialItem("235", "PISO PRIME NOGUEIRA NATURAL 2,14M²")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orcamento)

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
        metragemInput = findViewById(R.id.metragem_input)
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

        // Botões do Step 3
        findViewById<Button>(R.id.btn_calcular).setOnClickListener { 
            hideKeyboard()
            calcularPorMetragem() 
        }
        findViewById<Button>(R.id.btn_finalizar).setOnClickListener { 
            hideKeyboard()
            finalizarLista() 
        }
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

    private fun setupPlacaSpinner() {
        val placas = listOf(
            "Selecione o tipo de placa...",
            "Drywall ST Branco 1,80 x 1,20",
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
            } else {
                placaSelectionLayout.visibility = View.GONE
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
                
                // Usar o tipo de placa selecionado
                addMaterialByCode(selectedPlacaType!!, m2 / 2.88f)
                addMaterialByCode("1521", (m2 * 20) / 1000f)
                addMaterialByCode("1516", m2 / 30f)

                if (drywallSubtype == "Teto") {
                    addMaterialByCode("33", (m2 * 0.5f) / 12f)
                    addMaterialByCode("366", m2 / 0.6f)
                    addMaterialByCode("667", m2 * 0.05f)
                    addMaterialByCode("32", m2 * 0.02f)
                    addMaterialByCode("668", m2 * 0.02f)
                } else {
                    addMaterialByCode("388", m2 / 3f)
                    addMaterialByCode("387", m2 / 0.6f)
                    addMaterialByCode("192", (m2 * 2) / 100f)
                    addMaterialByCode("173", (m2 * 0.5f) / 100f)
                }
            }
            "PVC" -> {
                addMaterialByCode("163", m2 / 1.2f)
                addMaterialByCode("574", m2 / 6f)
                addMaterialByCode("146", m2 / 6f)
                addMaterialByCode("173", (m2 * 0.5f) / 100f)
            }
            "Isopor" -> {
                addMaterialByCode("68", m2 / 1.2f)
                addMaterialByCode("19", (m2 * 5) / 100f)
                addMaterialByCode("267", m2 * 2f)
                addMaterialByCode("164", m2 * 0.5f)
                addMaterialByCode("216", m2 / 4f)
                addMaterialByCode("1365", m2 / 4f)
                addMaterialByCode("1366", m2 / 4f)
                addMaterialByCode("1175", m2 / 15f)
            }
            "Painel" -> {
                addMaterialByCode("79", m2 / 2.88f)
                addMaterialByCode("89", m2 / 3f)
                addMaterialByCode("81", m2 / 3f)
                addMaterialByCode("87", m2 / 1.185f)
                addMaterialByCode("107", m2 / 0.84f)
                addMaterialByCode("110", m2 / 2.14f)
                addMaterialByCode("95", m2 / 1.18f)
                addMaterialByCode("98", m2 / 1.18f)
                addMaterialByCode("86", m2 / 1.185f)
            }
            "Piso" -> {
                addMaterialByCode("235", m2 / 2.14f)
            }
        }

        step3Metragem.visibility = View.GONE
        mostrarResultado()
    }

    private fun addMaterialByCode(code: String, quantidade: Float) {
        val produto = produtos.find { it.codigo == code }
        materiaisSelecionados.add(
            MaterialItem(
                codigo = code,
                nome = produto?.nome ?: "NÃO ENCONTRADO",
                quantidade = quantidade.toInt().coerceAtLeast(1)
            )
        )
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
        
        step3Lista.visibility = View.GONE
        mostrarResultado()
    }

    private fun mostrarResultado() {
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

    data class MaterialItem(
        val codigo: String,
        val nome: String,
        val quantidade: Int = 0
    )

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
