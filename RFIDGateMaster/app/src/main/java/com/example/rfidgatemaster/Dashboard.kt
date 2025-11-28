package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔥 Recibimos el rol enviado desde el login
        val rol = intent.getStringExtra("rol") ?: "OPERADOR"

        // Botones del dashboard
        val btnGestionUsuarios = findViewById<Button>(R.id.btnUsuarios)
        val btnGestionSensores = findViewById<Button>(R.id.btnSensores)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnControlBarrera = findViewById<Button>(R.id.btnBarrera)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)

        // 🔐 Si el rol es OPERADOR → ocultar botones restringidos
        if (rol == "OPERADOR") {
            btnGestionUsuarios.visibility = View.GONE
            btnGestionSensores.visibility = View.GONE
        }

        // Navegación normal
        btnGestionUsuarios.setOnClickListener {
            val intent = Intent(this, GestionUsuarios::class.java)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        btnGestionSensores.setOnClickListener {
            val intent = Intent(this, GestionSensores::class.java)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        btnHistorial.setOnClickListener {
            val intent = Intent(this, Historial::class.java)
            startActivity(intent)
        }

        btnControlBarrera.setOnClickListener {
            val intent = Intent(this, ControlBarrera::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            intent.putExtra("rol", rol)
            intent.putExtra("email", getIntent().getStringExtra("email"))
            startActivity(intent)
        }
    }
}
