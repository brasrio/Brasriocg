// Dido - Extrator de Leads de Mapas (Versão Gratuita)
// Removido sistema de licenciamento e limitações

document.addEventListener('DOMContentLoaded', function() {
    const root = document.getElementById('root');
    
    // Interface simples e funcional
    root.innerHTML = `
        <div style="padding: 16px; font-family: Arial, sans-serif; width: 300px;">
            <div style="text-align: center; margin-bottom: 20px;">
                <h2 style="color: #1976d2; margin: 0;">🗺️ Dido</h2>
                <p style="color: #666; margin: 5px 0; font-size: 14px;">Extrator de Leads de Mapas</p>
            </div>
            
            <div id="status" style="text-align: center; margin-bottom: 15px;">
                <div id="recordCount" style="font-size: 18px; font-weight: bold; color: #1976d2;">
                    Carregando...
                </div>
                <div id="statusText" style="font-size: 12px; color: #666; margin-top: 5px;">
                    Registros coletados
                </div>
            </div>
            
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <button id="exportBtn" style="
                    background: #1976d2; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    📊 Exportar Excel
                </button>
                
                <button id="clearBtn" style="
                    background: #f44336; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    🗑️ Limpar Dados
                </button>
                
                <button id="helpBtn" style="
                    background: #4caf50; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    ❓ Como Usar
                </button>
                
                <button id="testBtn" style="
                    background: #ff9800; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    🔧 Testar Extração
                </button>
            </div>
            
            <div id="info" style="
                margin-top: 15px; 
                padding: 10px; 
                background: #f5f5f5; 
                border-radius: 5px; 
                font-size: 12px; 
                color: #666;
            ">
                <strong>💡 Dica:</strong> Navegue pelo Google Maps ou Bing Maps. 
                Os dados dos negócios visitados serão coletados automaticamente!
            </div>
        </div>
    `;
    
    // Funções principais
    async function updateRecordCount() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            const count = Object.keys(records).length;
            
            document.getElementById('recordCount').textContent = count;
            document.getElementById('statusText').textContent = 
                count === 1 ? 'registro coletado' : 'registros coletados';
                
            // Atualizar badge da extensão
            chrome.action.setBadgeText({ text: count > 0 ? count.toString() : '' });
            chrome.action.setBadgeBackgroundColor({ color: '#1976d2' });
            
        } catch (error) {
            console.error('Erro ao atualizar contagem:', error);
            document.getElementById('recordCount').textContent = 'Erro';
        }
    }
    
    // Função para verificar se a biblioteca XLSX está carregada
    function checkXLSXLibrary() {
        return typeof XLSX !== 'undefined' && XLSX.utils && XLSX.utils.json_to_sheet;
    }
    
    // Função para exportar em CSV como fallback
    function exportToCSV(dataArray) {
        try {
            // Criar cabeçalhos
            const headers = Object.keys(dataArray[0]);
            const csvContent = [
                headers.join(','),
                ...dataArray.map(row => 
                    headers.map(header => {
                        const value = row[header] || '';
                        // Escapar aspas e vírgulas
                        return `"${value.toString().replace(/"/g, '""')}"`;
                    }).join(',')
                )
            ].join('\n');
            
            // Criar arquivo CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `dido_leads_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
            
            return true;
        } catch (error) {
            console.error('Erro ao exportar CSV:', error);
            return false;
        }
    }
    
    async function exportData() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            
            if (Object.keys(records).length === 0) {
                alert('Nenhum dado para exportar! Navegue pelo Google Maps ou Bing Maps primeiro.');
                return;
            }
            
            // Converter objetos para array de dados
            const dataArray = Object.values(records).map(record => {
                // Função para extrair telefone de diferentes campos
                const extractPhone = (record) => {
                    // Tentar diferentes campos de telefone primeiro
                    const phoneFields = [
                        record.phone_number,
                        record.phone,
                        record.international_phone_number,
                        record.phone_numbers && record.phone_numbers[0]
                    ];
                    
                    for (const phone of phoneFields) {
                        if (phone && phone.trim()) {
                            return phone.trim();
                        }
                    }
                    
                    // Se não encontrou telefone nos campos específicos, procurar no endereço
                    const address = record.fulladdr || record.address || '';
                    if (address) {
                        // Regex para detectar telefones brasileiros
                        const phoneRegex = /\((\d{2})\)\s*(\d{4,5})-?(\d{4})/g;
                        const match = phoneRegex.exec(address);
                        if (match) {
                            return `(${match[1]}) ${match[2]}-${match[3]}`;
                        }
                        
                        // Regex alternativo para outros formatos
                        const phoneRegex2 = /\((\d{2})\)\s*(\d{4,5})\s*(\d{4})/g;
                        const match2 = phoneRegex2.exec(address);
                        if (match2) {
                            return `(${match2[1]}) ${match2[2]}-${match2[3]}`;
                        }
                    }
                    
                    return '';
                };
                
                // Função para extrair endereço limpo (sem telefone)
                const extractAddress = (record) => {
                    let address = record.fulladdr || record.address || '';
                    
                    // Remover telefones usando regex
                    // Padrão: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX
                    address = address.replace(/\((\d{2})\)\s*(\d{4,5})-?(\d{4})/g, '');
                    
                    // Remover telefones com espaços: (XX) XXXX XXXX
                    address = address.replace(/\((\d{2})\)\s*(\d{4,5})\s*(\d{4})/g, '');
                    
                    // Limpar vírgulas extras e espaços
                    address = address.replace(/,\s*,/g, ','); // Vírgulas duplas
                    address = address.replace(/,\s*$/g, ''); // Vírgula no final
                    address = address.replace(/^\s*,/g, ''); // Vírgula no início
                    address = address.replace(/\s+/g, ' '); // Múltiplos espaços
                    
                    return address.trim();
                };
                
                return {
                    'Nome': record.name || '',
                    'Endereço': extractAddress(record),
                    'Telefone': extractPhone(record),
                    'Website': record.url || record.website || '',
                    'Avaliação': record.rating || '',
                    'Avaliações': record.reviews || '',
                    'Categoria': record.primary_category || (record.categories && record.categories[0]) || '',
                    'Categorias': record.categories ? record.categories.join(', ') : '',
                    'Latitude': record.latitude || '',
                    'Longitude': record.longitude || '',
                    'URL da Listagem': record.listing_url || '',
                    'Reivindicado': record.claimed || '',
                    'Data de Coleta': new Date(record.created_at).toLocaleDateString('pt-BR'),
                    'Query de Busca': record.query || '',
                    'UUID': record.uuid || ''
                };
            });
            
            // Verificar se a biblioteca XLSX está disponível
            if (checkXLSXLibrary()) {
                // Exportar para Excel
                const worksheet = XLSX.utils.json_to_sheet(dataArray);
                
                // Ajustar largura das colunas
                const columnWidths = [
                    { wch: 30 }, // Nome
                    { wch: 40 }, // Endereço
                    { wch: 20 }, // Telefone
                    { wch: 30 }, // Website
                    { wch: 10 }, // Avaliação
                    { wch: 10 }, // Avaliações
                    { wch: 20 }, // Categoria
                    { wch: 30 }, // Categorias
                    { wch: 12 }, // Latitude
                    { wch: 12 }, // Longitude
                    { wch: 40 }, // URL da Listagem
                    { wch: 12 }, // Reivindicado
                    { wch: 15 }, // Data de Coleta
                    { wch: 30 }, // Query de Busca
                    { wch: 25 }  // UUID
                ];
                worksheet['!cols'] = columnWidths;
                
                // Criar workbook
                const workbook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(workbook, worksheet, 'Leads Coletados');
                
                // Gerar arquivo Excel
                const fileName = `dido_leads_${new Date().toISOString().split('T')[0]}.xlsx`;
                XLSX.writeFile(workbook, fileName);
                
            } else {
                // Fallback para CSV se XLSX não estiver disponível
                console.warn('Biblioteca XLSX não encontrada, exportando como CSV');
                if (exportToCSV(dataArray)) {
                    alert('Biblioteca Excel não carregada. Dados exportados como CSV (.csv) que pode ser aberto no Excel.');
                } else {
                    throw new Error('Falha ao exportar dados');
                }
            }
            
        } catch (error) {
            console.error('Erro ao exportar:', error);
            alert('Erro ao exportar dados! Tente recarregar a extensão.');
        }
    }
    
    async function clearData() {
        if (confirm('Tem certeza que deseja limpar todos os dados coletados?')) {
            try {
                await chrome.storage.local.set({ found_records: {} });
                await updateRecordCount();
                alert('Dados limpos com sucesso!');
            } catch (error) {
                console.error('Erro ao limpar dados:', error);
                alert('Erro ao limpar dados!');
            }
        }
    }
    
    function showHelp() {
        alert(`🗺️ Dido - Como Usar:

1. Navegue pelo Google Maps (maps.google.com) ou Bing Maps (bing.com/maps)
2. Faça buscas por negócios (ex: "restaurantes em São Paulo")
3. Clique nos resultados para visualizar os detalhes
4. Os dados serão coletados automaticamente
5. Use "Exportar Excel" para baixar uma planilha Excel (.xlsx)
6. Use "Limpar Dados" para remover todos os registros

✅ 100% Gratuito e Sem Limitações!
✅ Funciona com Google Maps e Bing Maps
✅ Coleta: nome, endereço, telefone, website, avaliações, etc.
✅ Exporta em Excel com colunas organizadas!`);
    }
    
    async function testExtraction() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            
            if (Object.keys(records).length === 0) {
                alert('Nenhum dado para testar! Navegue pelo Google Maps ou Bing Maps primeiro.');
                return;
            }
            
            // Pegar o primeiro registro para teste
            const firstRecord = Object.values(records)[0];
            
            // Função de teste (cópia das funções de extração)
            const extractPhone = (record) => {
                const phoneFields = [
                    record.phone_number,
                    record.phone,
                    record.international_phone_number,
                    record.phone_numbers && record.phone_numbers[0]
                ];
                
                for (const phone of phoneFields) {
                    if (phone && phone.trim()) {
                        return phone.trim();
                    }
                }
                
                const address = record.fulladdr || record.address || '';
                if (address) {
                    const phoneRegex = /\((\d{2})\)\s*(\d{4,5})-?(\d{4})/g;
                    const match = phoneRegex.exec(address);
                    if (match) {
                        return `(${match[1]}) ${match[2]}-${match[3]}`;
                    }
                }
                
                return '';
            };
            
            const extractAddress = (record) => {
                let address = record.fulladdr || record.address || '';
                address = address.replace(/\((\d{2})\)\s*(\d{4,5})-?(\d{4})/g, '');
                address = address.replace(/\((\d{2})\)\s*(\d{4,5})\s*(\d{4})/g, '');
                address = address.replace(/,\s*,/g, ',');
                address = address.replace(/,\s*$/g, '');
                address = address.replace(/^\s*,/g, '');
                address = address.replace(/\s+/g, ' ');
                return address.trim();
            };
            
            const extractedPhone = extractPhone(firstRecord);
            const extractedAddress = extractAddress(firstRecord);
            
            alert(`🔧 Teste de Extração:

📋 Dados Originais:
Nome: ${firstRecord.name || 'N/A'}
Endereço Original: ${firstRecord.fulladdr || firstRecord.address || 'N/A'}

📞 Resultado da Extração:
Telefone Extraído: ${extractedPhone || 'NÃO ENCONTRADO'}
Endereço Limpo: ${extractedAddress || 'N/A'}

${extractedPhone ? '✅ Telefone extraído com sucesso!' : '❌ Telefone não encontrado'}`);
            
        } catch (error) {
            console.error('Erro no teste:', error);
            alert('Erro ao testar extração!');
        }
    }
    
    // Event listeners
    document.getElementById('exportBtn').addEventListener('click', exportData);
    document.getElementById('clearBtn').addEventListener('click', clearData);
    document.getElementById('helpBtn').addEventListener('click', showHelp);
    document.getElementById('testBtn').addEventListener('click', testExtraction);
    
    // Atualizar contagem inicial e a cada mudança no storage
    updateRecordCount();
    chrome.storage.onChanged.addListener((changes) => {
        if (changes.found_records) {
            updateRecordCount();
        }
    });
    
    // Atualizar a cada 2 segundos para garantir sincronização
    setInterval(updateRecordCount, 2000);
});
