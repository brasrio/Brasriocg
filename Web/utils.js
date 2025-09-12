// Variáveis globais
let produtos = {}; // Será carregado do JSON

// Configurações de materiais para parede
const MATERIAIS_PAREDE = {
  placas: {
    "280": { nome: "Drywall ST Branco", area: 2.16 }, // 1,80 x 1,20
    "177": { nome: "Drywall RU (Resistente à Umidade)", area: 2.16 }, // 1,80 x 1,20
    "193": { nome: "Drywall RF (Resistente à fogo)", area: 2.16 }, // 1,80 x 1,20
    "181": { nome: "Placa Cimentícia 8MM", area: 2.88 }, // 2,40 x 1,20
    "182": { nome: "Placa Cimentícia 10MM", area: 2.88 }, // 2,40 x 1,20
    "263": { nome: "Placa Cimentícia 6MM", area: 2.88 }, // 2,40 x 1,20
    "1172": { nome: "Placa Cimentícia 12MM", area: 2.88 } // 2,40 x 1,20
  }
};

// Classe para cálculo de parede - agora baseada em m² + pé direito
class ParedeCalculator {
    constructor(metrosQuadrados, peDireito, tipoPlaca) {
        this.metrosQuadrados = metrosQuadrados;
        this.peDireito = peDireito;
        this.tipoPlaca = tipoPlaca;
        this.comprimento = metrosQuadrados / peDireito; // Calcula comprimento automaticamente
    }

    calcularArea() {
        return this.metrosQuadrados;
    }

