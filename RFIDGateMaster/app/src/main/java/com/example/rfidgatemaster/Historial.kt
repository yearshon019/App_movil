package com.example.rfidgatemaster

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class Historial : AppCompatActivity() {

    private val BASE = "http://100.106.124.81/rfid_api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val txtHistorial = findViewById<TextView>(R.id.txtHistorial)

        val prefs = getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val idUsuario = prefs.getString("id_usuario", "") ?: ""
        val rol = prefs.getString("rol", "") ?: ""

        val url = BASE + "eventos_listar.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->

                val arr = JSONArray(response)

                if (arr.length() == 0) {
                    txtHistorial.text = "No hay historial disponible."
                } else {

                    val sb = StringBuilder()
                    sb.append("📜 Historial de Accesos\n\n")

                    for (i in 0 until arr.length()) {

                        val e = arr.getJSONObject(i)

                        val usuario = e.optString("nombre", "Desconocido")
                        val tipoEvento = e.optString("tipo_evento", "N/A")
                        val fecha = e.optString("fecha_hora", "Sin fecha")
                        val resultado = e.optString("resultado", "N/A")

                        // ------------------------------
                        // EVENTOS MANUALES (USAN JSON departamento)
                        // ------------------------------
                        if (tipoEvento == "APERTURA_MANUAL" || tipoEvento == "CIERRE_MANUAL") {

                            val depObj = e.optJSONObject("departamento")

                            val numero = depObj?.optString("numero", "N/A") ?: "N/A"
                            val torre = depObj?.optString("torre", "N/A") ?: "N/A"
                            val piso  = depObj?.optString("piso", "N/A") ?: "N/A"

                            sb.append(
                                """
                                👤 Usuario: $usuario
                                🛠 Evento Manual
                                🏢 Departamento: $numero | Torre: $torre | Piso: $piso
                                🎯 Acción: $tipoEvento
                                📌 Resultado: $resultado
                                ⏱ Fecha: $fecha
                                ------------------------------------
                                
                                """.trimIndent()
                            ).append("\n")

                        } else {

                            // ------------------------------
                            // EVENTOS CON TARJETA / SENSOR
                            // ------------------------------
                            val numero = e.optString("numero", "N/A")
                            val torre = e.optString("torre", "N/A")
                            val piso = e.optString("piso", "N/A")
                            val sensor = e.optString("codigo_sensor", "N/A")

                            sb.append(
                                """
                                👤 Usuario: $usuario
                                🏢 Departamento: $numero | Torre: $torre | Piso: $piso
                                🔑 Sensor: $sensor
                                🎯 Evento: $tipoEvento
                                📌 Resultado: $resultado
                                ⏱ Fecha: $fecha
                                ------------------------------------
                                
                                """.trimIndent()
                            ).append("\n")
                        }
                    }

                    txtHistorial.text = sb.toString()
                }
            },
            { error ->
                txtHistorial.text = "❌ Error cargando historial."
                Log.e("API", "Error: $error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id_usuario" to idUsuario,
                    "rol" to rol
                )
            }
        }

        request.retryPolicy = DefaultRetryPolicy(8000, 2, 1f)
        Volley.newRequestQueue(this).add(request)
    }
}
