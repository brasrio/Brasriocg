package com.example.brasrio

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.regex.Pattern

object Validators {
    
    // Validador de CPF
    fun isValidCPF(cpf: String): Boolean {
        val cleanCPF = cpf.replace(Regex("[^\\d]"), "")
        
        if (cleanCPF.length != 11) return false
        
        // Verifica se todos os dígitos são iguais
        if (cleanCPF.all { it == cleanCPF[0] }) return false
        
        // Validação do primeiro dígito verificador
        var sum = 0
        for (i in 0..8) {
            sum += (cleanCPF[i] - '0') * (10 - i)
        }
        var remainder = sum % 11
        val digit1 = if (remainder < 2) 0 else 11 - remainder
        
        if (cleanCPF[9] - '0' != digit1) return false
        
        // Validação do segundo dígito verificador
        sum = 0
        for (i in 0..9) {
            sum += (cleanCPF[i] - '0') * (11 - i)
        }
        remainder = sum % 11
        val digit2 = if (remainder < 2) 0 else 11 - remainder
        
        return cleanCPF[10] - '0' == digit2
    }
    
    // Validador de CNPJ
    fun isValidCNPJ(cnpj: String): Boolean {
        val cleanCNPJ = cnpj.replace(Regex("[^\\d]"), "")
        
        if (cleanCNPJ.length != 14) return false
        
        // Verifica se todos os dígitos são iguais
        if (cleanCNPJ.all { it == cleanCNPJ[0] }) return false
        
        // Validação do primeiro dígito verificador
        val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        var sum = 0
        for (i in 0..11) {
            sum += (cleanCNPJ[i] - '0') * weights1[i]
        }
        var remainder = sum % 11
        val digit1 = if (remainder < 2) 0 else 11 - remainder
        
        if (cleanCNPJ[12] - '0' != digit1) return false
        
        // Validação do segundo dígito verificador
        val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        sum = 0
        for (i in 0..12) {
            sum += (cleanCNPJ[i] - '0') * weights2[i]
        }
        remainder = sum % 11
        val digit2 = if (remainder < 2) 0 else 11 - remainder
        
        return cleanCNPJ[13] - '0' == digit2
    }
    
    // Validador de CPF ou CNPJ
    fun isValidCPForCNPJ(value: String): Boolean {
        val clean = value.replace(Regex("[^\\d]"), "")
        return when (clean.length) {
            11 -> isValidCPF(value)
            14 -> isValidCNPJ(value)
            else -> false
        }
    }
    
    // Formatar CPF
    fun formatCPF(cpf: String): String {
        val clean = cpf.replace(Regex("[^\\d]"), "")
        return when {
            clean.length <= 3 -> clean
            clean.length <= 6 -> "${clean.substring(0, 3)}.${clean.substring(3)}"
            clean.length <= 9 -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6)}"
            clean.length >= 11 -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6, 9)}-${clean.substring(9, minOf(11, clean.length))}"
            else -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6, 9)}-${clean.substring(9)}"
        }
    }
    
    // Formatar CNPJ
    fun formatCNPJ(cnpj: String): String {
        val clean = cnpj.replace(Regex("[^\\d]"), "")
        return when {
            clean.length <= 2 -> clean
            clean.length <= 5 -> "${clean.substring(0, 2)}.${clean.substring(2)}"
            clean.length <= 8 -> "${clean.substring(0, 2)}.${clean.substring(2, 5)}.${clean.substring(5)}"
            clean.length <= 12 -> "${clean.substring(0, 2)}.${clean.substring(2, 5)}.${clean.substring(5, 8)}/${clean.substring(8)}"
            clean.length >= 14 -> "${clean.substring(0, 2)}.${clean.substring(2, 5)}.${clean.substring(5, 8)}/${clean.substring(8, 12)}-${clean.substring(12, minOf(14, clean.length))}"
            else -> "${clean.substring(0, 2)}.${clean.substring(2, 5)}.${clean.substring(5, 8)}/${clean.substring(8, 12)}-${clean.substring(12)}"
        }
    }
    
    // Formatar telefone
    fun formatPhone(phone: String): String {
        val clean = phone.replace(Regex("[^\\d]"), "")
        return when {
            clean.length <= 2 -> clean
            clean.length <= 6 -> "(${clean.substring(0, 2)}) ${clean.substring(2)}"
            clean.length <= 10 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 6)}-${clean.substring(6)}"
            clean.length >= 11 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 7)}-${clean.substring(7, minOf(11, clean.length))}"
            else -> "(${clean.substring(0, 2)}) ${clean.substring(2, 7)}-${clean.substring(7)}"
        }
    }
    
    // Validador de nome (apenas letras e espaços)
    fun isValidName(name: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-ZÀ-ÿ\\s]+$")
        return pattern.matcher(name.trim()).matches() && name.trim().length >= 2
    }
    
    // Validador de telefone
    fun isValidPhone(phone: String): Boolean {
        val clean = phone.replace(Regex("[^\\d]"), "")
        return clean.length >= 10 && clean.length <= 11
    }
}

