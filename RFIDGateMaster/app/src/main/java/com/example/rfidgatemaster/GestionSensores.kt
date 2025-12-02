package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class GestionSensores : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api/"

    lateinit var spinnerTipo: Spinner
    lateinit var spinnerEstado: Spinner
    lateinit var listaSensores: ListView

    var ultimoUID = ""   // UID escaneado

    // Adaptador visual
    lateinit var adaptadorSensores: ArrayAdapter<String>
    val datosSensores = ArrayList<String>()

    // Datos reales del sensor
    val ids = ArrayList<String>()
    val codigos = ArrayList<String>()
    val tipos = ArrayList<String>()
    val estados = ArrayList<String>()

    // NUEVOS → datos del departamento
    val numeros = ArrayList<String>()
    val torres = ArrayList<String>()
    val pisos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        listaSensores = findViewById(R.id.listaSensores)

        adaptadorSensores = ArrayAdapter(this, android.R.layout.simple_list_item_1, datosSensores)
        listaSensores.adapter = adaptadorSensores

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener { registrarSensor() }

        cargarSensores()

        // -----------------------------------------
        // CLICK → EDITAR SENSOR
        // -----------------------------------------
        listaSensores.setOnItemClickListener { _, _, position, _ ->

            if (position < 0 || position >= ids.size) return@setOnItemClickListener

            val intent = Intent(this, EditarSensor::class.java)

            intent.putExtra("id_sensor", ids[position])
            intent.putExtra("codigo_sensor", codigos[position])
            intent.putExtra("tipo", tipos[position])
            intent.putExtra("estado", estados[position])

            // NUEVO: enviar departamento
            intent.putExtra("numero", numeros[position])
            intent.putExtra("torre", torres[position])
            intent.putExtra("piso", pisos[position])

            startActivity(intent)
        }
    }

    // ----------------------------------------------------
    // 1) ESCANEAR TARJETA RFID
    // ----------------------------------------------------
    fun escanearTarjeta() {
        val url = BASE + "leer_uid.php"

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
                        .setContentText("UID: $uid")
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

    // ----------------------------------------------------
    // 2) REGISTRAR SENSOR
    // ----------------------------------------------------
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

        val url = BASE + "sensores_agregar.php"

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sensor Registrado")
                    .setContentText("UID $ultimoUID guardado correctamente.")
                    .show()

                ultimoUID = ""
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

    // ----------------------------------------------------
    // 3) LISTAR SENSORES + GUARDAR DATOS
    // ----------------------------------------------------
    fun cargarSensores() {
        val url = BASE + "sensores_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                datosSensores.clear()
                ids.clear()
                codigos.clear()
                tipos.clear()
                estados.clear()

                numeros.clear()
                torres.clear()
                pisos.clear()

                for (i in 0 until response.length()) {
                    val s = response.getJSONObject(i)

                    val id = s.optString("id_sensor", "")
                    val codigo = s.optString("codigo_sensor", "")
                    val tipo = s.optString("tipo", "N/A")
                    val estado = s.optString("estado", "N/A")

                    val numero = s.optString("numero", "N/A")
                    val torre = s.optString("torre", "N/A")
                    val piso = s.optString("piso", "N/A")

                    if (id.isBlank()) continue

                    ids.add(id)
                    codigos.add(codigo)
                    tipos.add(tipo)
                    estados.add(estado)

                    numeros.add(numero)
                    torres.add(torre)
                    pisos.add(piso)

                    val texto =
                        "Código: $codigo\n" +
                                "Tipo: $tipo\n" +
                                "Estado: $estado\n" +
                                "Depto: $numero | Torre: $torre"
                    datosSensores.add(texto)
                }

                if (datosSensores.isEmpty()) {
                    datosSensores.add("No hay sensores registrados.")
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
