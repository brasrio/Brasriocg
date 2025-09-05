#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de teste para verificar a conexão com a Google Places API
"""

import os
import requests
import json

def test_api_connection():
    """Testa a conexão com a Google Places API"""
    print("🔍 Testando conexão com Google Places API...")
    
    # Verificar se a API key está configurada
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        print("❌ Variável GOOGLE_PLACES_API_KEY não configurada!")
        print("\nPara configurar:")
        print("Windows (PowerShell): $env:GOOGLE_PLACES_API_KEY = 'sua_chave'")
        print("Windows (CMD): set GOOGLE_PLACES_API_KEY=sua_chave")
        print("Linux/Mac: export GOOGLE_PLACES_API_KEY=sua_chave")
        return False
    
    print(f"✅ API Key encontrada: {api_key[:10]}...")
    
    # Testar busca simples
    test_url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
    params = {
        'query': 'padaria',
        'key': api_key,
        'language': 'pt-BR',
        'region': 'br'
    }
    
    try:
        print("\n📡 Fazendo requisição de teste...")
        response = requests.get(test_url, params=params, timeout=10)
        
        print(f"Status HTTP: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            status = data.get('status')
            
            print(f"Status da API: {status}")
            
            if status == 'OK':
                results = data.get('results', [])
                print(f"✅ Conexão bem-sucedida! Encontrados {len(results)} resultados de teste")
                
                if results:
                    first_result = results[0]
                    print(f"\n📋 Primeiro resultado:")
                    print(f"   Nome: {first_result.get('name', 'N/A')}")
                    print(f"   Endereço: {first_result.get('formatted_address', 'N/A')}")
                    print(f"   Place ID: {first_result.get('place_id', 'N/A')}")
                
                return True
                
            elif status == 'REQUEST_DENIED':
                error_message = data.get('error_message', 'Sem mensagem de erro')
                print(f"❌ Acesso negado: {error_message}")
                print("\nPossíveis causas:")
                print("- Places API não está ativada no projeto")
                print("- API key inválida ou expirada")
                print("- Restrições de domínio/IP na API key")
                return False
                
            elif status == 'OVER_QUERY_LIMIT':
                print("❌ Limite de consultas excedido")
                return False
                
            elif status == 'INVALID_REQUEST':
                print("❌ Requisição inválida")
                return False
                
            else:
                print(f"❌ Status inesperado: {status}")
                return False
                
        else:
            print(f"❌ Erro HTTP: {response.status_code}")
            print(f"Resposta: {response.text}")
            return False
            
    except requests.exceptions.Timeout:
        print("❌ Timeout na requisição")
        return False
        
    except requests.exceptions.RequestException as e:
        print(f"❌ Erro na requisição: {e}")
        return False
        
    except json.JSONDecodeError:
        print("❌ Erro ao decodificar resposta JSON")
        return False
        
    except Exception as e:
        print(f"❌ Erro inesperado: {e}")
        return False

def test_place_details():
    """Testa a busca de detalhes de um lugar específico"""
    print("\n🔍 Testando busca de detalhes...")
    
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    if not api_key:
        return False
    
    # Primeiro buscar um lugar
    search_url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
    search_params = {
        'query': 'padaria',
        'key': api_key,
        'language': 'pt-BR',
        'region': 'br'
    }
    
    try:
        response = requests.get(search_url, params=search_params, timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('status') == 'OK' and data.get('results'):
                place_id = data['results'][0]['place_id']
                
                # Buscar detalhes
                details_url = "https://maps.googleapis.com/maps/api/place/details/json"
                details_params = {
                    'place_id': place_id,
                    'key': api_key,
                    'language': 'pt-BR',
                    'fields': 'name,formatted_phone_number,website,formatted_address'
                }
                
                details_response = requests.get(details_url, params=details_params, timeout=10)
                if details_response.status_code == 200:
                    details_data = details_response.json()
                    if details_data.get('status') == 'OK':
                        result = details_data.get('result', {})
                        print(f"✅ Detalhes obtidos com sucesso!")
                        print(f"   Nome: {result.get('name', 'N/A')}")
                        print(f"   Telefone: {result.get('formatted_phone_number', 'N/A')}")
                        print(f"   Website: {result.get('website', 'N/A')}")
                        return True
                    else:
                        print(f"❌ Erro ao buscar detalhes: {details_data.get('status')}")
                        return False
                else:
                    print(f"❌ Erro HTTP ao buscar detalhes: {details_response.status_code}")
                    return False
            else:
                print("❌ Nenhum resultado encontrado para buscar detalhes")
                return False
        else:
            print(f"❌ Erro HTTP na busca: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ Erro ao testar detalhes: {e}")
        return False

def main():
    """Função principal do teste"""
    print("=" * 60)
    print("TESTE DE CONEXÃO - GOOGLE PLACES API")
    print("=" * 60)
    
    # Testar conexão básica
    if test_api_connection():
        print("\n✅ Teste de conexão básica: PASSOU")
        
        # Testar busca de detalhes
        if test_place_details():
            print("✅ Teste de detalhes: PASSOU")
            print("\n🎉 TODOS OS TESTES PASSARAM!")
            print("A API está funcionando corretamente.")
            print("\nVocê pode agora executar:")
            print("python search.py")
        else:
            print("❌ Teste de detalhes: FALHOU")
    else:
        print("\n❌ Teste de conexão básica: FALHOU")
    
    print("\n" + "=" * 60)
    print("📚 Para obter ajuda:")
    print("- Leia o README.md")
    print("- Execute: python setup.py")
    print("- Verifique a documentação da Google Places API")

if __name__ == "__main__":
    main()
