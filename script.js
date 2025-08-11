let selectedMaterial = null;
let drywallSubtype = null;
let materiaisSelecionados = [];
let produtos = [];
let produtosCarregados = false;

// Fallback básico caso o fetch falhe (códigos importantes)
const produtosBackup = [
  { codigo: "33", nome: "Arame de 10", valor: 0 },
  { codigo: "192", nome: "Bucha 6", valor: 0 },
  { codigo: "667", nome: "Cantoneira 25x30", valor: 0 },
  { codigo: "1521", nome: "Parafuso ponta agulha GN25 CX com mil", valor: 0 },
  { codigo: "1516", nome: "Fita telada branca 90mt Home", valor: 0 },
  { codigo: "280", nome: "Placa drywall comum", valor: 0 },
  { codigo: "366", nome: "Perfil F530 barra", valor: 0 },
  { codigo: "32", nome: "Regulador F530", valor: 0 },
  { codigo: "668", nome: "Tabica barra", valor: 0 },
  { codigo: "388", nome: "Guia 48", valor: 0 },
  { codigo: "387", nome: "Montante 48", valor: 0 },
  { codigo: "173", nome: "Parafuso Frangeado 45", valor: 0 },
  { codigo: "68", nome: "Forro isopor 20mm", valor: 0 },
  { codigo: "216", nome: "Travessa perfil clicado branco", valor: 0 },
  { codigo: "267", nome: "Presilha bigodinho para forro isopor", valor: 0 },
  { codigo: "1175", nome: "COLA SELANTE PU", valor: 0 },
  { codigo: "431", nome: "Massa kolimar 28kg", valor: 0 },
  { codigo: "698", nome: "Massa kolimar 5kg", valor: 0 },
  { codigo: "1431", nome: "Painel divisoria cristal (cinza)", valor: 0 }
];

// Carrega JSON ou usa backup
fetch("produtos.json")
  .then(r => {
    if (!r.ok) throw new Error("Erro ao carregar JSON");
    return r.json();
  })
  .then(data => {
    produtos = data.map(p => ({
      codigo: String(p.codigo),
      nome: String(p.nome),
      valor: Number(p.valor) || 0
    }));
    produtosCarregados = true;
  })
  .catch(() => {
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

function addMaterialByCode(code, quantidade) {
  let prod = findProductByCode(code);
  materiaisSelecionados.push({
    codigo: code,
    nome: prod ? prod.nome : "NÃO ENCONTRADO",
    quantidade: Math.ceil(quantidade),
    valor: prod ? prod.valor : 0
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
    // Placa drywall (2,88m²)
    addMaterialByCode("280", m2 / 2.88);
    addMaterialByCode("1521", m2 * 20); // Parafuso GN25
    addMaterialByCode("1516", m2 / 30); // Fita telada

    if (drywallSubtype === "Teto") {
      addMaterialByCode("33", m2 * 0.5); // Arame
      addMaterialByCode("366", m2 / 0.6); // Perfil F530
      addMaterialByCode("667", m2 * 0.05); // Cantoneira
      addMaterialByCode("32", m2 * 0.02); // Regulador
      addMaterialByCode("668", m2 * 0.02); // Tabica
    } else {
      addMaterialByCode("388", m2 / 3);   // Guia
      addMaterialByCode("387", m2 / 0.6); // Montante
      addMaterialByCode("192", m2 * 2);   // Bucha
      addMaterialByCode("173", m2 * 0.5); // Parafuso Frangeado
    }
  }

  else if (selectedMaterial === "PVC") {
    addMaterialByCode("99", m2 / 0.20);  // Baguete Preto (peça ~0,20m²)
    addMaterialByCode("387", m2 / 0.6);  // Montante
    addMaterialByCode("173", m2 * 0.5);  // Parafuso Frangeado
  }

  else if (selectedMaterial === "Gesso") {
    addMaterialByCode("431", m2 / 30);   // Massa kolimar 28kg (30m²/emb)
    addMaterialByCode("1518", m2 / 30);  // Fita Cimenticia
    addMaterialByCode("192", m2 * 2);    // Bucha 6
    addMaterialByCode("1521", m2 * 15);  // Parafuso GN25
  }

  else if (selectedMaterial === "Isopor") {
    addMaterialByCode("68", m2 / 1.2);   // Forro isopor 20mm
    addMaterialByCode("216", m2 / 4);    // Travessa perfil clicado
    addMaterialByCode("267", m2 * 2);    // Presilha bigodinho
    addMaterialByCode("1175", m2 / 15);  // Cola selante PU
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
      <strong>[${mat.codigo}]</strong> ${mat.nome} - R$ ${mat.valor.toFixed(2)}
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
        quantidade: qtd,
        valor: mat.valor
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
    html += `<li>[${mat.codigo}] ${mat.quantidade}x ${mat.nome} - R$ ${mat.valor.toFixed(2)}</li>`;
  });
  html += "</ul>";
  html += `<p style="color:red;font-weight:bold;">
    Estes são materiais sugeridos. Consulte o instalador para confirmar as quantidades exatas.
  </p>`;
  document.getElementById('result-content').innerHTML = html;
  document.getElementById('resultado').style.display = 'block';
}