// TextWatcher para CPF/CNPJ - Versão simplificada e segura
class CPFCNPJTextWatcher(private val editText: EditText) : TextWatcher {
    private var isUpdating = false
    private var oldText = ""
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (!isUpdating) {
            oldText = s?.toString() ?: ""
        }
    }
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    
    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        
        val currentText = s?.toString() ?: ""
        val numbersOnly = currentText.replace(Regex("[^\\d]"), "")
        
        // Limitar a 14 dígitos
        val limitedNumbers = if (numbersOnly.length > 14) {
            numbersOnly.substring(0, 14)
        } else {
            numbersOnly
        }
        
        val formatted = when {
            limitedNumbers.length <= 11 -> formatCPFSafe(limitedNumbers)
            else -> formatCNPJSafe(limitedNumbers)
        }
        
        if (formatted != currentText) {
            isUpdating = true
            editText.setText(formatted)
            editText.setSelection(formatted.length)
            isUpdating = false
        }
    }
    
    private fun formatCPFSafe(numbers: String): String {
        return when {
            numbers.length <= 3 -> numbers
            numbers.length <= 6 -> "${numbers.substring(0, 3)}.${numbers.substring(3)}"
            numbers.length <= 9 -> "${numbers.substring(0, 3)}.${numbers.substring(3, 6)}.${numbers.substring(6)}"
            numbers.length == 10 -> "${numbers.substring(0, 3)}.${numbers.substring(3, 6)}.${numbers.substring(6, 9)}-${numbers.substring(9)}"
            numbers.length == 11 -> "${numbers.substring(0, 3)}.${numbers.substring(3, 6)}.${numbers.substring(6, 9)}-${numbers.substring(9, 11)}"
            else -> numbers
        }
    }
    
    private fun formatCNPJSafe(numbers: String): String {
        return when {
            numbers.length <= 2 -> numbers
            numbers.length <= 5 -> "${numbers.substring(0, 2)}.${numbers.substring(2)}"
            numbers.length <= 8 -> "${numbers.substring(0, 2)}.${numbers.substring(2, 5)}.${numbers.substring(5)}"
            numbers.length <= 12 -> "${numbers.substring(0, 2)}.${numbers.substring(2, 5)}.${numbers.substring(5, 8)}/${numbers.substring(8)}"
            numbers.length == 13 -> "${numbers.substring(0, 2)}.${numbers.substring(2, 5)}.${numbers.substring(5, 8)}/${numbers.substring(8, 12)}-${numbers.substring(12)}"
            numbers.length == 14 -> "${numbers.substring(0, 2)}.${numbers.substring(2, 5)}.${numbers.substring(5, 8)}/${numbers.substring(8, 12)}-${numbers.substring(12, 14)}"
            else -> numbers
        }
    }
}

// TextWatcher para telefone - Versão simplificada e segura
class PhoneTextWatcher(private val editText: EditText) : TextWatcher {
    private var isUpdating = false
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    
    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        
        val currentText = s?.toString() ?: ""
        val numbersOnly = currentText.replace(Regex("[^\\d]"), "")
        
        // Limitar a 11 dígitos
        val limitedNumbers = if (numbersOnly.length > 11) {
            numbersOnly.substring(0, 11)
        } else {
            numbersOnly
        }
        
        val formatted = formatPhoneSafe(limitedNumbers)
        
        if (formatted != currentText) {
            isUpdating = true
            editText.setText(formatted)
            editText.setSelection(formatted.length)
            isUpdating = false
        }
    }
    
    private fun formatPhoneSafe(numbers: String): String {
        return when {
            numbers.length <= 2 -> numbers
            numbers.length <= 6 -> "(${numbers.substring(0, 2)}) ${numbers.substring(2)}"
            numbers.length <= 10 -> "(${numbers.substring(0, 2)}) ${numbers.substring(2, 6)}-${numbers.substring(6)}"
            numbers.length == 11 -> "(${numbers.substring(0, 2)}) ${numbers.substring(2, 7)}-${numbers.substring(7, 11)}"
            else -> numbers
        }
    }
}

// TextWatcher para nome (apenas letras) - Versão simplificada e segura
class NameTextWatcher(private val editText: EditText) : TextWatcher {
    private var isUpdating = false
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    
    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        
        val currentText = s?.toString() ?: ""
        val filtered = currentText.replace(Regex("[^a-zA-ZÀ-ÿ\\s]"), "")
        
        if (filtered != currentText) {
            isUpdating = true
            editText.setText(filtered)
            editText.setSelection(filtered.length)
            isUpdating = false
        }
    }
}
