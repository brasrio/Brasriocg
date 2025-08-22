# Teste do Cálculo do Perfil F530

## 🔍 Problema Reportado
- **Entrada**: 10m²
- **Site JavaScript**: Resultado = 5 unidades
- **App Android**: Resultado = 16 unidades (incorreto)

## 📊 Análise Matemática

### **Cálculo Esperado:**
```kotlin
ceil(m2 / 2f).toInt()
```

### **Teste com 10m²:**
- `m2 = 10.0f`
- `10.0f / 2f = 5.0f`
- `ceil(5.0f) = 5.0f`
- `5.0f.toInt() = 5`

**Resultado esperado: 5 unidades**

## 🔍 Possíveis Causas

### **1. Valor de m2 incorreto**
- Verificar se o valor de `m2` está sendo lido corretamente do EditText
- Verificar se há conversão de string para float

### **2. Cálculo duplicado**
- Verificar se o Perfil F530 está sendo adicionado mais de uma vez
- Verificar se há múltiplas chamadas para `addMaterialByCode("366", ...)`

### **3. Problema na função addMaterialByCode**
- Verificar se a função está funcionando corretamente
- Verificar se há algum processamento adicional

## 🧪 Teste de Debug

### **Adicionar logs para debug:**
```kotlin
val m2 = m2Text.toFloatOrNull()
Log.d("DEBUG", "m2 = $m2")
val perfilF530 = ceil(m2 / 2f).toInt()
Log.d("DEBUG", "Perfil F530 = $perfilF530")
addMaterialByCode("366", perfilF530)
```

## 📋 Verificações Necessárias

1. **Verificar valor de m2**: Confirmar que está sendo lido como 10.0f
2. **Verificar cálculo**: Confirmar que `ceil(10/2) = 5`
3. **Verificar resultado final**: Confirmar que está sendo adicionado como 5 unidades
4. **Verificar duplicação**: Confirmar que não está sendo adicionado múltiplas vezes

## 🎯 Próximos Passos

1. Adicionar logs de debug no código
2. Testar com valores diferentes (5m², 15m², 20m²)
3. Verificar se o problema é específico para 10m² ou geral
4. Comparar com outros cálculos para ver se há padrão

---

**Status**: 🔍 Em investigação  
**Data**: $(date)
