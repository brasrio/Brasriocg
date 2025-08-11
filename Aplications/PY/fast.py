import requests
from bs4 import BeautifulSoup
import pandas as pd
import time

URL = 'https://loja.fastsistemasconstrutivos.com.br/drywall'
HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36',
    'Accept-Language': 'pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7',
    'Referer': 'https://www.google.com',
    'Connection': 'keep-alive',
}

def get_all_product_links():
    print(f'🔄 Acessando a página de produtos...')
    response = requests.get(URL, headers=HEADERS)

    if response.status_code != 200:
        print(f'❌ Erro ao acessar produtos. Código: {response.status_code}')
        return []

    soup = BeautifulSoup(response.text, 'html.parser')
    products = soup.select('li.product a.woocommerce-LoopProduct-link')

    links = []
    for prod in products:
        link = prod.get('href')
        if link and link not in links:
            links.append(link)

    print(f'✅ {len(links)} produtos encontrados.')
    return links

def get_product_data(url):
    response = requests.get(url, headers=HEADERS)
    soup = BeautifulSoup(response.text, 'html.parser')

    title = soup.find('h1', class_='product_title')
    price = soup.find('p', class_='price')

    name = title.get_text(strip=True) if title else 'N/A'
    price_text = price.get_text(strip=True).replace('\xa0', ' ') if price else 'N/A'

    return {
        'Nome': name,
        'Preço': price_text,
        'Link': url
    }

def main():
    print('🚀 Iniciando coleta de dados da loja FAST...')
    links = get_all_product_links()

    produtos = []
    for i, link in enumerate(links, 1):
        print(f'📦 {i}/{len(links)} - Coletando dados de: {link}')
        data = get_product_data(link)
        produtos.append(data)
        time.sleep(1)

    df = pd.DataFrame(produtos)
    df.to_excel('produtos_fast.xlsx', index=False)
    print('✅ Planilha criada: produtos_fast.xlsx')

if __name__ == '__main__':
    main()
