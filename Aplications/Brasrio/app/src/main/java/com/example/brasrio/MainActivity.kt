package com.example.brasrio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Configurar botão de orçamento
        findViewById<Button>(R.id.btn_orcamento).setOnClickListener {
            startActivity(Intent(this, OrcamentoActivity::class.java))
        }
        
        // Configurar botão de instaladores
        findViewById<Button>(R.id.btn_instaladores).setOnClickListener {
            abrirInstaladores()
        }
        
        // Configurar botão de fretes
        findViewById<Button>(R.id.btn_fretes).setOnClickListener {
            abrirFretes()
        }
    }
    
    private fun abrirInstaladores() {
        try {
            val numeroWhatsApp = "5521971252304"
            val mensagem = "Olá! Gostaria de informações sobre *INSTALADORES CREDENCIADOS* da BRASRIO."
            val mensagemCodificada = Uri.encode(mensagem)
            
            val uri = Uri.parse("https://wa.me/$numeroWhatsApp?text=$mensagemCodificada")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun abrirFretes() {
        try {
            val numeroWhatsApp = "5521971252304"
            val mensagem = "Olá! Gostaria de informações sobre *FRETES* da BRASRIO."
            val mensagemCodificada = Uri.encode(mensagem)
            
            val uri = Uri.parse("https://wa.me/$numeroWhatsApp?text=$mensagemCodificada")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}