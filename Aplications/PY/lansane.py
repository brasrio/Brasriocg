import requests
from bs4 import BeautifulSoup
import pandas as pd
import time

BASE_URL = 'https://www.lansane.com.br'
HEADERS = {'User-Agent': 'Mozilla/5.0'}

def get_all_product_links():
    product_links = []
    page = 1
    while True:
        print(f'🔄 Lendo página {page}...')
        url = f'{BASE_URL}/loja/page/{page}/'
        response = requests.get(url, headers=HEADERS)

        if response.status_code != 200:
            break

        soup = BeautifulSoup(response.text, 'html.parser')
        products = soup.select('li.product a.woocommerce-LoopProduct-link')

        if not products:
            break

        for prod in products:
            link = prod.get('href')
            if link and link not in product_links:
                product_links.append(link)
        
        page += 1
        time.sleep(1)  # respeitar o site
    return product_links

def get_product_data(url):
    response = requests.get(url, headers=HEADERS)
    soup = BeautifulSoup(response.text, 'html.parser')

    title = soup.find('h1', class_='product_title')
    price = soup.find('p', class_='price')

    name = title.get_text(strip=True) if title else 'N/A'
    if price:
        price_text = price.get_text(strip=True).replace('\xa0', ' ')
    else:
        price_text = 'N/A'

    return {
        'Nome': name,
        'Preço': price_text,
        'Link': url
    }

def main():
    print('🚀 Coletando todos os links de produtos...')
    links = get_all_product_links()
    print(f'✅ {len(links)} produtos encontrados.')

    produtos = []
    for i, link in enumerate(links, 1):
        print(f'📦 {i}/{len(links)} - Coletando dados de: {link}')
        data = get_product_data(link)
        produtos.append(data)
        time.sleep(1)  # respeitar o site

    df = pd.DataFrame(produtos)
    df.to_excel('produtos_lansane.xlsx', index=False)
    print('✅ Planilha criada: produtos_lansane.xlsx')

if __name__ == '__main__':
    main()
