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
import com.example.brasrio.ProdutoLoader
import com.example.brasrio.CalculoUtils
import com.example.brasrio.ParedeCalculator
import com.example.brasrio.CimenticiaCalculator
import com.example.brasrio.PVCCalculator
import com.example.brasrio.MaterialItem
import com.example.brasrio.PisoOption
import android.util.Log

class OrcamentoActivity : AppCompatActivity() {

    private lateinit var step1: LinearLayout
    private lateinit var step1Drywall: LinearLayout
    private lateinit var step1Cimenticia: LinearLayout
    private lateinit var step1Pvc: LinearLayout
    private lateinit var step1Piso: LinearLayout
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
    private var cimenticiaSubtype: String? = null
    private var pvcSubtype: String? = null
    private var pisoSubtype: String? = null
    private var selectedPlacaType: String? = null
    private var selectedCorPiso: String? = null
    private var quantidadeJanelas: Int = 0
    private var quantidadePortas: Int = 0
    private val materiaisSelecionados = mutableListOf<MaterialItem>()

    // Lista de produtos - será carregada do JSON
    private lateinit var produtos: List<MaterialItem>
    
    // Funções auxiliares para cálculos
    private fun calculateParafusos(area: Float, sistema: String): Int {
        return CalculoUtils.calculateParafusos(area, sistema)
    }
    
    private fun calculateFita(area: Float, sistema: String): Int {
        return CalculoUtils.calculateFita(area, sistema)
    }
    
    private fun calculateMassa(area: Float, sistema: String): Float {
        return CalculoUtils.calculateMassa(area, sistema)
    }
    
    private fun getAreaPisoVinilico(codigo: String): Float {
        return CalculoUtils.getAreaPisoVinilico(codigo)
    }
    
    private fun getAreaPisoLaminado(codigo: String): Float {
        return CalculoUtils.getAreaPisoLaminado(codigo)
    }
    
    private fun escolherMelhorPisoVinilico(m2: Float): PisoOption {
        return CalculoUtils.escolherMelhorPisoVinilico(m2)
    }
    
    private fun escolherMelhorPisoLaminado(m2: Float): PisoOption {
        return CalculoUtils.escolherMelhorPisoLaminado(m2)
    }

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
        step1Cimenticia = findViewById(R.id.step1_cimenticia)
        step1Pvc = findViewById(R.id.step1_pvc)
        step1Piso = findViewById(R.id.step1_piso)
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
        findViewById<Button>(R.id.btn_cimenticia).setOnClickListener { selectMaterialType("Cimenticia") }
        findViewById<Button>(R.id.btn_pvc).setOnClickListener { selectMaterialType("PVC") }
        findViewById<Button>(R.id.btn_isopor).setOnClickListener { selectMaterialType("Isopor") }
        findViewById<Button>(R.id.btn_painel).setOnClickListener { selectMaterialType("Painel") }
        findViewById<Button>(R.id.btn_piso).setOnClickListener { selectMaterialType("Piso") }

        // Botões do Step 1 Drywall
        findViewById<Button>(R.id.btn_teto).setOnClickListener { selectDrywallSubtype("Teto") }
        findViewById<Button>(R.id.btn_parede).setOnClickListener { selectDrywallSubtype("Parede") }
        findViewById<Button>(R.id.btn_back_material).setOnClickListener { backToMaterialChoice() }

        // Botões do Step 1 Cimenticia
        findViewById<Button>(R.id.btn_cimenticia_teto).setOnClickListener { selectCimenticiaSubtype("Teto") }
        findViewById<Button>(R.id.btn_cimenticia_parede).setOnClickListener { selectCimenticiaSubtype("Parede") }
        findViewById<Button>(R.id.btn_back_cimenticia).setOnClickListener { backToMaterialChoice() }

        // Botões do Step 1 PVC
        findViewById<Button>(R.id.btn_pvc_placa).setOnClickListener { selectPvcSubtype("Placa") }
        findViewById<Button>(R.id.btn_pvc_regua).setOnClickListener { selectPvcSubtype("Regua") }
        findViewById<Button>(R.id.btn_back_pvc).setOnClickListener { backToMaterialChoice() }

