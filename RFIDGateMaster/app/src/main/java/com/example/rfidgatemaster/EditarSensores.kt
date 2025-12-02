package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class EditarSensor : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api/"

    lateinit var txtCodigo: TextView
    lateinit var txtDepartamento: TextView
    lateinit var spinnerTipo: Spinner
    lateinit var spinnerEstado: Spinner

    lateinit var idSensor: String
    lateinit var codigoSensor: String

    lateinit var numero: String
    lateinit var torre: String
    lateinit var piso: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_sensores)

        // ------------------ RECIBIR DATOS -------------------
        idSensor = intent.getStringExtra("id_sensor")!!
        codigoSensor = intent.getStringExtra("codigo_sensor")!!
        numero = intent.getStringExtra("numero") ?: "N/A"
        torre = intent.getStringExtra("torre") ?: "N/A"
        piso = intent.getStringExtra("piso") ?: "N/A"

        // ------------------ VISTAS -------------------
        txtCodigo = findViewById(R.id.txtCodigoSensorEditar)
        txtDepartamento = findViewById(R.id.txtDepartamentoSensorEditar)
        spinnerTipo = findViewById(R.id.spinnerTipoSensorEditar)
        spinnerEstado = findViewById(R.id.spinnerEstadoSensorEditar)

        txtCodigo.text = "Código Sensor: $codigoSensor"
        txtDepartamento.text = "Departamento: $numero | Torre: $torre | Piso: $piso"

        // ------------------ SPINNERS -------------------
        val tipos = arrayOf("TARJETA", "LLAVERO")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
        spinnerTipo.setSelection(tipos.indexOf(intent.getStringExtra("tipo")))

        val estados = arrayOf("ACTIVO", "INACTIVO", "PERDIDO", "BLOQUEADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spinnerEstado.setSelection(estados.indexOf(intent.getStringExtra("estado")))

        // ------------------ BOTONES -------------------
        findViewById<Button>(R.id.btnGuardarSensor).setOnClickListener { guardarCambios() }
        findViewById<Button>(R.id.btnEliminarSensor).setOnClickListener { eliminarSensor() }
    }

    // ------------------------- GUARDAR -------------------------
    fun guardarCambios() {
        val url = BASE + "sensores_modificar.php"

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sensor actualizado")
                    .setContentText("Redirigiendo en 5 segundos...")
                    .show()

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, GestionSensores::class.java))
                    finish()
                }, 5000)

            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al actualizar")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf(
                    "id_sensor" to idSensor,
                    "tipo" to spinnerTipo.selectedItem.toString(),
                    "estado" to spinnerEstado.selectedItem.toString()
                )
        }

        Volley.newRequestQueue(this).add(peticion)
    }

    // ------------------------- ELIMINAR -------------------------
    fun eliminarSensor() {
        val url = BASE + "sensores_eliminar.php"

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sensor eliminado")
                    .setContentText("Redirigiendo en 5 segundos...")
                    .show()

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, GestionSensores::class.java))
                    finish()
                }, 5000)

            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al eliminar")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf("id_sensor" to idSensor)
        }

        Volley.newRequestQueue(this).add(peticion)
    }
}
