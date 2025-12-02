package com.example.rfidgatemaster

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class GestionSensores : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api"
    lateinit var spinnerTipo: Spinner
    lateinit var spinnerEstado: Spinner
    lateinit var listaSensores: ListView

    var ultimoUID = ""   // ← Aquí guardamos lo que se escanea

    lateinit var adaptadorSensores: ArrayAdapter<String>
    val datosSensores = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        listaSensores = findViewById(R.id.listaSensores)

        adaptadorSensores =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, datosSensores)
        listaSensores.adapter = adaptadorSensores

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener { registrarSensor() }

        cargarSensores()
    }

    // ------------------------------------------------------------------
    // 1) ESCANEAR TARJETA RFID → LEE leer_uid.php
    // ------------------------------------------------------------------
    fun escanearTarjeta() {
        val url = "$BASE/leer_uid.php"

        val peticion = StringRequest(
            Request.Method.GET, url,
            { uid ->
                if (uid.isBlank()) {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("No detectado")
                        .setContentText("Aún no se ha escaneado ninguna tarjeta RFID.")
                        .show()
                } else {
                    ultimoUID = uid

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Tarjeta detectada")
                        .setContentText("UID: $uid\n¿Deseas registrarla?")
                        .show()
                }
            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No se pudo conectar con el servidor.")
                    .show()
            }
        )

        Volley.newRequestQueue(this).add(peticion)
    }

    // ------------------------------------------------------------------
    // 2) REGISTRAR SENSOR CON EL UID ESCANEADO
    // ------------------------------------------------------------------
    fun registrarSensor() {

        if (ultimoUID.isBlank()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Escanee primero")
                .setContentText("Primero debes escanear una tarjeta RFID.")
                .show()
            return
        }

        val tipo = spinnerTipo.selectedItem.toString()
        val estado = spinnerEstado.selectedItem.toString()

        val url = "$BASE/sensores_agregar.php"

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sensor Registrado")
                    .setContentText("UID $ultimoUID guardado correctamente.")
                    .show()

                ultimoUID = ""   // limpia el UID
                cargarSensores()
            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error registrando")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "codigo_sensor" to ultimoUID,
                    "tipo" to tipo,
                    "estado" to estado,
                    "id_departamento" to "1"
                )
            }
        }

        Volley.newRequestQueue(this).add(peticion)
    }

    // ------------------------------------------------------------------
    // 3) LISTAR SENSORES
    // ------------------------------------------------------------------
    fun cargarSensores() {
        val url = "$BASE/sensores_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                datosSensores.clear()

                for (i in 0 until response.length()) {
                    val s = response.getJSONObject(i)
                    val texto =
                        "Código: ${s.getString("codigo_sensor")}\n" +
                                "Tipo: ${s.getString("tipo")}\n" +
                                "Estado: ${s.getString("estado")}"

                    datosSensores.add(texto)
                }

                adaptadorSensores.notifyDataSetChanged()
            },
            {
                datosSensores.clear()
                datosSensores.add("Error al cargar sensores.")
                adaptadorSensores.notifyDataSetChanged()
            }
        )

        Volley.newRequestQueue(this).add(peticion)
    }
}