        // Botões do Step 1 Piso
        findViewById<Button>(R.id.btn_piso_vinilico).setOnClickListener { selectPisoSubtype("Vinílico") }
        findViewById<Button>(R.id.btn_piso_laminado).setOnClickListener { selectPisoSubtype("Laminado") }
        findViewById<Button>(R.id.btn_back_piso).setOnClickListener { backToMaterialChoice() }

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
        step1Cimenticia.visibility = View.GONE
        step1Pvc.visibility = View.GONE
        step1Piso.visibility = View.GONE
        step2.visibility = View.GONE
        step3Metragem.visibility = View.GONE
        step3Lista.visibility = View.GONE
        resultado.visibility = View.GONE
        materialInfo.text = ""
    }

    private fun selectMaterialType(type: String) {
        selectedMaterial = type
        drywallSubtype = null
        cimenticiaSubtype = null
        pvcSubtype = null
        pisoSubtype = null
        
        when (type) {
            "Drywall" -> {
                step1.visibility = View.GONE
                step1Drywall.visibility = View.VISIBLE
            }
            "Cimenticia" -> {
                step1.visibility = View.GONE
                step1Cimenticia.visibility = View.VISIBLE
            }
            "PVC" -> {
                step1.visibility = View.GONE
                step1Pvc.visibility = View.VISIBLE
            }
            "Piso" -> {
                step1.visibility = View.GONE
                step1Piso.visibility = View.VISIBLE
            }
            else -> {
                step1.visibility = View.GONE
                step2.visibility = View.VISIBLE
            }
        }
    }

    private fun selectDrywallSubtype(sub: String) {
        drywallSubtype = sub
        step1Drywall.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun selectCimenticiaSubtype(sub: String) {
        cimenticiaSubtype = sub
        step1Cimenticia.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun selectPvcSubtype(sub: String) {
        pvcSubtype = sub
        step1Pvc.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun selectPisoSubtype(sub: String) {
        pisoSubtype = sub
        step1Piso.visibility = View.GONE
        step2.visibility = View.VISIBLE
    }

    private fun backToMaterialChoice() {
        step1Drywall.visibility = View.GONE
        step1Cimenticia.visibility = View.GONE
        step1Pvc.visibility = View.GONE
        step1Piso.visibility = View.GONE
        step1.visibility = View.VISIBLE
        selectedMaterial = null
        drywallSubtype = null
        cimenticiaSubtype = null
        pvcSubtype = null
        pisoSubtype = null
        selectedPlacaType = null
        selectedCorPiso = null
    }

    private fun backToStep2() {
        step2.visibility = View.GONE
        when (selectedMaterial) {
            "Drywall" -> step1Drywall.visibility = View.VISIBLE
            "Cimenticia" -> step1Cimenticia.visibility = View.VISIBLE
            "PVC" -> step1Pvc.visibility = View.VISIBLE
            "Piso" -> step1Piso.visibility = View.VISIBLE
            else -> step1.visibility = View.VISIBLE
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
        val placas = mutableListOf("Selecione o tipo de placa...")
        val codigosPlacas = mutableListOf("")
        
        when {
            selectedMaterial == "Drywall" -> {
                placas.addAll(listOf(
                    "Drywall ST Branco 1,80 x 1,20",
                    "Drywall RU (Resistente à Umidade) 1,80 x 1,20",
                    "Drywall RF (Resistente à fogo) 1,80 x 1,20"
                ))
                codigosPlacas.addAll(listOf("280", "177", "193"))
            }
            selectedMaterial == "Cimenticia" && cimenticiaSubtype == "Parede" -> {
                placas.addAll(listOf(
                    "PLACA CIMENTICIA 2.40 X 1.20 8MM DECORLIT",
                    "PLACA CIMENTICIA 2.40 X 1.20 10MM DECORLIT",
                    "PLACA CIMENTICIA 2.40 X 1.20 6MM DECORLIT",
                    "PLACA CIMENTICIA 2.40 X 1.20 12MM DECORLIT"
                ))
                codigosPlacas.addAll(listOf("181", "182", "263", "1172"))
            }
            selectedMaterial == "PVC" && pvcSubtype == "Regua" -> {
                placas.addAll(listOf(
                    "FORRO PVC 1,00 METROS ( 7MM )",
                    "FORRO PVC 2,00 METROS ( 7MM )",
                    "FORRO PVC 6,50 METROS ( 7MM )",
                    "FORRO PVC 3,00 METROS ( 7MM )",
                    "FORRO PVC 6,00 METROS ( 7MM )",
                    "FORRO PVC 3,50 METROS ( 7MM )",
                    "FORRO PVC 5.50 METROS ( 7MM )",
                    "FORRO PVC 4,00 METROS ( 7MM )",
                    "FORRO PVC 5.00 METROS ( 7MM )",
                    "FORRO PVC 4,50 METROS ( 7MM )",
                    "FORRO PVC 7,00 METROS ( 7MM )",
                    "FORRO PVC 8,00 METROS ( 7MM )",
                    "FORRO PVC 8,50 METROS ( 7MM )"
                ))
                codigosPlacas.addAll(listOf("1138", "1139", "740", "566", "571", "567", "741", "568", "570", "569", "572", "573", "1251"))
            }
            selectedMaterial == "Piso" -> {
                if (pisoSubtype == "Vinílico") {
                    placas.addAll(listOf(
                        "PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA - 18 REGUAS - 2MM - 3,90 M2",
                        "PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA - 18 REGUAS - 2MM - 3,90 M2",
                        "PISO VINILICO RUFFINO BRAVO COR ANGELIM - 3MM - 2,6 M2",
                        "PISO VINILICO RUFFINO NOBILE COLADO BAOBA 2MM - 3,90M2",
                        "PISO VINILICO RUFFINO NOBILE COLADO DAMASCO 2MM - 3,90M2"
                    ))
                    codigosPlacas.addAll(listOf("1574", "1570", "1599", "1575", "1576"))
                } else if (pisoSubtype == "Laminado") {
                    placas.addAll(listOf(
                        "PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM - CAIXA C/ 2,41M2",
                        "PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO CX C/ 2,51M2",
                        "PISO LAMINADO QUICK STEP PREMIERE MOCHA - 2,84M2"
                    ))
                    codigosPlacas.addAll(listOf("1102", "1236", "1401"))
                }
            }
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, placas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaca.adapter = adapter
        
        spinnerPlaca.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0 && position <= codigosPlacas.size) {
                    selectedPlacaType = codigosPlacas[position]
                    if (selectedMaterial == "Piso") {
                        selectedCorPiso = codigosPlacas[position]
                    }
                } else {
                    selectedPlacaType = null
                    selectedCorPiso = null
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedPlacaType = null
                selectedCorPiso = null
            }
        }
    }

    private fun selectCalcMethod(method: String) {
        step2.visibility = View.GONE
        if (method == "metragem") {
            step3Metragem.visibility = View.VISIBLE
            
            // Configurar spinner de placa baseado no material
            setupPlacaSpinner()
            
            // Mostrar seleção de placa para materiais que precisam
            val precisaPlaca = selectedMaterial == "Drywall" || 
                              (selectedMaterial == "Cimenticia" && cimenticiaSubtype == "Parede") ||
                              (selectedMaterial == "PVC" && pvcSubtype == "Regua") ||
                              selectedMaterial == "Piso"
            
            placaSelectionLayout.visibility = if (precisaPlaca) View.VISIBLE else View.GONE
            
            // Mostrar campo de pé direito para Drywall Parede e Cimenticia Parede
            val precisaPeDireito = (selectedMaterial == "Drywall" && drywallSubtype == "Parede") ||
                                   (selectedMaterial == "Cimenticia" && cimenticiaSubtype == "Parede")
            
            peDireitoLayout.visibility = if (precisaPeDireito) View.VISIBLE else View.GONE
            
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
        


        // Obter pé direito se for Drywall Parede ou Cimenticia Parede
        var peDireito = 2.7f // Valor padrão
        if ((selectedMaterial == "Drywall" && drywallSubtype == "Parede") ||
            (selectedMaterial == "Cimenticia" && cimenticiaSubtype == "Parede")) {
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
                    
                    // Placa escolhida - JavaScript: m2 / 2.16 (não 2.88)
                    addMaterialByCode(selectedPlacaType!!, ceil(m2 / 2.16f).toInt())
                    
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
                    
                    // JavaScript: m2 / 4 (reduzido novamente - era m2/3)
                    addMaterialByCode("366", ceil(m2 / 4f).toInt()) // Perfil F530
                    
                    // JavaScript: m2 * 0.05 - sem ceil
                    addMaterialByCode("667", (m2 * 0.05f).toInt()) // Cantoneira
                    // JavaScript: m2 * 0.24 (aumentado 100% novamente - era 0.12)
                    addMaterialByCode("32", (m2 * 0.24f).toInt()) // Regulador
                }
            }
            "Cimenticia" -> {
                if (cimenticiaSubtype == null) {
                    Toast.makeText(this, "Escolha Teto ou Parede", Toast.LENGTH_SHORT).show()
                    return
                }
                
                if (cimenticiaSubtype == "Parede" && selectedPlacaType == null) {
                    Toast.makeText(this, "Selecione o tipo de placa!", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    val calculator = CimenticiaCalculator(
                        m2, 
                        peDireito, 
                        selectedPlacaType, 
                        cimenticiaSubtype!!.lowercase(),
                        produtos
                    )
                    materiaisSelecionados.addAll(calculator.calcularMateriais())
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro no cálculo: ${e.message}", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "PVC" -> {
                if (pvcSubtype == "Regua" && selectedPlacaType == null) {
                    Toast.makeText(this, "Selecione o tipo de régua!", Toast.LENGTH_SHORT).show()
                    return
                }
                
                if (pvcSubtype == "Regua") {
                    try {
                        val calculator = PVCCalculator(m2, selectedPlacaType!!, produtos)
                        materiaisSelecionados.addAll(calculator.calcularMateriais())
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro no cálculo: ${e.message}", Toast.LENGTH_SHORT).show()
                        return
                    }
                } else {
                    // PVC Placa - lógica original
                    addMaterialByCode("163", ceil(m2 * 1.28f).toInt()) // Forro PVC
                    addMaterialByCode("574", ceil(m2 / 6f).toInt()) // Roda forro
                    addMaterialByCode("146", ceil(m2 / 6f).toInt()) // Roda forro U
                    addMaterialByCode("173", ceil((m2 * 0.5f) / 100f).toInt()) // Parafuso Frangeado
                }
            }
            "Isopor" -> {
                // Forro isopor: cada pacote cobre 19,2m² (conforme descrição do produto)
                addMaterialByCode("68", ceil(m2 / 19.2f).toInt()) // Forro isopor
                addMaterialByCode("19", ceil((m2 * 5f) / 100f).toInt()) // Parafuso ponta agulha
                // Presilha bigode: vem com 50 unidades por pacote
                addMaterialByCode("267", ceil((m2 * 2f) / 50f).toInt()) // Presilha bigodinho
                // Pino clip: vem com 100 unidades por pacote (cento)
                addMaterialByCode("164", ceil((m2 * 0.5f) / 100f).toInt()) // Pino Cadeirinha
                addMaterialByCode("1364", ceil(m2 / 4f).toInt()) // Travessa perfil clicado
                addMaterialByCode("1365", ceil(m2 / 4f).toInt()) // Travessa clicado 1,25
                addMaterialByCode("1366", ceil(m2 / 4f).toInt()) // Travessa clicado 0,625
                addMaterialByCode("1175", ceil(m2 / 15f).toInt()) // Cola Selante PU
            }
            "Painel" -> {
                // Sistema divisória naval
                val sistema = "divisoria"
                
                // Calcula quantidade de painéis baseado nos metros quadrados
                val quantidadePaineis = ceil(m2 / 2.53f).toInt()
                
                // Cálculo baseado na quantidade de painéis
                addMaterialByCode("79", quantidadePaineis) // Painel Eucatex (Divisória Naval)
                addMaterialByCode("89", ceil(quantidadePaineis * 1.22f).toInt()) // Guia Baixa (U) Branca 3.00 mts
                addMaterialByCode("81", quantidadePaineis) // NTR Travessa 3M
                addMaterialByCode("87", quantidadePaineis) // NTR Travessa 1185 M
                
                // Requadros e Batentes só são incluídos se houver portas
                if (quantidadePortas > 0) {
                    addMaterialByCode("102", ceil(quantidadePortas * 2f).toInt()) // Requadro Horizontal 0,81 M
                    addMaterialByCode("101", ceil(quantidadePortas * 2f).toInt()) // Requadro Vertical 2,11 M
                    addMaterialByCode("107", quantidadePortas) // Batente Horizontal 0,84 M
                    addMaterialByCode("110", ceil(quantidadePortas * 2f).toInt()) // Batente Vertical 2,14 M
                }
                
                // Materiais para janelas (se houver)
                if (quantidadeJanelas > 0) {
                    addMaterialByCode("95", ceil(quantidadeJanelas * 4f).toInt()) // Leito Branco 1,18 mts
                    addMaterialByCode("98", ceil(quantidadeJanelas * 4f).toInt()) // Baguete Branco 1,18 mts
                }
            }
            "Piso" -> {
                if (pisoSubtype == null) {
                    Toast.makeText(this, "Escolha Vinílico ou Laminado", Toast.LENGTH_SHORT).show()
                    return
                }
                
                if (selectedCorPiso == null) {
                    Toast.makeText(this, "Selecione a cor do material!", Toast.LENGTH_SHORT).show()
                    return
                }
                
                if (pisoSubtype == "Vinílico") {
                    // Se foi especificado um código de cor específico, usa ele
                    if (selectedCorPiso in listOf("1574", "1570", "1599", "1575", "1576")) {
                        val areaPiso = getAreaPisoVinilico(selectedCorPiso!!)
                        val quantidade = ceil(m2 / areaPiso).toInt()
                        addMaterialByCode(selectedCorPiso!!, quantidade)
                        addMaterialByCode("947", quantidade) // MASSA NIVELADORA PISO SC/ 4KG MAPEI
                    } else {
                        // Senão, usa a lógica de escolha automática
                        val melhor = escolherMelhorPisoVinilico(m2)
                        addMaterialByCode(melhor.codigo, melhor.quantidade)
                        addMaterialByCode("947", melhor.quantidade) // MASSA NIVELADORA PISO SC/ 4KG MAPEI
                    }
                } else if (pisoSubtype == "Laminado") {
                    // Se foi especificado um código de cor específico, usa ele
                    if (selectedCorPiso in listOf("1102", "1236", "1401")) {
                        val areaPiso = getAreaPisoLaminado(selectedCorPiso!!)
                        val quantidade = ceil(m2 / areaPiso).toInt()
                        addMaterialByCode(selectedCorPiso!!, quantidade)
                        addMaterialByCode("447", ceil(m2 / 1.2f).toInt()) // MANTA P/ PISO LAMINADO 1,20ML
                    } else {
                        // Senão, usa a lógica de escolha automática
                        val melhor = escolherMelhorPisoLaminado(m2)
                        addMaterialByCode(melhor.codigo, melhor.quantidade)
                        addMaterialByCode("447", ceil(m2 / 1.2f).toInt()) // MANTA P/ PISO LAMINADO 1,20ML
                    }
                }
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
        val subtype = when {
            selectedMaterial == "Drywall" -> drywallSubtype
            selectedMaterial == "Cimenticia" -> cimenticiaSubtype
            selectedMaterial == "PVC" -> pvcSubtype
            selectedMaterial == "Piso" -> pisoSubtype
            else -> null
        }
        
        if (selectedMaterial != null && subtype != null) {
            materialInfoText.append("Material: $selectedMaterial - $subtype")
        } else if (selectedMaterial != null) {
            materialInfoText.append("Material: $selectedMaterial")
        }
        
        // Verificar se é cálculo por metragem ou seleção manual
        val isMetragem = metragemInput.text.isNotEmpty() || peDireitoInput.text.isNotEmpty()
        
        if (isMetragem) {
            // Adicionar informações específicas para materiais com pé direito
            if ((selectedMaterial == "Drywall" && drywallSubtype == "Parede") ||
                (selectedMaterial == "Cimenticia" && cimenticiaSubtype == "Parede")) {
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
        resultText.append(getString(R.string.materiais_necessarios))
        resultText.append("\n\n")
        
        materiaisSelecionados.forEach { material ->
            resultText.append("• [${material.codigo}] ${material.quantidade}x ${material.nome}\n")
        }
        
        resultText.append("\n⚠️ ATENÇÃO:\n")
        resultText.append(getString(R.string.atencao_estimativa))
        
        resultContent.text = resultText.toString()
        resultado.visibility = View.VISIBLE
    }

    private fun novoOrcamento() {
        selectedMaterial = null
        drywallSubtype = null
        cimenticiaSubtype = null
        pvcSubtype = null
        pisoSubtype = null
        selectedPlacaType = null
        selectedCorPiso = null
        quantidadeJanelas = 0
        quantidadePortas = 0
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
