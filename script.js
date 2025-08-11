let selectedMaterial = null;
let drywallSubtype = null;
let materiaisSelecionados = [];
let produtos = [];
let produtosCarregados = false;

// Fallback com produtos reais do JSON + alguns códigos essenciais
const produtosBackup = [
  { codigo: "583", nome: "METALON 20x20", valor: 15.50 },
  { codigo: "1163", nome: "FITA FIBAFUSE", valor: 8.90 },
  { codigo: "574", nome: "RODA FORRO MOLDURA", valor: 12.30 },
  { codigo: "146", nome: "ROAD FORRO U", valor: 18.75 },
  { codigo: "26", nome: "PREGO 15X15", valor: 2.50 },
  { codigo: "89", nome: "GUIA DE 3M BRANCA", valor: 22.40 },
  { codigo: "93", nome: "GUIA DE 3M CINZA", valor: 22.40 },
  { codigo: "52", nome: "DOBRADIÇA BRANCA", valor: 8.60 },
  { codigo: "164", nome: "PINO CLIP", valor: 0.35 },
  { codigo: "570", nome: "FORRO PVC 5M 7MM", valor: 28.90 },
  { codigo: "6", nome: "CANTONEIRA PERFURADA", valor: 4.80 },
  { codigo: "14", nome: "MONTANTE DE 90 0,50", valor: 16.20 },
  { codigo: "20", nome: "PARAFUSO 25 BROCA", valor: 0.15 },
  { codigo: "142", nome: "PARAFUSO 13 AGULHA", valor: 0.12 },
  { codigo: "387", nome: "MONTANTE 48 CD", valor: 14.80 },
  { codigo: "166", nome: "FINCAPINO AMARELO", valor: 0.25 },
  { codigo: "38", nome: "LA DE ROCHA", valor: 12.50 },
  { codigo: "256", nome: "LA DE PET", valor: 18.90 },
  { codigo: "317", nome: "ESPUMA EXPANSIVA", valor: 16.80 },
  // Códigos essenciais para cálculos
  { codigo: "280", nome: "PLACA DRYWALL COMUM", valor: 28.50 },
  { codigo: "1521", nome: "PARAFUSO PONTA AGULHA GN25 CX MIL", valor: 45.00 },
  { codigo: "1516", nome: "FITA TELADA BRANCA 90MT", valor: 12.80 },
  { codigo: "33", nome: "ARAME DE 10", valor: 8.20 },
  { codigo: "366", nome: "PERFIL F530 BARRA", valor: 18.60 },
  { codigo: "667", nome: "CANTONEIRA 25X30", valor: 4.80 },
  { codigo: "32", nome: "REGULADOR F530", valor: 3.50 },
  { codigo: "668", nome: "TABICA BARRA", valor: 12.40 },
  { codigo: "388", nome: "GUIA 48", valor: 14.20 },
  { codigo: "192", nome: "BUCHA 6", valor: 0.08 },
  { codigo: "173", nome: "PARAFUSO FRANGEADO 45", valor: 0.18 },
  { codigo: "68", nome: "FORRO ISOPOR 20MM", valor: 8.90 },
  { codigo: "216", nome: "TRAVESSA PERFIL CLICADO BRANCO", valor: 15.30 },
  { codigo: "267", nome: "PRESILHA BIGODINHO FORRO ISOPOR", valor: 0.45 },
  { codigo: "1175", nome: "COLA SELANTE PU", valor: 24.50 },
  { codigo: "431", nome: "MASSA KOLIMAR 28KG", valor: 85.00 },
  { codigo: "698", nome: "MASSA KOLIMAR 5KG", valor: 18.50 },
  { codigo: "1518", nome: "FITA CIMENTICIA", valor: 9.80 }
];

// Carrega JSON ou usa backup
fetch("produtos.json")
  .then(r => {
    if (!r.ok) throw new Error("Erro ao carregar JSON");
    return r.json();
  })
  .then(data => {
    // Combina dados do JSON com produtos essenciais do backup
    let produtosJSON = data.map(p => ({
      codigo: String(p.codigo),
      nome: String(p.nome),
      valor: Number(p.valor) || 0
    }));
    
    // Adiciona produtos essenciais que não estão no JSON
    produtosBackup.forEach(backup => {
      let existe = produtosJSON.find(p => p.codigo === backup.codigo);
      if (!existe) {
        produtosJSON.push(backup);
      }
    });
    
    produtos = produtosJSON;
    produtosCarregados = true;
    console.log(`${produtos.length} produtos carregados`);
  })
  .catch((error) => {
    console.warn("Erro ao carregar JSON, usando backup:", error);
    produtos = produtosBackup;
    produtosCarregados = true;
  });

