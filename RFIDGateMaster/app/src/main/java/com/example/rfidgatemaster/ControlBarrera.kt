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

    private val BASE = "http://100.106.124.81/rfid_api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_barrera)

        val txtEstado = findViewById<TextView>(R.id.txtEstadoBarrera)
        val btnAbrir = findViewById<Button>(R.id.btnAbrir)
        val btnCerrar = findViewById<Button>(R.id.btnCerrar)

        // Leer usuario que inició sesión
        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        val idUsuario = prefs.getString("id_usuario", "") ?: ""

        // ---------------------------------------------------------
        //                 ABRIR BARRERA (POST)
        // ---------------------------------------------------------
        btnAbrir.setOnClickListener {

            val url = BASE + "barrera_abrir.php"

            val req = object : StringRequest(
                Method.POST,
                url,
                { response ->

                    txtEstado.text = "Estado de la Barrera: Abierta"

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Barrera Abierta")
                        .setContentText("El portón ha sido abierto exitosamente.")
                        .show()
                },
                {
                    txtEstado.text = "Error al abrir"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No se pudo abrir la barrera.")
                        .show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "id_usuario" to idUsuario
                    )
                }
            }

            Volley.newRequestQueue(this).add(req)
        }

        // ---------------------------------------------------------
        //                 CERRAR BARRERA (POST)
        // ---------------------------------------------------------
        btnCerrar.setOnClickListener {

            val url = BASE + "barrera_cerrar.php"

            val req = object : StringRequest(
                Method.POST,
                url,
                { response ->

                    txtEstado.text = "Estado de la Barrera: Cerrada"

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Barrera Cerrada")
                        .setContentText("El portón ha sido cerrado exitosamente.")
                        .show()
                },
                {
                    txtEstado.text = "Error al cerrar"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No se pudo cerrar la barrera.")
                        .show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "id_usuario" to idUsuario
                    )
                }
            }

            Volley.newRequestQueue(this).add(req)
        }
    }
}
