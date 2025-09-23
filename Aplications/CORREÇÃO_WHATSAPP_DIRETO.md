# Correção do WhatsApp Direto - BRASRIO App

## Problema Identificado

**WhatsApp da empresa em vez de direto:** As telas de fretes e instaladores estavam redirecionando para o WhatsApp da empresa em vez de redirecionar diretamente para cada prestador de serviço, como no site.

## Análise do Site

### **fretes.html**
```javascript
function contatarWhatsApp(numero) {
  const mensagem = encodeURIComponent('Olá! Gostaria de solicitar um orçamento de frete para materiais de construção.');
  window.open(`https://wa.me/55${numero}?text=${mensagem}`, '_blank');
}
```

### **instaladores.html**
```javascript
function contatarWhatsApp(numero) {
  const mensagem = encodeURIComponent('Olá! Gostaria de contratar seus serviços de instalação de PVC e Drywall.');
  window.open(`https://wa.me/55${numero}?text=${mensagem}`, '_blank');
}
```

## Solução Implementada

### 1. **FretesActivity Atualizada**

#### **Antes:**
- Redirecionava para WhatsApp da empresa
- Mensagem genérica da empresa

#### **Depois:**
```kotlin
fun contatarFreteWhatsApp(numeroWhatsApp: String) {
    val mensagem = "Olá! Gostaria de solicitar um orçamento de frete para materiais de construção."
    val url = "https://wa.me/55$numeroWhatsApp?text=${Uri.encode(mensagem)}"
    
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}
```

### 2. **InstaladoresActivity Atualizada**

#### **Antes:**
- Redirecionava para WhatsApp da empresa
- Mensagem genérica da empresa

#### **Depois:**
```kotlin
fun contatarInstaladorWhatsApp(numeroWhatsApp: String) {
    val mensagem = "Olá! Gostaria de contratar seus serviços de instalação de PVC e Drywall."
    val url = "https://wa.me/55$numeroWhatsApp?text=${Uri.encode(mensagem)}"
    
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}
```

### 3. **Mensagens Específicas**

#### **Para Fretes:**
- **Mensagem:** "Olá! Gostaria de solicitar um orçamento de frete para materiais de construção."
- **Redirecionamento:** Direto para o WhatsApp do frete específico

#### **Para Instaladores:**
- **Mensagem:** "Olá! Gostaria de contratar seus serviços de instalação de PVC e Drywall."
- **Redirecionamento:** Direto para o WhatsApp do instalador específico

## Funcionalidades Implementadas

### **Fretes (3 disponíveis):**
- **Rafael Ribeiro:** WhatsApp direto (21970397474)
- **Luis Carlos:** WhatsApp direto (21964836357)
- **Bira:** WhatsApp direto (21999398855)

### **Instaladores (6 credenciados):**
- **Marcio M&T Decor:** WhatsApp direto (21981442542)
- **Hygino's Gesso:** WhatsApp direto (21990634358)
- **JG Construtora:** WhatsApp direto (21999773930)
- **Márcio Miranda:** WhatsApp direto (21984175074)
- **Alexandre Drywall:** WhatsApp direto (21978627781)
- **LB SERVIÇOS:** WhatsApp direto (21964936389)

## Vantagens da Nova Abordagem

1. **Contato Direto:** Cliente fala diretamente com o prestador
2. **Mensagens Específicas:** Cada tipo de serviço tem mensagem adequada
3. **Experiência Melhor:** Igual ao site, sem intermediários
4. **Eficiência:** Reduz tempo de resposta

## Status

✅ **FretesActivity atualizada**
✅ **InstaladoresActivity atualizada**
✅ **WhatsApp direto implementado**
✅ **Mensagens específicas criadas**
✅ **Funcionamento igual ao site**

Agora as telas de fretes e instaladores redirecionam diretamente para o WhatsApp de cada prestador, exatamente como no site.
