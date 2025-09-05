#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de instalação e configuração para o Google Business Finder
"""

import os
import sys
import subprocess
import platform

def install_requirements():
    """Instala as dependências necessárias"""
    print("📦 Instalando dependências...")
    
    try:
        subprocess.check_call([sys.executable, "-m", "pip", "install", "-r", "requirements.txt"])
        print("✅ Dependências instaladas com sucesso!")
        return True
    except subprocess.CalledProcessError as e:
        print(f"❌ Erro ao instalar dependências: {e}")
        return False

def create_env_file():
    """Cria arquivo .env com configurações"""
    print("\n🔧 Configurando variáveis de ambiente...")
    
    api_key = input("Digite sua chave da API do Google Places: ").strip()
    
    if not api_key:
        print("❌ Chave da API é obrigatória!")
        return False
    
    env_content = f"""# Google Places API Key
GOOGLE_PLACES_API_KEY={api_key}

# Configurações opcionais
MAX_RESULTS=100
DEFAULT_RADIUS_KM=50
LANGUAGE=pt-BR
REGION=br
"""
    
    try:
        with open('.env', 'w', encoding='utf-8') as f:
            f.write(env_content)
        print("✅ Arquivo .env criado com sucesso!")
        return True
    except Exception as e:
        print(f"❌ Erro ao criar arquivo .env: {e}")
        return False

def setup_windows():
    """Configuração específica para Windows"""
    print("\n🪟 Configurando para Windows...")
    
    # Criar variável de ambiente temporária
    api_key = input("Digite sua chave da API do Google Places: ").strip()
    
    if not api_key:
        print("❌ Chave da API é obrigatória!")
        return False
    
    # Comando para definir variável de ambiente
    cmd = f'set GOOGLE_PLACES_API_KEY={api_key}'
    print(f"\nPara definir a variável de ambiente, execute no PowerShell:")
    print(f"$env:GOOGLE_PLACES_API_KEY = '{api_key}'")
    print(f"\nOu no CMD:")
    print(f"set GOOGLE_PLACES_API_KEY={api_key}")
    
    return True

def main():
    """Função principal de configuração"""
    print("=" * 60)
    print("GOOGLE BUSINESS FINDER - CONFIGURAÇÃO")
    print("=" * 60)
    
    # Verificar se Python está instalado
    if sys.version_info < (3, 7):
        print("❌ Python 3.7 ou superior é necessário!")
        return
    
    print(f"✅ Python {sys.version_info.major}.{sys.version_info.minor} detectado")
    
    # Instalar dependências
    if not install_requirements():
        return
    
    # Configurar variáveis de ambiente
    if platform.system() == "Windows":
        setup_windows()
    else:
        create_env_file()
    
    print("\n" + "=" * 60)
    print("🎉 CONFIGURAÇÃO CONCLUÍDA!")
    print("=" * 60)
    print("\nPara usar a aplicação:")
    
    if platform.system() == "Windows":
        print("1. Defina a variável de ambiente GOOGLE_PLACES_API_KEY")
        print("2. Execute: python google_business_finder.py")
    else:
        print("1. Execute: python google_business_finder.py")
    
    print("\n📚 Documentação:")
    print("- Google Places API: https://developers.google.com/maps/documentation/places/web-service")
    print("- Console Google Cloud: https://console.cloud.google.com/")

if __name__ == "__main__":
    main()