// ---------- Navegação ----------
function selectMaterialType(type) {
  selectedMaterial = type;
  drywallSubtype = null;
  if (type === "Drywall") {
    document.getElementById('step1').style.display = 'none';
    document.getElementById('step1-drywall').style.display = 'block';
  } else {
    document.getElementById('step1').style.display = 'none';
    document.getElementById('step2').style.display = 'block';
  }
}

function selectDrywallSubtype(sub) {
  drywallSubtype = sub;
  document.getElementById('step1-drywall').style.display = 'none';
  document.getElementById('step2').style.display = 'block';
}

function backToMaterialChoice() {
  document.getElementById('step1-drywall').style.display = 'none';
  document.getElementById('step1').style.display = 'block';
  selectedMaterial = null;
  drywallSubtype = null;
}

function selectCalcMethod(method) {
  if (!produtosCarregados) {
    alert("Aguarde, carregando lista de produtos...");
    return;
  }
  document.getElementById('step2').style.display = 'none';
  if (method === 'metragem') {
    document.getElementById('step3-metragem').style.display = 'block';
  } else {
    carregarListaMateriais();
    document.getElementById('step3-lista').style.display = 'block';
  }
}

// ---------- Utilidades ----------
function findProductByCode(code) {
  return produtos.find(p => String(p.codigo) === String(code)) || null;
}

function addMaterialByCode(code, quantidade, nomeAlternativo = null) {
  let prod = findProductByCode(code);
  if (!prod && nomeAlternativo) {
    // Se não encontrar o produto, cria um temporário com nome alternativo
    prod = { codigo: code, nome: nomeAlternativo, valor: 0 };
  }
  
  // Ajusta quantidade para produtos que vêm em embalagens específicas
  let qtdFinal = quantidade;
  let unidade = "un";
  
  if (prod && prod.nome.includes("CX") && (prod.nome.includes("MIL") || prod.nome.includes("1000"))) {
    // Para caixas com mil unidades, converte a quantidade necessária para caixas
    qtdFinal = Math.ceil(quantidade / 1000);
    unidade = "cx";
  } else if (prod && prod.nome.includes("SACO") && prod.nome.includes("KG")) {
    // Para produtos em sacos/kg, mantém como está mas marca unidade
    qtdFinal = Math.ceil(quantidade);
    unidade = "saco";
  } else {
    qtdFinal = Math.ceil(quantidade);
  }
  
  materiaisSelecionados.push({
    codigo: code,
    nome: prod ? prod.nome : "PRODUTO NÃO ENCONTRADO",
    quantidade: qtdFinal,
    quantidadeOriginal: quantidade,
    unidade: unidade,
    valor: prod ? prod.valor : 0,
    encontrado: !!prod
  });
}

