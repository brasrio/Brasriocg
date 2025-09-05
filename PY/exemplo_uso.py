#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Exemplo de uso da aplicação Google Business Finder
Este script demonstra como usar a classe GoogleBusinessFinder programaticamente
"""

import os
from google_business_finder import GoogleBusinessFinder

def exemplo_busca_simples():
    """Exemplo de busca simples por categoria"""
    print("🔍 Exemplo: Busca por 'padaria'")
    
    # Verificar se a API key está configurada
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        print("❌ Configure a variável GOOGLE_PLACES_API_KEY primeiro")
        return
    
    # Criar instância do buscador
    finder = GoogleBusinessFinder(api_key)
    
    # Buscar padarias
    query = "padaria"
    max_results = 10
    
    print(f"Buscando até {max_results} padarias...")
    
    # Executar busca e exportar
    output_file = finder.search_and_export(query, max_results=max_results)
    
    if output_file:
        print(f"✅ Busca concluída! Arquivo salvo: {output_file}")
    else:
        print("❌ Nenhum resultado encontrado")

def exemplo_busca_localizada():
    """Exemplo de busca por localização específica"""
    print("\n📍 Exemplo: Busca por 'farmácia' em 'São Paulo, SP'")
    
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        print("❌ Configure a variável GOOGLE_PLACES_API_KEY primeiro")
        return
    
    finder = GoogleBusinessFinder(api_key)
    
    # Buscar farmácias em São Paulo
    query = "farmácia"
    location = "São Paulo, SP"
    radius = 10000  # 10km
    max_results = 15
    
    print(f"Buscando até {max_results} farmácias em {location} (raio: {radius//1000}km)...")
    
    # Buscar lugares
    places = finder.search_places(query, location, radius)
    
    if places:
        print(f"Encontrados {len(places)} lugares")
        
        # Processar apenas os primeiros resultados para o exemplo
        places = places[:max_results]
        
        # Coletar informações de contato
        contact_data = []
        
        for i, place in enumerate(places):
            print(f"Processando {i+1}/{len(places)}: {place.get('name', '')}")
            
            # Buscar detalhes completos
            details = finder.get_place_details(place['place_id'])
            
            # Extrair informações de contato
            contact_info = finder.extract_contact_info(place, details)
            contact_data.append(contact_info)
        
        # Criar DataFrame
        import pandas as pd
        df = pd.DataFrame(contact_data)
        
        # Mostrar resultados
        print(f"\n📊 Resultados encontrados:")
        print(df[['nome', 'telefone', 'endereco']].to_string(index=False))
        
        # Exportar para Excel
        output_file = f"exemplo_farmacias_sp_{len(contact_data)}.xlsx"
        df.to_excel(output_file, index=False)
        print(f"\n✅ Dados exportados para: {output_file}")
        
    else:
        print("❌ Nenhum lugar encontrado")

def exemplo_busca_restaurantes():
    """Exemplo de busca por restaurantes"""
    print("\n🍽️ Exemplo: Busca por 'restaurante'")
    
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        print("❌ Configure a variável GOOGLE_PLACES_API_KEY primeiro")
        return
    
    finder = GoogleBusinessFinder(api_key)
    
    # Buscar restaurantes
    query = "restaurante"
    max_results = 5
    
    print(f"Buscando até {max_results} restaurantes...")
    
    # Buscar lugares
    places = finder.search_places(query)
    
    if places:
        places = places[:max_results]
        
        print(f"\n📋 Restaurantes encontrados:")
        for i, place in enumerate(places, 1):
            print(f"{i}. {place.get('name', 'N/A')}")
            print(f"   Endereço: {place.get('formatted_address', 'N/A')}")
            print(f"   Rating: {place.get('rating', 'N/A')}")
            print()
        
        # Exportar para Excel
        output_file = finder.search_and_export(query, max_results=max_results)
        if output_file:
            print(f"✅ Dados exportados para: {output_file}")
    
    else:
        print("❌ Nenhum restaurante encontrado")

def main():
    """Função principal do exemplo"""
    print("=" * 60)
    print("EXEMPLOS DE USO - GOOGLE BUSINESS FINDER")
    print("=" * 60)
    
    print("\nEste script demonstra diferentes formas de usar a aplicação.")
    print("Certifique-se de configurar a variável GOOGLE_PLACES_API_KEY primeiro.\n")
    
    # Verificar se a API key está configurada
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        print("❌ Variável GOOGLE_PLACES_API_KEY não configurada!")
        print("\nPara configurar:")
        print("Windows (PowerShell): $env:GOOGLE_PLACES_API_KEY = 'sua_chave'")
        print("Windows (CMD): set GOOGLE_PLACES_API_KEY=sua_chave")
        print("Linux/Mac: export GOOGLE_PLACES_API_KEY=sua_chave")
        return
    
    print(f"✅ API Key configurada: {api_key[:10]}...")
    
    # Executar exemplos
    try:
        exemplo_busca_simples()
        exemplo_busca_localizada()
        exemplo_busca_restaurantes()
        
        print("\n" + "=" * 60)
        print("🎉 TODOS OS EXEMPLOS EXECUTADOS COM SUCESSO!")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ Erro durante a execução: {e}")
        print("Verifique se a API key está correta e se a Places API está ativada.")

if __name__ == "__main__":
    main()
