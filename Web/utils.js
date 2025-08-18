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

function addMaterialByCode(code, quantidade, materiaisSelecionados) {
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

// ---------- Funções de cálculo ----------
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

// ---------- Cálculo principal ----------
function calcularMateriais(material, subtype, m2, placaSel) {
  const materiaisSelecionados = [];

  if (material === "Drywall") {
    if (!subtype) {
      throw new Error("Escolha Teto ou Parede");
    }

    // Determinar o sistema baseado no tipo
    let sistema = "parede";
    if (subtype === "Teto") {
      sistema = "forro";
    }

    // Placa escolhida
    addMaterialByCode(placaSel, m2 / 2.88, materiaisSelecionados);
    
    // Parafusos (vêm em mil unidades - código 1521)
    const parafusosNecessarios = calculateParafusos(m2, sistema);
    addMaterialByCode("1521", Math.ceil(parafusosNecessarios / 1000), materiaisSelecionados);
    
    // Fita telada (vêm em rolos de 90m - código 1516)
    const fitaNecessaria = calculateFita(m2, sistema);
    addMaterialByCode("1516", Math.ceil(fitaNecessaria / 90), materiaisSelecionados);
    
    // Massa para acabamento (vêm em kg - código 698 para 5kg, 431 para 28kg)
    const massaNecessaria = calculateMassa(m2, sistema);
    if (massaNecessaria <= 5) {
      addMaterialByCode("698", 1, materiaisSelecionados); // 5kg
    } else {
      const qtd28kg = Math.ceil(massaNecessaria / 28);
      addMaterialByCode("431", qtd28kg, materiaisSelecionados); // 28kg
    }

    if (subtype === "Teto") {
      addMaterialByCode("33", (m2 * 0.5) / 12, materiaisSelecionados); // Arame
      addMaterialByCode("366", m2 / 2, materiaisSelecionados); // Perfil F530
      addMaterialByCode("667", m2 * 0.05, materiaisSelecionados); // Cantoneira
      addMaterialByCode("32", m2 * 0.02, materiaisSelecionados); // Regulador
      addMaterialByCode("668", m2 * 0.02, materiaisSelecionados); // Tabica
    } else {
      // Perfis para parede
      const perfisNecessarios = calculatePerfis(m2, sistema);
      addMaterialByCode("388", Math.ceil(perfisNecessarios * 0.4), materiaisSelecionados); // Guia 48
      addMaterialByCode("387", Math.ceil(perfisNecessarios * 0.6), materiaisSelecionados); // Montante 48
      addMaterialByCode("192", (m2 * 2) / 100, materiaisSelecionados); // Bucha 6
      addMaterialByCode("173", (m2 * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado
    }
  }
  else if (material === "PVC") {
    addMaterialByCode("163", m2 / 1.2, materiaisSelecionados); // Forro PVC
    addMaterialByCode("574", m2 / 6, materiaisSelecionados); // Roda forro
    addMaterialByCode("146", m2 / 6, materiaisSelecionados); // Roda forro U
    addMaterialByCode("173", (m2 * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado
  }
  else if (material === "Isopor") {
    addMaterialByCode("68", m2 / 1.2, materiaisSelecionados); // Forro isopor
    addMaterialByCode("19", (m2 * 5) / 100, materiaisSelecionados); // Parafuso ponta agulha
    addMaterialByCode("267", m2 * 2, materiaisSelecionados); // Presilha bigodinho
    addMaterialByCode("164", m2 * 0.5, materiaisSelecionados); // Pino Cadeirinha
    addMaterialByCode("216", m2 / 4, materiaisSelecionados); // Travessa perfil clicado
    addMaterialByCode("1365", m2 / 4, materiaisSelecionados); // Travessa clicado 1,25
    addMaterialByCode("1366", m2 / 4, materiaisSelecionados); // Travessa clicado 0,625
    addMaterialByCode("1175", m2 / 15, materiaisSelecionados); // Cola Selante PU
  }
  else if (material === "Painel") {
    // Sistema divisória
    const sistema = "divisoria";
    
    addMaterialByCode("79", m2 / 2.88, materiaisSelecionados); // Painel Eucatex
    
    // Perfis para divisória
    const perfisNecessarios = calculatePerfis(m2, sistema);
    addMaterialByCode("89", Math.ceil(perfisNecessarios * 0.3), materiaisSelecionados); // Guia Baixa
    addMaterialByCode("81", Math.ceil(perfisNecessarios * 0.3), materiaisSelecionados); // NTR Travessa 3M
    addMaterialByCode("87", Math.ceil(perfisNecessarios * 0.4), materiaisSelecionados); // NTR Travessa 1185M
    
    addMaterialByCode("107", m2 / 0.84, materiaisSelecionados); // Batente Horizontal
    addMaterialByCode("110", m2 / 2.14, materiaisSelecionados); // Batente Vertical
    addMaterialByCode("95", m2 / 1.18, materiaisSelecionados); // Leito Branco
    addMaterialByCode("98", m2 / 1.18, materiaisSelecionados); // Baguete Branco
    
    // Parafusos para divisória
    const parafusosNecessarios = calculateParafusos(m2, sistema);
    addMaterialByCode("142", Math.ceil(parafusosNecessarios / 100), materiaisSelecionados); // Parafuso ponta agulha 13 cento
    
    // Fita telada para divisória
    const fitaNecessaria = calculateFita(m2, sistema);
    addMaterialByCode("1516", Math.ceil(fitaNecessaria / 90), materiaisSelecionados); // Fita telada branca 90m
    
    // Massa para acabamento divisória
    const massaNecessaria = calculateMassa(m2, sistema);
    if (massaNecessaria <= 5) {
      addMaterialByCode("698", 1, materiaisSelecionados); // 5kg
    } else {
      const qtd28kg = Math.ceil(massaNecessaria / 28);
      addMaterialByCode("431", qtd28kg, materiaisSelecionados); // 28kg
    }
  }
  else if (material === "Piso") {
    const melhor = escolherMelhorPiso(m2);
    addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados);
  }

  return materiaisSelecionados;
}