// ---------- Cálculo ----------
function calcularPorMetragem() {
  let m2 = parseFloat(document.getElementById('metragem').value);
  if (isNaN(m2) || m2 <= 0) {
    alert("Digite uma metragem válida!");
    return;
  }
  materiaisSelecionados = [];

  if (selectedMaterial === "Drywall") {
    if (!drywallSubtype) {
      alert("Escolha Teto ou Parede");
      return;
    }
    
    // Materiais comuns para Drywall
    addMaterialByCode("280", m2 / 2.88, "PLACA DRYWALL COMUM"); // Placa drywall (2,88m²)
    addMaterialByCode("1521", m2 * 20, "PARAFUSO PONTA AGULHA GN25"); // Parafuso GN25
    addMaterialByCode("1516", m2 / 30, "FITA TELADA BRANCA 90MT"); // Fita telada

    if (drywallSubtype === "Teto") {
      addMaterialByCode("33", m2 * 0.5, "ARAME DE 10"); // Arame
      addMaterialByCode("366", m2 / 0.6, "PERFIL F530 BARRA"); // Perfil F530
      addMaterialByCode("6", m2 * 0.05, "CANTONEIRA PERFURADA"); // Cantoneira (usando código real)
      addMaterialByCode("32", m2 * 0.02, "REGULADOR F530"); // Regulador
      addMaterialByCode("668", m2 * 0.02, "TABICA BARRA"); // Tabica
    } else {
      addMaterialByCode("89", m2 / 3, "GUIA DE 3M BRANCA");   // Guia (usando código real)
      addMaterialByCode("387", m2 / 0.6, "MONTANTE 48 CD"); // Montante
      addMaterialByCode("192", m2 * 2, "BUCHA 6");   // Bucha
      addMaterialByCode("173", m2 * 0.5, "PARAFUSO FRANGEADO 45"); // Parafuso Frangeado
    }
  }

  else if (selectedMaterial === "PVC") {
    addMaterialByCode("570", m2 / 0.20, "FORRO PVC 5M 7MM");  // Forro PVC (usando código real)
    addMaterialByCode("387", m2 / 0.6, "MONTANTE 48 CD");  // Montante
    addMaterialByCode("173", m2 * 0.5, "PARAFUSO FRANGEADO 45");  // Parafuso Frangeado
    addMaterialByCode("89", m2 / 3, "GUIA DE 3M BRANCA"); // Guia
  }

  else if (selectedMaterial === "Gesso") {
    addMaterialByCode("431", m2 / 30, "MASSA KOLIMAR 28KG");   // Massa kolimar 28kg (30m²/emb)
    addMaterialByCode("1518", m2 / 30, "FITA CIMENTICIA");  // Fita Cimenticia
    addMaterialByCode("192", m2 * 2, "BUCHA 6");    // Bucha 6
    addMaterialByCode("1521", m2 * 15, "PARAFUSO PONTA AGULHA GN25");  // Parafuso GN25
  }

  else if (selectedMaterial === "Isopor") {
    addMaterialByCode("68", m2 / 1.2, "FORRO ISOPOR 20MM");   // Forro isopor 20mm
    addMaterialByCode("216", m2 / 4, "TRAVESSA PERFIL CLICADO BRANCO");    // Travessa perfil clicado
    addMaterialByCode("267", m2 * 2, "PRESILHA BIGODINHO FORRO ISOPOR");    // Presilha bigodinho
    addMaterialByCode("1175", m2 / 15, "COLA SELANTE PU");  // Cola selante PU
  }

  document.getElementById('step3-metragem').style.display = 'none';
  mostrarResultado();
}

// ---------- Lista manual ----------
function carregarListaMateriais() {
  let lista = document.getElementById('materials-list');
  lista.innerHTML = '';
  
  // Ordena produtos por código
  let produtosOrdenados = [...produtos].sort((a, b) => {
    return parseInt(a.codigo) - parseInt(b.codigo);
  });
  
  produtosOrdenados.forEach(mat => {
    let div = document.createElement('div');
    div.style.cssText = 'padding: 8px; border-bottom: 1px solid #eee; display: flex; align-items: center; gap: 10px;';
    div.innerHTML = `
      <input type="number" id="qtd-${mat.codigo}" min="0" step="0.1" 
             style="width:80px; padding: 5px;" placeholder="Qtd">
      <span style="font-weight: bold; color: #ff6600;">[${mat.codigo}]</span> 
      <span style="flex: 1;">${mat.nome}</span> 
      <span style="color: #666; font-weight: bold;">R$ ${mat.valor.toFixed(2)}</span>
    `;
    lista.appendChild(div);
  });
}

function finalizarLista() {
  materiaisSelecionados = [];
  produtos.forEach(mat => {
    let qtdInput = document.getElementById(`qtd-${mat.codigo}`);
    let qtd = parseFloat(qtdInput.value) || 0;
    if (qtd > 0) {
      // Ajusta para produtos que vêm em embalagens específicas
      let qtdFinal = qtd;
      let unidade = "un";
      
      if (mat.nome.includes("CX") && (mat.nome.includes("MIL") || mat.nome.includes("1000"))) {
        unidade = "cx";
      } else if (mat.nome.includes("SACO") && mat.nome.includes("KG")) {
        unidade = "saco";
      }
      
      materiaisSelecionados.push({
        codigo: mat.codigo,
        nome: mat.nome,
        quantidade: qtdFinal,
        unidade: unidade,
        valor: mat.valor,
        encontrado: true
      });
    }
  });
  
  if (materiaisSelecionados.length === 0) {
    alert("Selecione pelo menos um material!");
    return;
  }
  
  document.getElementById('step3-lista').style.display = 'none';
  mostrarResultado();
}

