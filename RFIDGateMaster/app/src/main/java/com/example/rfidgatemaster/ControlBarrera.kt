package com.example.rfidgatemaster

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ControlBarrera : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_barrera)

        val txtEstado = findViewById<TextView>(R.id.txtEstadoBarrera)
        val btnAbrir = findViewById<Button>(R.id.btnAbrir)
        val btnCerrar = findViewById<Button>(R.id.btnCerrar)

        btnAbrir.setOnClickListener {
            val url = "$BASE/barrera_abrir.php"
            val req = StringRequest(Request.Method.GET, url,
                {
                    txtEstado.text = "Estado de la Barrera: Abierta"
                },
                {
                    txtEstado.text = "Error"
                })
            Volley.newRequestQueue(this).add(req)
        }

        btnCerrar.setOnClickListener {
            val url = "$BASE/barrera_cerrar.php"
            val req = StringRequest(Request.Method.GET, url,
                {
                    txtEstado.text = "Estado de la Barrera: Cerrada"
                },
                {
                    txtEstado.text = "Error"
                })
            Volley.newRequestQueue(this).add(req)
        }
    }
}
