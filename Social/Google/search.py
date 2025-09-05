#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Google Business Finder - Buscador de Empresas usando Google Places API
Busca informações de empresas por nome ou categoria e exporta para Excel
"""

import os
import json
import requests
import pandas as pd
from typing import List, Dict, Optional
import time
from datetime import datetime
import logging

# Configuração de logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('google_business_finder.log'),
        logging.StreamHandler()
    ]
)

class GoogleBusinessFinder:
    def __init__(self, api_key: str):
        """
        Inicializa o buscador com a chave da API do Google
        
        Args:
            api_key (str): Chave da API do Google Places
        """
        self.api_key = api_key
        self.base_url = "https://maps.googleapis.com/maps/api/place"
        self.session = requests.Session()
        
    def search_places(self, query: str, location: str = "Brasil", radius: int = 50000) -> List[Dict]:
        """
        Busca lugares baseado na query
        
        Args:
            query (str): Termo de busca (nome da empresa ou categoria)
            location (str): Localização para busca (padrão: Brasil)
            radius (int): Raio de busca em metros (padrão: 50km)
            
        Returns:
            List[Dict]: Lista de lugares encontrados
        """
        places = []
        
        try:
            # Primeiro, buscar lugares por texto
            text_search_url = f"{self.base_url}/textsearch/json"
            params = {
                'query': query,
                'key': self.api_key,
                'language': 'pt-BR',
                'region': 'br'
            }
            
            if location != "Brasil":
                params['location'] = location
                params['radius'] = radius
            
            logging.info(f"Buscando lugares para: {query}")
            response = self.session.get(text_search_url, params=params)
            response.raise_for_status()
            
            data = response.json()
            
            if data['status'] == 'OK':
                places.extend(data['results'])
                logging.info(f"Encontrados {len(data['results'])} lugares")
                
                # Buscar mais resultados se houver paginação
                while 'next_page_token' in data:
                    time.sleep(2)  # Aguardar token ficar válido
                    params['pagetoken'] = data['next_page_token']
                    response = self.session.get(text_search_url, params=params)
                    data = response.json()
                    if data['status'] == 'OK':
                        places.extend(data['results'])
                        logging.info(f"Total de lugares encontrados: {len(places)}")
            else:
                logging.warning(f"Status da API: {data['status']} - {data.get('error_message', '')}")
                
        except requests.exceptions.RequestException as e:
            logging.error(f"Erro na requisição: {e}")
        except Exception as e:
            logging.error(f"Erro inesperado: {e}")
            
        return places
    
    def get_place_details(self, place_id: str) -> Optional[Dict]:
        """
        Obtém detalhes completos de um lugar específico
        
        Args:
            place_id (str): ID do lugar
            
        Returns:
            Optional[Dict]: Detalhes do lugar ou None se erro
        """
        try:
            details_url = f"{self.base_url}/details/json"
            params = {
                'place_id': place_id,
                'key': self.api_key,
                'language': 'pt-BR',
                'fields': 'name,formatted_phone_number,website,formatted_address,rating,user_ratings_total,types,business_status'
            }
            
            response = self.session.get(details_url, params=params)
            response.raise_for_status()
            
            data = response.json()
            
            if data['status'] == 'OK':
                return data['result']
            else:
                logging.warning(f"Erro ao buscar detalhes: {data['status']}")
                return None
                
        except Exception as e:
            logging.error(f"Erro ao buscar detalhes do lugar {place_id}: {e}")
            return None
    
    def extract_contact_info(self, place: Dict, details: Optional[Dict] = None) -> Dict:
        """
        Extrai informações de contato de um lugar
        
        Args:
            place (Dict): Dados básicos do lugar
            details (Optional[Dict]): Detalhes completos do lugar
            
        Returns:
            Dict: Informações de contato organizadas
        """
        contact_info = {
            'nome': place.get('name', ''),
            'endereco': place.get('formatted_address', ''),
            'telefone': '',
            'website': '',
            'email': '',
            'rating': place.get('rating', ''),
            'total_avaliacoes': place.get('user_ratings_total', ''),
            'tipo_negocio': ', '.join(place.get('types', [])),
            'status_negocio': '',
            'coordenadas': f"{place.get('geometry', {}).get('location', {}).get('lat', '')}, {place.get('geometry', {}).get('location', {}).get('lng', '')}"
        }
        
        # Se temos detalhes, extrair informações adicionais
        if details:
            contact_info['telefone'] = details.get('formatted_phone_number', '')
            contact_info['website'] = details.get('website', '')
            contact_info['status_negocio'] = details.get('business_status', '')
            
            # Tentar extrair email do website se disponível
            if details.get('website'):
                contact_info['email'] = self.extract_email_from_website(details['website'])
        
        return contact_info
    
    def extract_email_from_website(self, website: str) -> str:
        """
        Tenta extrair email do website (implementação básica)
        
        Args:
            website (str): URL do website
            
        Returns:
            str: Email encontrado ou string vazia
        """
        # Esta é uma implementação básica
        # Em produção, você pode implementar um crawler mais sofisticado
        # ou usar serviços especializados para extração de emails
        
        # Padrões comuns de email em websites
        email_patterns = [
            'contato@', 'info@', 'vendas@', 'atendimento@', 'comercial@'
        ]
        
        # Por simplicidade, retornamos um placeholder
        # Em uma implementação real, você faria uma requisição HTTP ao website
        # e procuraria por padrões de email
        return "Verificar no website"
    
    def search_and_export(self, query: str, output_file: str = None, max_results: int = 100) -> str:
        """
        Busca lugares e exporta para Excel
        
        Args:
            query (str): Termo de busca
            output_file (str): Nome do arquivo de saída
            max_results (int): Número máximo de resultados
            
        Returns:
            str: Caminho do arquivo exportado
        """
        if not output_file:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            output_file = f"empresas_{query.replace(' ', '_')}_{timestamp}.xlsx"
        
        logging.info(f"Iniciando busca para: {query}")
        
        # Buscar lugares
        places = self.search_places(query)
        
        if not places:
            logging.warning("Nenhum lugar encontrado")
            return ""
        
        # Limitar resultados
        places = places[:max_results]
        
        # Coletar informações de contato
        contact_data = []
        
        for i, place in enumerate(places):
            logging.info(f"Processando lugar {i+1}/{len(places)}: {place.get('name', '')}")
            
            # Buscar detalhes completos
            details = self.get_place_details(place['place_id'])
            
            # Extrair informações de contato
            contact_info = self.extract_contact_info(place, details)
            contact_data.append(contact_info)
            
            # Aguardar entre requisições para evitar rate limiting
            time.sleep(0.1)
        
        # Criar DataFrame e exportar
        df = pd.DataFrame(contact_data)
        
        # Reorganizar colunas
        column_order = [
            'nome', 'telefone', 'email', 'website', 'endereco', 
            'rating', 'total_avaliacoes', 'tipo_negocio', 'status_negocio', 'coordenadas'
        ]
        
        # Filtrar apenas colunas que existem
        existing_columns = [col for col in column_order if col in df.columns]
        df = df[existing_columns]
        
        # Exportar para Excel
        df.to_excel(output_file, index=False, sheet_name='Empresas')
        
        logging.info(f"Dados exportados para: {output_file}")
        logging.info(f"Total de empresas processadas: {len(contact_data)}")
        
        return output_file

def main():
    """Função principal da aplicação"""
    print("=" * 60)
    print("GOOGLE BUSINESS FINDER - Buscador de Empresas")
    print("=" * 60)
    
    # Verificar se a chave da API está disponível
    api_key = os.getenv('GOOGLE_PLACES_API_KEY')
    
    if not api_key:
        print("\n❌ ERRO: Chave da API do Google Places não encontrada!")
        print("Por favor, configure a variável de ambiente GOOGLE_PLACES_API_KEY")
        print("\nComo obter a chave:")
        print("1. Acesse: https://console.cloud.google.com/")
        print("2. Crie um projeto ou selecione um existente")
        print("3. Ative a API do Places")
        print("4. Crie credenciais (chave da API)")
        print("5. Configure a variável de ambiente:")
        print("   Windows: set GOOGLE_PLACES_API_KEY=sua_chave_aqui")
        print("   Linux/Mac: export GOOGLE_PLACES_API_KEY=sua_chave_aqui")
        return
    
    # Inicializar buscador
    finder = GoogleBusinessFinder(api_key)
    
    while True:
        print("\n" + "=" * 40)
        print("OPÇÕES DISPONÍVEIS:")
        print("1. Buscar empresas por nome/categoria")
        print("2. Buscar empresas em localização específica")
        print("3. Sair")
        print("=" * 40)
        
        choice = input("\nEscolha uma opção (1-3): ").strip()
        
        if choice == '1':
            query = input("\nDigite o nome da empresa ou categoria (ex: 'padaria', 'farmácia'): ").strip()
            if query:
                max_results = input("Número máximo de resultados (padrão: 50): ").strip()
                max_results = int(max_results) if max_results.isdigit() else 50
                
                output_file = finder.search_and_export(query, max_results=max_results)
                if output_file:
                    print(f"\n✅ Busca concluída! Arquivo salvo: {output_file}")
                else:
                    print("\n❌ Nenhum resultado encontrado ou erro na busca.")
        
        elif choice == '2':
            query = input("\nDigite o nome da empresa ou categoria: ").strip()
            location = input("Digite a localização (ex: 'São Paulo, SP'): ").strip()
            radius = input("Raio de busca em km (padrão: 50): ").strip()
            radius = int(radius) * 1000 if radius.isdigit() else 50000
            
            if query and location:
                max_results = input("Número máximo de resultados (padrão: 50): ").strip()
                max_results = int(max_results) if max_results.isdigit() else 50
                
                output_file = finder.search_and_export(query, max_results=max_results)
                if output_file:
                    print(f"\n✅ Busca concluída! Arquivo salvo: {output_file}")
                else:
                    print("\n❌ Nenhum resultado encontrado ou erro na busca.")
        
        elif choice == '3':
            print("\n👋 Obrigado por usar o Google Business Finder!")
            break
        
        else:
            print("\n❌ Opção inválida! Por favor, escolha 1, 2 ou 3.")

if __name__ == "__main__":
    main()