// ---------- Resultado ----------
function mostrarResultado() {
  let html = "<h3>Lista de Materiais:</h3><ul style='text-align: left;'>";
  let totalValor = 0;
  let temProdutoNaoEncontrado = false;
  
  materiaisSelecionados.forEach(mat => {
    let subtotal = mat.quantidade * mat.valor;
    totalValor += subtotal;
    
    let statusCor = mat.encontrado ? '' : 'color: red;';
    let aviso = mat.encontrado ? '' : ' ⚠️';
    
    if (!mat.encontrado) {
      temProdutoNaoEncontrado = true;
    }
    
    // Mostra informação adicional para produtos em embalagens especiais
    let infoAdicional = '';
    if (mat.unidade === 'cx' && mat.quantidadeOriginal) {
      infoAdicional = `<br><small style="color: #666;">💡 Necessário: ${Math.ceil(mat.quantidadeOriginal)} unidades | Comprando: ${mat.quantidade} caixa(s) = ${mat.quantidade * 1000} unidades</small>`;
    }
    
    html += `<li style="${statusCor}">
      <strong>[${mat.codigo}]</strong> ${mat.quantidade}x ${mat.nome}${aviso}
      <br><small>Valor unit: R$ ${mat.valor.toFixed(2)} | Subtotal: R$ ${subtotal.toFixed(2)}</small>
      ${infoAdicional}
    </li><br>`;
  });
  
  html += "</ul>";
  
  if (totalValor > 0) {
    html += `<div style="background: #f0f0f0; padding: 15px; border-radius: 8px; margin: 20px 0;">
      <h4 style="color: #ff6600; margin: 0;">TOTAL ESTIMADO: R$ ${totalValor.toFixed(2)}</h4>
    </div>`;
  }
  
  if (temProdutoNaoEncontrado) {
    html += `<div style="background: #ffe6e6; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid red;">
      <strong>⚠️ ATENÇÃO:</strong> Alguns produtos marcados com ⚠️ não foram encontrados na base de dados atual.
      Consulte nossa loja para verificar disponibilidade e preços atualizados.
    </div>`;
  }
  
  html += `<div style="background: #fff3e6; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ff6600;">
    <strong>📋 IMPORTANTE:</strong><br>
    • Estes são materiais e quantidades sugeridos baseados em cálculos padrão<br>
    • Consulte sempre um instalador qualificado para confirmar as quantidades exatas<br>
    • Quantidades podem variar conforme especificidades da obra<br>
    • Preços sujeitos a alteração sem aviso prévio
  </div>`;
  
  html += `<div style="margin-top: 20px;">
    <button onclick="reiniciarOrcamento()" style="background: #666; padding: 12px 20px;">
      🔄 Fazer Novo Orçamento
    </button>
    <button onclick="window.print()" style="background: #28a745; padding: 12px 20px; margin-left: 10px;">
      🖨️ Imprimir Lista
    </button>
  </div>`;
  
  document.getElementById('result-content').innerHTML = html;
  document.getElementById('resultado').style.display = 'block';
}

// ---------- Reiniciar ----------
function reiniciarOrcamento() {
  // Reset todas as variáveis
  selectedMaterial = null;
  drywallSubtype = null;
  materiaisSelecionados = [];
  
  // Reset entrada de metragem
  document.getElementById('metragem').value = '';
  
  // Esconde todas as seções exceto a primeira
  document.getElementById('step1').style.display = 'block';
  document.getElementById('step1-drywall').style.display = 'none';
  document.getElementById('step2').style.display = 'none';
  document.getElementById('step3-metragem').style.display = 'none';
  document.getElementById('step3-lista').style.display = 'none';
  document.getElementById('resultado').style.display = 'none';
}