    calcularMateriais() {
        const area = this.calcularArea();
        const placaConfig = MATERIAIS_PAREDE.placas[this.tipoPlaca];
        
        if (!placaConfig) {
            throw new Error("Tipo de placa não encontrado");
        }

        const materiaisSelecionados = [];
        const sistema = "parede";

        // Placa escolhida - cálculo baseado na área real (aumentada em 20% para parede)
        const quantidadePlacas = Math.ceil((area / placaConfig.area) * 4.45);
        addMaterialByCode(this.tipoPlaca, quantidadePlacas, materiaisSelecionados);
        
        // Parafusos (vêm em mil unidades - código 1521)
        const parafusosNecessarios = calculateParafusos(area, sistema);
        addMaterialByCode("1521", Math.ceil(parafusosNecessarios / 1000), materiaisSelecionados);
        
        // Fita telada (vêm em rolos de 90m - código 1516)
        const fitaNecessaria = calculateFita(area, sistema);
        addMaterialByCode("1516", Math.ceil(fitaNecessaria / 90), materiaisSelecionados);
        
        // Massa para acabamento (vêm em kg - código 698 para 5kg, 431 para 28kg)
        const massaNecessaria = calculateMassa(area, sistema);
        if (massaNecessaria <= 5) {
            addMaterialByCode("698", 1, materiaisSelecionados); // 5kg
        } else {
            const qtd28kg = Math.ceil(massaNecessaria / 28);
            addMaterialByCode("431", qtd28kg, materiaisSelecionados); // 28kg
        }

        // Perfis para parede - baseado no comprimento calculado e pé direito
        const montantesNecessarios = Math.ceil(this.comprimento / 0.6) + 1; // A cada 60cm + extremidades
        const guiasNecessarias = Math.ceil((this.comprimento * 2) / 3); // Guias superior e inferior
        
        addMaterialByCode("388", Math.ceil(guiasNecessarias * 3), materiaisSelecionados); // Guia 48 (aumentada em 200%)
        addMaterialByCode("387", Math.ceil(montantesNecessarios * 1.1), materiaisSelecionados); // Montante 48 (aumentado em 10%)
        addMaterialByCode("192", (area * 2) / 100, materiaisSelecionados); // Bucha 6
        addMaterialByCode("173", (area * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado

        return materiaisSelecionados;
    }

    getResumo() {
        return {
            metrosQuadrados: this.metrosQuadrados,
            peDireito: this.peDireito,
            comprimento: this.comprimento.toFixed(2),
            tipoPlaca: this.tipoPlaca
        };
    }
}

// Classe para cálculo de placas cimentícias
class CimenticiaCalculator {
    constructor(metrosQuadrados, peDireito, tipoPlaca, sistema) {
        this.metrosQuadrados = metrosQuadrados;
        this.peDireito = peDireito;
        this.tipoPlaca = tipoPlaca;
        this.sistema = sistema; // 'teto' ou 'parede'
        this.comprimento = metrosQuadrados / peDireito; // Calcula comprimento automaticamente
    }

    calcularArea() {
        return this.metrosQuadrados;
    }

    calcularMateriais() {
        const area = this.calcularArea();
        const materiaisSelecionados = [];

        // Placa cimentícia selecionada - área 2.88 m² (2.40 x 1.20)
        const quantidadePlacas = Math.ceil(area / 2.88);
        addMaterialByCode(this.tipoPlaca, quantidadePlacas, materiaisSelecionados);

        // Materiais específicos do sistema cimentícia
        if (this.sistema === 'teto') {
            // Para teto: painel wall (código 464), massa para projeto cimentícia (código 582), fita cimentícia (código 1518)
            addMaterialByCode("464", quantidadePlacas, materiaisSelecionados); // Painel wall
            addMaterialByCode("582", Math.ceil(area * 0.5), materiaisSelecionados); // Massa para projeto cimentícia
            addMaterialByCode("1518", Math.ceil(area * 1.2), materiaisSelecionados); // Fita cimentícia
        } else {
            // Para parede: mesmo sistema mas com cálculos específicos para parede
            addMaterialByCode(this.tipoPlaca, Math.ceil(quantidadePlacas * 1.2), materiaisSelecionados); // Placa aumentada em 20% para parede
            addMaterialByCode("582", Math.ceil(area * 0.6), materiaisSelecionados); // Massa para projeto cimentícia
            addMaterialByCode("1518", Math.ceil(area * 1.4), materiaisSelecionados); // Fita cimentícia
            
            // Perfis para parede cimentícia
            const montantesNecessarios = Math.ceil(this.comprimento / 0.6) + 1;
            const guiasNecessarias = Math.ceil((this.comprimento * 2) / 3);
            
            addMaterialByCode("388", Math.ceil(guiasNecessarias * 3), materiaisSelecionados); // Guia 48
            addMaterialByCode("387", Math.ceil(montantesNecessarios * 1.1), materiaisSelecionados); // Montante 48
            addMaterialByCode("192", (area * 2) / 100, materiaisSelecionados); // Bucha 6
            addMaterialByCode("173", (area * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado
        }

        return materiaisSelecionados;
    }

    getResumo() {
        return {
            metrosQuadrados: this.metrosQuadrados,
            peDireito: this.peDireito,
            comprimento: this.comprimento.toFixed(2),
            tipoPlaca: this.tipoPlaca,
            sistema: this.sistema
        };
    }
}

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
  // Se produtos é um array simples, converte para formato compatível
  if (Array.isArray(produtos)) {
    return produtos.map(prod => {
      const chaves = Object.keys(prod);
      let descricao = "NÃO ENCONTRADO";
      for (const chave of chaves) {
        if (chave !== "1") { // "1" é o código, as outras chaves contêm a descrição
          descricao = prod[chave];
          break;
        }
      }
      return {
        CodigoProduto: prod["1"],
        Descricao: descricao
      };
    });
  }
  // Fallback para estrutura com categorias
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
  // Procura no array de produtos
  if (Array.isArray(produtos)) {
    return produtos.find(p => String(p["1"]) === String(code));
  }
  // Fallback para estrutura com categorias (caso o formato mude no futuro)
  for (const categoria in produtos) {
    if (produtos[categoria] && Array.isArray(produtos[categoria])) {
      const produto = produtos[categoria].find(p => String(p["1"]) === String(code));
      if (produto) return produto;
    }
  }
  return null;
}

function addMaterialByCode(code, quantidade, materiaisSelecionados) {
  const prod = findProductByCode(code);
  let descricao = "NÃO ENCONTRADO";
  
  if (prod) {
    // A descrição está no valor da chave "PLACA ST 1.80 X 1.20 KNAUF"
    const chaves = Object.keys(prod);
    for (const chave of chaves) {
      if (chave !== "1") { // "1" é o código, as outras chaves contêm a descrição
        descricao = prod[chave];
        break;
      }
    }
  }
  
  // Só adiciona se a quantidade for maior que 0
  if (quantidade > 0) {
    materiaisSelecionados.push({
      codigo: code,
      nome: descricao,
      quantidade: Math.ceil(quantidade)
    });
  }
}

// Escolhe o piso vinílico com menor sobra (empate: menor quantidade; depois maior área por caixa)
function escolherMelhorPisoVinilico(m2) {
  const opcoes = [
    { codigo: "1574", area: 3.90, nome: "PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA" },
    { codigo: "1570", area: 3.90, nome: "PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA" },
    { codigo: "1599", area: 2.6, nome: "PISO VINILICO RUFFINO BRAVO COR ANGELIM" },
    { codigo: "1575", area: 3.90, nome: "PISO VINILICO RUFFINO NOBILE COLADO BAOBA" },
    { codigo: "1576", area: 3.90, nome: "PISO VINILICO RUFFINO NOBILE COLADO DAMASCO" }
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

// Escolhe o piso laminado com menor sobra
function escolherMelhorPisoLaminado(m2) {
  const opcoes = [
    { codigo: "1102", area: 2.41, nome: "PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM" },
    { codigo: "1236", area: 2.51, nome: "PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO" },
    { codigo: "1401", area: 2.84, nome: "PISO LAMINADO QUICK STEP PREMIERE MOCHA" }
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

// Função auxiliar para obter área do piso vinílico por código
function getAreaPisoVinilico(codigo) {
  const areas = {
    "1574": 3.90,
    "1570": 3.90,
    "1599": 2.6,
    "1575": 3.90,
    "1576": 3.90
  };
  return areas[String(codigo)] || 3.90; // Default para 3.90 se não encontrar
}

// Função auxiliar para obter área do piso laminado por código
function getAreaPisoLaminado(codigo) {
  const areas = {
    "1102": 2.41,
    "1236": 2.51,
    "1401": 2.84
  };
  return areas[String(codigo)] || 2.41; // Default para 2.41 se não encontrar
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
        parede: 1.25, // Reduzido pela metade (era 2.5)
        forro: 1.4,   // Reduzido pela metade (era 2.8)
        divisoria: 1.3 // Reduzido pela metade (era 2.6)
    };
    return Math.ceil(area * quantidades[sistema] * 1.08);
}

// Massa
function calculateMassa(area, sistema) {
    const quantidades = {
        parede: 0.4, // Reduzido pela metade (era 0.8)
        forro: 0.45, // Reduzido pela metade (era 0.9)
        divisoria: 0.425 // Reduzido pela metade (era 0.85)
    };
    return Math.round((area * quantidades[sistema] * 1.12) * 10) / 10;
}

// ---------- Cálculo principal ----------
function calcularMateriais(material, subtype, m2, placaSel, quantidadeJanelas = 0, quantidadePortas = 0, peDireito = null) {
  const materiaisSelecionados = [];

  if (material === "Cimenticia") {
    if (!subtype) {
      throw new Error("Escolha Teto ou Parede");
    }

    // Determinar o sistema baseado no tipo
    let sistema = "parede";
    if (subtype === "Teto") {
      sistema = "teto";
    }

    // Usar a classe CimenticiaCalculator
    const calculator = new CimenticiaCalculator(m2, peDireito || 2.7, placaSel, sistema);
    const materiaisCimenticia = calculator.calcularMateriais();
    materiaisSelecionados.push(...materiaisCimenticia);

  } else if (material === "Drywall") {
    if (!subtype) {
      throw new Error("Escolha Teto ou Parede");
    }

    // Determinar o sistema baseado no tipo
    let sistema = "parede";
    if (subtype === "Teto") {
      sistema = "forro";
    }

    // Placa escolhida
    if (subtype === "Parede" || subtype === "parede") {
      addMaterialByCode(placaSel, Math.ceil((m2 / 2.16) * 4.45), materiaisSelecionados); // Placa aumentada em 20% para parede (era 371%)
    } else {
      addMaterialByCode(placaSel, Math.ceil(m2 / 2.16), materiaisSelecionados);
    }
    
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
      addMaterialByCode("366", m2 / 4, materiaisSelecionados); // Perfil F530 (reduzido novamente - era m2/3)
      addMaterialByCode("667", m2 * 0.05, materiaisSelecionados); // Cantoneira
      addMaterialByCode("32", m2 * 0.24, materiaisSelecionados); // Regulador (aumentado 100% novamente - era 0.12)
    } else {
      // Perfis para parede
      const perfisNecessarios = calculatePerfis(m2, sistema);
      addMaterialByCode("388", Math.ceil(perfisNecessarios * 0.4 * 3), materiaisSelecionados); // Guia 48 (aumentada em 200% - era 150%)
      addMaterialByCode("387", Math.ceil(perfisNecessarios * 0.6 * 1.1), materiaisSelecionados); // Montante 48 (aumentado em 10%)
      addMaterialByCode("192", (m2 * 2) / 100, materiaisSelecionados); // Bucha 6
      addMaterialByCode("173", (m2 * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado
    }
  }
  else if (material === "PVC") {
    addMaterialByCode("163", Math.ceil(m2 * 1.28), materiaisSelecionados); // Forro PVC
    addMaterialByCode("574", m2 / 6, materiaisSelecionados); // Roda forro
    addMaterialByCode("146", m2 / 6, materiaisSelecionados); // Roda forro U
    addMaterialByCode("173", (m2 * 0.5) / 100, materiaisSelecionados); // Parafuso Frangeado
  }
  else if (material === "Isopor") {
    // Forro isopor: cada pacote cobre 19,2m² (conforme descrição do produto)
    addMaterialByCode("68", Math.ceil(m2 / 19.2), materiaisSelecionados); // Forro isopor
    addMaterialByCode("19", (m2 * 5) / 100, materiaisSelecionados); // Parafuso ponta agulha
    // Presilha bigode: vem com 50 unidades por pacote
    addMaterialByCode("267", Math.ceil((m2 * 2) / 50), materiaisSelecionados); // Presilha bigodinho
    // Pino clip: vem com 100 unidades por pacote (cento)
    addMaterialByCode("164", Math.ceil((m2 * 0.5) / 100), materiaisSelecionados); // Pino Cadeirinha
    addMaterialByCode("1364", m2 / 4, materiaisSelecionados); // Travessa perfil clicado
    addMaterialByCode("1365", m2 / 4, materiaisSelecionados); // Travessa clicado 1,25
    addMaterialByCode("1366", m2 / 4, materiaisSelecionados); // Travessa clicado 0,625
    addMaterialByCode("1175", m2 / 15, materiaisSelecionados); // Cola Selante PU
  }
  else if (material === "Painel") {
    // Sistema divisória naval
    const sistema = "divisoria";
    
    // Calcula quantidade de painéis baseado nos metros quadrados
    const quantidadePaineis = Math.ceil(m2 / 2.53);
    
    // Cálculo baseado na quantidade de painéis
    addMaterialByCode("79", quantidadePaineis, materiaisSelecionados); // Painel Eucatex (Divisória Naval)
    addMaterialByCode("89", Math.ceil(quantidadePaineis * 1.22), materiaisSelecionados); // Guia Baixa (U) Branca 3.00 mts
    addMaterialByCode("81", Math.ceil(quantidadePaineis * 1), materiaisSelecionados); // NTR Travessa 3M
    addMaterialByCode("87", Math.ceil(quantidadePaineis * 1), materiaisSelecionados); // NTR Travessa 1185 M
    
    // Requadros e Batentes só são incluídos se houver portas
    if (quantidadePortas > 0) {
      addMaterialByCode("102", Math.ceil(quantidadePortas * 2), materiaisSelecionados); // Requadro Horizontal 0,81 M
      addMaterialByCode("101", Math.ceil(quantidadePortas * 2), materiaisSelecionados); // Requadro Vertical 2,11 M
      addMaterialByCode("107", Math.ceil(quantidadePortas * 1), materiaisSelecionados); // Batente Horizontal 0,84 M
      addMaterialByCode("110", Math.ceil(quantidadePortas * 2), materiaisSelecionados); // Batente Vertical 2,14 M
    }
    
    // Materiais para janelas (se houver)
    if (quantidadeJanelas > 0) {
      addMaterialByCode("95", Math.ceil(quantidadeJanelas * 4), materiaisSelecionados); // Leito Branco 1,18 mts
      addMaterialByCode("98", Math.ceil(quantidadeJanelas * 4), materiaisSelecionados); // Baguete Branco 1,18 mts
    }
  }
  else if (material === "Piso") {
    if (!subtype) {
      throw new Error("Escolha Vinílico ou Laminado");
    }
    
    if (subtype === "Vinílico") {
      // Se foi especificado um código de cor específico, usa ele
      if (placaSel && ['1574', '1570', '1599', '1575', '1576'].includes(String(placaSel))) {
        const areaPiso = getAreaPisoVinilico(placaSel);
        const quantidade = Math.ceil(m2 / areaPiso);
        addMaterialByCode(placaSel, quantidade, materiaisSelecionados);
        addMaterialByCode("947", quantidade, materiaisSelecionados); // MASSA NIVELADORA PISO SC/ 4KG MAPEI
      } else {
        // Senão, usa a lógica de escolha automática
        const melhor = escolherMelhorPisoVinilico(m2);
        addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados);
        addMaterialByCode("947", melhor.quantidade, materiaisSelecionados); // MASSA NIVELADORA PISO SC/ 4KG MAPEI
      }
    } else if (subtype === "Laminado") {
      // Se foi especificado um código de cor específico, usa ele
      if (placaSel && ['1102', '1236', '1401'].includes(String(placaSel))) {
        const areaPiso = getAreaPisoLaminado(placaSel);
        const quantidade = Math.ceil(m2 / areaPiso);
        addMaterialByCode(placaSel, quantidade, materiaisSelecionados);
        addMaterialByCode("447", Math.ceil(m2 / 1.2), materiaisSelecionados); // MANTA P/ PISO LAMINADO 1,20ML
      } else {
        // Senão, usa a lógica de escolha automática
        const melhor = escolherMelhorPisoLaminado(m2);
        addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados);
        addMaterialByCode("447", Math.ceil(m2 / 1.2), materiaisSelecionados); // MANTA P/ PISO LAMINADO 1,20ML
      }
    }
  }

  return materiaisSelecionados;
}
