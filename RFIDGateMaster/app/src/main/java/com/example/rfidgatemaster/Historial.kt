package com.example.rfidgatemaster

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class Historial : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val txtHistorial = findViewById<TextView>(R.id.txtHistorial)

        val url = "$BASE/eventos_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { r ->

                var texto = "Historial de accesos:\n\n"

                for (i in 0 until r.length()) {
                    val e = r.getJSONObject(i)

                    texto += "Usuario: ${e.getString("nombre")}\n"
                    texto += "Sensor: ${e.getString("codigo_sensor")}\n"
                    texto += "Evento: ${e.getString("tipo_evento")}\n"
                    texto += "Fecha: ${e.getString("fecha_hora")}\n"
                    texto += "Resultado: ${e.getString("resultado")}\n"
                    texto += "---------------------------\n"
                }

                txtHistorial.text = texto
            },
            {
                txtHistorial.text = "Error de conexión."
            }
        )

        Volley.newRequestQueue(this).add(peticion)
    }
}
