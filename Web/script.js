// Lista de produtos
const produtos = [
  // Itens mais comuns
  { codigo: "33", nome: "Arame de 10" },
  { codigo: "99", nome: "Baguete Preto" },
  { codigo: "192", nome: "Bucha 6" },
  { codigo: "667", nome: "Cantoneira 25x30" },
  { codigo: "1363", nome: "Cantoneira Branca Home" },
  { codigo: "67", nome: "Conector perfil" },
  { codigo: "158", nome: "Fechadura preta volga" },
  { codigo: "166", nome: "Fincapino amarelo" },
  { codigo: "1518", nome: "Fita Cimenticia 51MM" },
  { codigo: "1515", nome: "Fita telada azul 90mt Home" },
  { codigo: "1516", nome: "Fita telada branca 90mt Home" },
  { codigo: "388", nome: "Guia 48" },
  { codigo: "96", nome: "Leito preto" },
  { codigo: "431", nome: "Massa kolimar 28kg" },
  { codigo: "387", nome: "Montante 48" },
  { codigo: "81", nome: "NTR Preto" },
  { codigo: "1547", nome: "Painel divisoria cristal (cinza)" },
  { codigo: "1546", nome: "Painel divisória" },
  { codigo: "1175", nome: "COLA SELANTE PU" },
  { codigo: "142", nome: "Parafuso ponta agulha 13 cento" },
  { codigo: "1521", nome: "Parafuso ponta agulha GN25 CX com mil" },
  { codigo: "1364", nome: "Perfil Clicado" },
  { codigo: "366", nome: "Perfil F530 barra" },
  { codigo: "164", nome: "Pino Cadeirinha" },

  // Placas Drywall (códigos corrigidos)
  { codigo: "280", nome: "Drywall ST Branco 1,80 x 1,20" },
  { codigo: "177", nome: "Drywall RU (Resistente à Umidade) 1,80 x 1,20" },
  { codigo: "193", nome: "Drywall RF (Resistente à fogo) 1,80 x 1,20" },

  { codigo: "222", nome: "Porta divisoria cristal" },
  { codigo: "173", nome: "Parafuso Frangeado 45" },
  { codigo: "698", nome: "Massa kolimar 5kg" },
  { codigo: "267", nome: "Presilha bigodinho para forro isopor" },
  { codigo: "32", nome: "Regulador F530" },
  { codigo: "668", nome: "Tabica barra" },
  { codigo: "216", nome: "Travessa perfil clicado branco" },

  // PVC
  { codigo: "574", nome: "RODA FORRO MOLDURA 6 MTS" },
  { codigo: "146", nome: "Roda forro U" },
  { codigo: "163", nome: "Forro pvc Modular 10mm" },

  // Isopor
  { codigo: "68", nome: "Forro Isopor Texturizado 1243X618X20MM (PCT25) 19,2M2" },
  { codigo: "19", nome: "Parafuso ponta agulha 13" },
  { codigo: "1365", nome: "Travessa clicado 1,25" },
  { codigo: "1366", nome: "Travessa clicado 0,625" },

  // Painel Eucatex
  { codigo: "79", nome: "Painel Eucatex (Divisória Naval)" },
  { codigo: "89", nome: "Guia Baixa (U) Branca 3.00 mts" },
  { codigo: "81", nome: "NTR Travessa 3M" },
  { codigo: "87", nome: "NTR Travessa 1185 M" },
  { codigo: "107", nome: "Batente Horizontal 0,84 M" },
  { codigo: "110", nome: "Batente Vertical 2,14 M" },
  { codigo: "95", nome: "Leito Branco 1,18 mts" },
  { codigo: "98", nome: "Baguete Branco 1,18 mts" },
  { codigo: "86", nome: "NTR Travessa 1185 M" },

  // Pisos (apenas estes dois)
  { codigo: "1599", nome: "PISO VINILICO RUFFINO BRAVO COR ANGELIM - 3MM - 2,6 M2" },
  { codigo: "1575", nome: "PISO VINILICO RUFFINO NOBILE COLADO BAOBA 2MM - 3,90M2" }
];

