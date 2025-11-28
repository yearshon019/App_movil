package com.example.rfidgatemaster

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val txtEmail = findViewById<TextView>(R.id.txtEmailPerfil)
        val txtRol = findViewById<TextView>(R.id.txtRolPerfil)

        txtEmail.text = "Email: " + intent.getStringExtra("email")
        txtRol.text = "Rol: " + intent.getStringExtra("rol")
    }
}
