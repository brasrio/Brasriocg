// Lista fixa de produtos mais comuns + PVC e Isopor
const produtos = [
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
  { codigo: "1175", nome: "Cola selante PU" },
  { codigo: "142", nome: "Parafuso ponta agulha 13 cento" },
  { codigo: "1521", nome: "Parafuso ponta agulha GN25 CX com mil" },
  { codigo: "1364", nome: "Perfil Clicado" },
  { codigo: "366", nome: "Perfil F530 barra" },
  { codigo: "164", nome: "Pino Cadeirinha" },
  { codigo: "280", nome: "Placa drywall comum" },
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
  { codigo: "68", nome: "Forro isopor 20mm" },
  { codigo: "19", nome: "Parafuso ponta agulha 13" },
  { codigo: "1365", nome: "Travessa clicado 1,25" },
  { codigo: "1366", nome: "Travessa clicado 0,625" }
];

let selectedMaterial = null;
let drywallSubtype = null;
let materiaisSelecionados = [];

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

function addMaterialByCode(code, quantidade) {
  let prod = findProductByCode(code);
  materiaisSelecionados.push({
    codigo: code,
    nome: prod ? prod.nome : "NÃO ENCONTRADO",
    quantidade: Math.ceil(quantidade)
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
    addMaterialByCode("280", m2 / 2.88);                 // Placa drywall
    addMaterialByCode("1521", (m2 * 20) / 1000);          // GN25 (caixa 1000)
    addMaterialByCode("1516", m2 / 30);                   // Fita telada

    if (drywallSubtype === "Teto") {
      addMaterialByCode("33", (m2 * 0.5) / 12);           // Arame 10 (kg)
      addMaterialByCode("366", m2 / 0.6);                 // Perfil F530
      addMaterialByCode("667", m2 * 0.05);                // Cantoneira
      addMaterialByCode("32", m2 * 0.02);                 // Regulador
      addMaterialByCode("668", m2 * 0.02);                 // Tabica
    } else { // Parede
      addMaterialByCode("388", m2 / 3);                   // Guia 48
      addMaterialByCode("387", m2 / 0.6);                 // Montante 48
      addMaterialByCode("192", (m2 * 2) / 100);           // Bucha 6 (100 unid)
      addMaterialByCode("173", (m2 * 0.5) / 100);         // Parafuso frangeado (100 unid)
    }
  }

  else if (selectedMaterial === "PVC") {
    addMaterialByCode("163", m2 / 1.2);                   // Forro PVC Modular
    addMaterialByCode("574", m2 / 6);                     // Roda Forro Moldura
    addMaterialByCode("146", m2 / 6);                     // Roda Forro U
    addMaterialByCode("173", (m2 * 0.5) / 100);           // Parafuso frangeado (100 unid)
  }

  else if (selectedMaterial === "Isopor") {
    addMaterialByCode("68", m2 / 1.2);                    // Forro isopor
    addMaterialByCode("19", (m2 * 5) / 100);              // Parafuso ponta agulha 13 (100 unid)
    addMaterialByCode("267", m2 * 2);                     // Presilha bigodinho
    addMaterialByCode("164", m2 * 0.5);                   // Pino cadeirinha
    addMaterialByCode("216", m2 / 4);                     // Travessa clicado branco
    addMaterialByCode("1365", m2 / 4);                    // Travessa clicado 1,25
    addMaterialByCode("1366", m2 / 4);                    // Travessa clicado 0,625
    addMaterialByCode("1175", m2 / 15);                   // Cola selante PU
  }

  document.getElementById('step3-metragem').style.display = 'none';
  mostrarResultado();
}

// ---------- Lista manual ----------
function carregarListaMateriais() {
  let lista = document.getElementById('materials-list');
  lista.innerHTML = '';
  produtos.forEach(mat => {
    let div = document.createElement('div');
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
    let qtd = parseFloat(document.getElementById(`qtd-${mat.codigo}`).value) || 0;
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
  html += `<p style="color:red;font-weight:bold;">
    Estes são materiais sugeridos. Consulte o instalador para confirmar as quantidades exatas.
  </p>`;
  document.getElementById('result-content').innerHTML = html;
  document.getElementById('resultado').style.display = 'block';
}