let selectedMaterial = null;
let drywallSubtype = null;
let materiaisSelecionados = [];

// ---------- Navegação ----------
function selectMaterialType(type) {
  selectedMaterial = type;
  drywallSubtype = null;

  document.getElementById('step1').style.display = 'none';

  if (type === "Drywall") {
    document.getElementById('step1-drywall').style.display = 'block';
  } else {
    document.getElementById('step1-drywall').style.display = 'none';
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

// Escolha do método
function selectCalcMethod(method) {
  document.getElementById('step2').style.display = 'none';

  if (method === 'metragem') {
    document.getElementById('step3-metragem').style.display = 'block';

    // Mostra/esconde a lista de placas conforme o material
    const cont = document.getElementById('placaDrywallContainer');
    const select = document.getElementById('placaDrywall');
    if (selectedMaterial === 'Drywall') {
      cont.style.display = 'block';
      select.value = ""; // força escolha
    } else {
      cont.style.display = 'none';
      select.value = "";
    }
  } else {
    carregarListaMateriais();
    document.getElementById('step3-lista').style.display = 'block';
  }
}

// ---------- Utilidades ----------
function findProductByCode(code) {
  return produtos.find(p => String(p.codigo) === String(code)) || null;
}

function addMaterialByCode(code, quantidade) {
  const prod = findProductByCode(code);
  materiaisSelecionados.push({
    codigo: code,
    nome: prod ? prod.nome : "NÃO ENCONTRADO",
    quantidade: Math.ceil(quantidade)
  });
}

// Escolhe o piso com menor sobra (empate: menor quantidade; depois maior área por caixa)
function escolherMelhorPiso(m2) {
  const opcoes = [
    { codigo: "1599", area: 2.6 },
    { codigo: "1575", area: 3.90 }
  ];
  let melhor = null;
  opcoes.forEach(op => {
    const quantidade = Math.ceil(m2 / op.area);
    const coberta = quantidade * op.area;
    const sobra = coberta - m2;
    if (
      !melhor ||
      sobra < melhor.sobra ||
      (sobra === melhor.sobra && quantidade < melhor.quantidade) ||
      (sobra === melhor.sobra && quantidade === melhor.quantidade && op.area > melhor.area)
    ) {
      melhor = { ...op, quantidade, sobra };
    }
  });
  return melhor;
}

// ---------- Cálculo ----------
function calcularPorMetragem() {
  const m2 = parseFloat(document.getElementById('metragem').value);
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

    // Deve escolher a placa no select
    const placaSel = document.getElementById('placaDrywall').value;
    if (!placaSel) {
      alert("Selecione o tipo de placa de Drywall.");
      return;
    }

    // Placa escolhida
    addMaterialByCode(placaSel, m2 / 2.88);
    addMaterialByCode("1521", (m2 * 20) / 1000);
    addMaterialByCode("1516", m2 / 30);

    if (drywallSubtype === "Teto") {
      addMaterialByCode("33", (m2 * 0.5) / 12);
      addMaterialByCode("366", m2 / 0.6);
      addMaterialByCode("667", m2 * 0.05);
      addMaterialByCode("32", m2 * 0.02);
      addMaterialByCode("668", m2 * 0.02);
    } else {
      addMaterialByCode("388", m2 / 3);
      addMaterialByCode("387", m2 / 0.6);
      addMaterialByCode("192", (m2 * 2) / 100);
      addMaterialByCode("173", (m2 * 0.5) / 100);
    }
  }
  else if (selectedMaterial === "PVC") {
    addMaterialByCode("163", m2 / 1.2);
    addMaterialByCode("574", m2 / 6);
    addMaterialByCode("146", m2 / 6);
    addMaterialByCode("173", (m2 * 0.5) / 100);
  }
  else if (selectedMaterial === "Isopor") {
    addMaterialByCode("68", m2 / 1.2);
    addMaterialByCode("19", (m2 * 5) / 100);
    addMaterialByCode("267", m2 * 2);
    addMaterialByCode("164", m2 * 0.5);
    addMaterialByCode("216", m2 / 4);
    addMaterialByCode("1365", m2 / 4);
    addMaterialByCode("1366", m2 / 4);
    addMaterialByCode("1175", m2 / 15);
  }
  else if (selectedMaterial === "Painel") {
    addMaterialByCode("79", m2 / 2.88);
    addMaterialByCode("89", m2 / 3);
    addMaterialByCode("81", m2 / 3);
    addMaterialByCode("87", m2 / 1.185);
    addMaterialByCode("107", m2 / 0.84);
    addMaterialByCode("110", m2 / 2.14);
    addMaterialByCode("95", m2 / 1.18);
    addMaterialByCode("98", m2 / 1.18);
    addMaterialByCode("86", m2 / 1.185);
  }
  else if (selectedMaterial === "Piso") {
    const melhor = escolherMelhorPiso(m2);
    addMaterialByCode(melhor.codigo, melhor.quantidade);
  }

  document.getElementById('step3-metragem').style.display = 'none';
  mostrarResultado();
}

// ---------- Lista manual ----------
function carregarListaMateriais() {
  const lista = document.getElementById('materials-list');
  lista.innerHTML = '';
  produtos.forEach(mat => {
    const div = document.createElement('div');
    div.innerHTML = `
      <input type="number" id="qtd-${mat.codigo}" min="0" style="width:80px;" placeholder="Qtd">
      <strong>[${mat.codigo}]</strong> ${mat.nome}
    `;
    lista.appendChild(div);
  });
}

function finalizarLista() {
  materiaisSelecionados = [];
  produtos.forEach(mat => {
    const qtd = parseFloat(document.getElementById(`qtd-${mat.codigo}`).value) || 0;
    if (qtd > 0) {
      materiaisSelecionados.push({
        codigo: mat.codigo,
        nome: mat.nome,
        quantidade: qtd
      });
    }
  });
  document.getElementById('step3-lista').style.display = 'none';
  mostrarResultado();
}

// ---------- Resultado ----------
function mostrarResultado() {
  let html = "<ul>";
  materiaisSelecionados.forEach(mat => {
    html += `<li>[${mat.codigo}] ${mat.quantidade}x ${mat.nome}</li>`;
  });
  html += "</ul>";

  html += `
    <div style="color:red; font-weight:bold; margin-top:10px;">
      <p>Este cálculo é apenas uma estimativa e não considera características específicas do local de instalação nem possíveis perdas.</p>
      <p>Utilize-o apenas como referência. Para informações precisas, recomenda-se consultar um profissional de confiança.</p>
    </div>
  `;

  document.getElementById('result-content').innerHTML = html;
  document.getElementById('resultado').style.display = 'block';
}

// ---------- WhatsApp ----------
function fazerPedidoWhatsApp() {
  if (materiaisSelecionados.length === 0) {
    alert("Nenhum material selecionado!");
    return;
  }

  let mensagem = " *PEDIDO DE COMPRA WEB - BRASRIO*\n\n";
  mensagem += "*Materiais Solicitados:*\n";
  materiaisSelecionados.forEach(mat => {
    mensagem += `• [${mat.codigo}] ${mat.quantidade}x ${mat.nome}\n`;
  });

  mensagem += `\n *Total de itens:* ${materiaisSelecionados.length}\n`;
  mensagem += ` Data: ${new Date().toLocaleString('pt-BR')}\n\n`;
  mensagem += " *Observação:* Este é um cálculo estimado, para maior precisão contatar um profissional de confiança.";

  const numeroWhatsApp = "5521971252304";
  const url = `https://wa.me/${numeroWhatsApp}?text=${encodeURIComponent(mensagem)}`;
  window.open(url, '_blank');
}
