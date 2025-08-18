// Variáveis globais
let produtos = {}; // Será carregado do JSON

// ---------- Carregamento de dados ----------
async function carregarProdutos() {
  try {
    const response = await fetch('produtos.json');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    produtos = await response.json();
    console.log('Produtos carregados com sucesso:', produtos);
  } catch (error) {
    console.error('Erro ao carregar produtos:', error);
    alert('Erro ao carregar a lista de produtos. Verifique se o arquivo produtos.json está disponível.');
  }
}

// Função para obter todos os produtos em uma lista plana
function getAllProducts() {
  const todosProdutos = [];
  for (const categoria in produtos) {
    if (produtos[categoria] && Array.isArray(produtos[categoria])) {
      todosProdutos.push(...produtos[categoria]);
    }
  }
  return todosProdutos;
}

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

function backToStep1() {
  // Volta do step2 para step1
  document.getElementById('step2').style.display = 'none';
  if (selectedMaterial === "Drywall") {
    document.getElementById('step1-drywall').style.display = 'block';
  } else {
    document.getElementById('step1').style.display = 'block';
    selectedMaterial = null;
    drywallSubtype = null;
  }
}

function backToStep2() {
  // Volta das etapas 3 para step2
  document.getElementById('step3-metragem').style.display = 'none';
  document.getElementById('step3-lista').style.display = 'none';
  document.getElementById('step2').style.display = 'block';
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
  // Procura em todas as categorias
  for (const categoria in produtos) {
    if (produtos[categoria] && Array.isArray(produtos[categoria])) {
      const produto = produtos[categoria].find(p => String(p.codigo) === String(code));
      if (produto) return produto;
    }
  }
  return null;
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

// ---------- Novas funções de cálculo ----------
// Perfis
function calculatePerfis(area, sistema) {
    const quantidades = {
        parede: 2.5,
        forro: 3.2,
        divisoria: 2.8
    };
    return Math.ceil(area * quantidades[sistema] * 1.05);
}

// Parafusos
function calculateParafusos(area, sistema) {
    const quantidades = {
        parede: 12,
        forro: 15,
        divisoria: 14
    };
    return Math.ceil(area * quantidades[sistema] * 1.15);
}

// Fita
function calculateFita(area, sistema) {
    const quantidades = {
        parede: 2.5,
        forro: 2.8,
        divisoria: 2.6
    };
    return Math.ceil(area * quantidades[sistema] * 1.08);
}

// Massa
function calculateMassa(area, sistema) {
    const quantidades = {
        parede: 0.8,
        forro: 0.9,
        divisoria: 0.85
    };
    return Math.round((area * quantidades[sistema] * 1.12) * 10) / 10;
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

    // Determinar o sistema baseado no tipo
    let sistema = "parede";
    if (drywallSubtype === "Teto") {
      sistema = "forro";
    }

    // Placa escolhida
    addMaterialByCode(placaSel, m2 / 2.88);
    
    // Parafusos (vêm em mil unidades - código 1521)
    const parafusosNecessarios = calculateParafusos(m2, sistema);
    addMaterialByCode("1521", Math.ceil(parafusosNecessarios / 1000));
    
    // Fita telada (vêm em rolos de 90m - código 1516)
    const fitaNecessaria = calculateFita(m2, sistema);
    addMaterialByCode("1516", Math.ceil(fitaNecessaria / 90));
    
    // Massa para acabamento (vêm em kg - código 698 para 5kg, 431 para 28kg)
    const massaNecessaria = calculateMassa(m2, sistema);
    if (massaNecessaria <= 5) {
      addMaterialByCode("698", 1); // 5kg
    } else {
      const qtd28kg = Math.ceil(massaNecessaria / 28);
      addMaterialByCode("431", qtd28kg); // 28kg
    }

    if (drywallSubtype === "Teto") {
      addMaterialByCode("33", (m2 * 0.5) / 12); // Arame
      addMaterialByCode("366", m2 / 2); // Perfil F530
      addMaterialByCode("667", m2 * 0.05); // Cantoneira
      addMaterialByCode("32", m2 * 0.02); // Regulador
      addMaterialByCode("668", m2 * 0.02); // Tabica
    } else {
      // Perfis para parede
      const perfisNecessarios = calculatePerfis(m2, sistema);
      addMaterialByCode("388", Math.ceil(perfisNecessarios * 0.4)); // Guia 48
      addMaterialByCode("387", Math.ceil(perfisNecessarios * 0.6)); // Montante 48
      addMaterialByCode("192", (m2 * 2) / 100); // Bucha 6
      addMaterialByCode("173", (m2 * 0.5) / 100); // Parafuso Frangeado
    }
  }
  else if (selectedMaterial === "PVC") {
    addMaterialByCode("163", m2 / 1.2); // Forro PVC
    addMaterialByCode("574", m2 / 6); // Roda forro
    addMaterialByCode("146", m2 / 6); // Roda forro U
    addMaterialByCode("173", (m2 * 0.5) / 100); // Parafuso Frangeado
  }
  else if (selectedMaterial === "Isopor") {
    addMaterialByCode("68", m2 / 1.2); // Forro isopor
    addMaterialByCode("19", (m2 * 5) / 100); // Parafuso ponta agulha
    addMaterialByCode("267", m2 * 2); // Presilha bigodinho
    addMaterialByCode("164", m2 * 0.5); // Pino Cadeirinha
    addMaterialByCode("216", m2 / 4); // Travessa perfil clicado
    addMaterialByCode("1365", m2 / 4); // Travessa clicado 1,25
    addMaterialByCode("1366", m2 / 4); // Travessa clicado 0,625
    addMaterialByCode("1175", m2 / 15); // Cola Selante PU
  }
  else if (selectedMaterial === "Painel") {
    // Sistema divisória
    const sistema = "divisoria";
    
    addMaterialByCode("79", m2 / 2.88); // Painel Eucatex
    
    // Perfis para divisória
    const perfisNecessarios = calculatePerfis(m2, sistema);
    addMaterialByCode("89", Math.ceil(perfisNecessarios * 0.3)); // Guia Baixa
    addMaterialByCode("81", Math.ceil(perfisNecessarios * 0.3)); // NTR Travessa 3M
    addMaterialByCode("87", Math.ceil(perfisNecessarios * 0.4)); // NTR Travessa 1185M
    
    addMaterialByCode("107", m2 / 0.84); // Batente Horizontal
    addMaterialByCode("110", m2 / 2.14); // Batente Vertical
    addMaterialByCode("95", m2 / 1.18); // Leito Branco
    addMaterialByCode("98", m2 / 1.18); // Baguete Branco
    
    // Parafusos para divisória
    const parafusosNecessarios = calculateParafusos(m2, sistema);
    addMaterialByCode("142", Math.ceil(parafusosNecessarios / 100)); // Parafuso ponta agulha 13 cento
    
    // Fita telada para divisória
    const fitaNecessaria = calculateFita(m2, sistema);
    addMaterialByCode("1516", Math.ceil(fitaNecessaria / 90)); // Fita telada branca 90m
    
    // Massa para acabamento divisória
    const massaNecessaria = calculateMassa(m2, sistema);
    if (massaNecessaria <= 5) {
      addMaterialByCode("698", 1); // 5kg
    } else {
      const qtd28kg = Math.ceil(massaNecessaria / 28);
      addMaterialByCode("431", qtd28kg); // 28kg
    }
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
  
  // Carrega produtos de todas as categorias
  const todosProdutos = getAllProducts();
  todosProdutos.forEach(mat => {
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
  const todosProdutos = getAllProducts();
  todosProdutos.forEach(mat => {
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

// ---------- Inicialização ----------
// Carrega os produtos quando a página carrega
document.addEventListener('DOMContentLoaded', function() {
  carregarProdutos();
});
