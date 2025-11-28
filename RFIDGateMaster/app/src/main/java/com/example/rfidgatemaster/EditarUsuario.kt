package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest

class EditarUsuario : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api/"

    lateinit var edtNombre: EditText
    lateinit var edtEmail: EditText
    lateinit var edtTelefono: EditText
    lateinit var edtRut: EditText
    lateinit var spinnerEstado: Spinner
    lateinit var spinnerRol: Spinner
    lateinit var spinnerDepartamento: Spinner

    lateinit var idUsuario: String
    lateinit var idDeptOriginal: String

    val listaDepartamentos = ArrayList<String>()
    val idsDepartamentos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // ----------------- RECIBIR DATOS -----------------
        idUsuario = intent.getStringExtra("id_usuario")!!
        val nombre = intent.getStringExtra("nombre")!!
        val email = intent.getStringExtra("email")!!
        val telefono = intent.getStringExtra("telefono")!!
        val rut = intent.getStringExtra("rut")!!
        val estado = intent.getStringExtra("estado")!!
        val rol = intent.getStringExtra("rol")!!
        idDeptOriginal = intent.getStringExtra("id_departamento")!!

        // ----------------- VISTAS -----------------
        edtNombre = findViewById(R.id.edtNombreEditar)
        edtEmail = findViewById(R.id.edtEmailEditar)
        edtTelefono = findViewById(R.id.edtTelefonoEditar)
        edtRut = findViewById(R.id.edtRutEditar)
        spinnerEstado = findViewById(R.id.spinnerEstadoEditar)
        spinnerRol = findViewById(R.id.spinnerRolEditar)
        spinnerDepartamento = findViewById(R.id.spinnerDepartamentoEditar)

        edtNombre.setText(nombre)
        edtEmail.setText(email)
        edtTelefono.setText(telefono)
        edtRut.setText(rut)

        // ESTADOS
        val estados = arrayOf("ACTIVO", "INACTIVO", "BLOQUEADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spinnerEstado.setSelection(estados.indexOf(estado))

        // ROLES
        val roles = arrayOf("ADMIN", "OPERADOR")
        spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerRol.setSelection(roles.indexOf(rol))

        // CARGAR DEPARTAMENTOS
        cargarDepartamentos()

        // BOTONES
        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener { guardarCambios() }
        findViewById<Button>(R.id.btnEliminarUsuario).setOnClickListener { eliminarUsuario() }
    }

    fun cargarDepartamentos() {
        val url = "$BASE/departamentos_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                listaDepartamentos.clear()
                idsDepartamentos.clear()

                for (i in 0 until response.length()) {
                    val d = response.getJSONObject(i)

                    idsDepartamentos.add(d.getString("id_departamento"))
                    listaDepartamentos.add("Depto ${d.getString("numero")} - Torre ${d.getString("torre")}")
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaDepartamentos)
                spinnerDepartamento.adapter = adapter

                // Seleccionar el departamento original
                val posicion = idsDepartamentos.indexOf(idDeptOriginal)
                if (posicion >= 0) spinnerDepartamento.setSelection(posicion)
            },
            { }
        )

        Volley.newRequestQueue(this).add(peticion)
    }

    fun guardarCambios() {
        val url = "$BASE/usuarios_modificar.php"

        val idDeptCorrecto = idsDepartamentos[spinnerDepartamento.selectedItemPosition]

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Usuario actualizado")
                    .setContentText("Redirigiendo en 5 segundos...")
                    .show()

                android.os.Handler().postDelayed({
                    startActivity(Intent(this, GestionUsuarios::class.java))
                    finish()
                }, 5000)

            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al actualizar")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id_usuario" to idUsuario,
                    "nombre" to edtNombre.text.toString(),
                    "email" to edtEmail.text.toString(),
                    "telefono" to edtTelefono.text.toString(),
                    "rut" to edtRut.text.toString(),
                    "estado" to spinnerEstado.selectedItem.toString(),
                    "rol" to spinnerRol.selectedItem.toString(),
                    "id_departamento" to idDeptCorrecto
                )
            }
        }

        Volley.newRequestQueue(this).add(peticion)
    }

    fun eliminarUsuario() {
        val url = "$BASE/usuarios_eliminar.php"

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Usuario eliminado")
                    .setContentText("Redirigiendo en 5 segundos...")
                    .show()

                android.os.Handler().postDelayed({
                    startActivity(Intent(this, GestionUsuarios::class.java))
                    finish()
                }, 5000)

            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al eliminar")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("id_usuario" to idUsuario)
            }
        }

        Volley.newRequestQueue(this).add(peticion)
    }
}