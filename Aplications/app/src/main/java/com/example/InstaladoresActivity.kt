package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InstaladoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instaladores)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        // Views serão inicializadas no setupClickListeners
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
        
        findViewById<Button>(R.id.btnContatarWhatsApp).setOnClickListener {
            contatarWhatsApp()
        }
    }

    private fun contatarWhatsApp() {
        val mensagem = "*SOLICITAÇÃO DE INSTALADORES CREDENCIADOS - BRASRIO*\n\n" +
                "Olá! Gostaria de solicitar indicação de instaladores credenciados para minha obra.\n\n" +
                "Por favor, entre em contato para mais informações."

        val numeroWhatsApp = "5521971252304"
        val url = "https://wa.me/$numeroWhatsApp?text=${Uri.encode(mensagem)}"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
