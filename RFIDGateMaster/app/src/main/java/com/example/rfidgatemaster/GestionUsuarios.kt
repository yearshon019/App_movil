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
import org.json.JSONObject

class GestionUsuarios : AppCompatActivity() {

    val BASE = "http://100.106.124.81/rfid_api/"

    lateinit var listaUsuarios: ListView
    lateinit var adaptadorUsuarios: ArrayAdapter<String>
    val datosUsuarios = ArrayList<String>()
    val ultimoJson = ArrayList<JSONObject>()   // Guarda los datos reales del usuario

    // Spinners
    lateinit var spinnerEstado: Spinner
    lateinit var spinnerRol: Spinner
    lateinit var spinnerDepartamento: Spinner

    // Departamentos
    val listaDepartamentos = ArrayList<String>()
    val idsDepartamentos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        // LISTA
        listaUsuarios = findViewById(R.id.listaUsuarios)
        adaptadorUsuarios = ArrayAdapter(this, android.R.layout.simple_list_item_1, datosUsuarios)
        listaUsuarios.adapter = adaptadorUsuarios

        // SPINNERS
        spinnerEstado = findViewById(R.id.spinnerEstadoUsuario)
        spinnerRol = findViewById(R.id.spinnerRolUsuario)
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento)

        // ESTADO
        val estados = arrayOf("ACTIVO", "INACTIVO", "BLOQUEADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        // ROL
        val roles = arrayOf("ADMIN", "OPERADOR")
        spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        // Cargar departamentos desde API
        cargarDepartamentos()

        // BOTÓN AGREGAR USUARIO
        findViewById<Button>(R.id.btnAgregarUsuario).setOnClickListener {
            registrarUsuario()
        }

        // CLICK NORMAL → Editar usuario
        listaUsuarios.setOnItemClickListener { parent, view, position, id ->
            abrirEditarUsuario(position)
        }

        cargarUsuarios()
    }

    // ============================================================
    // CARGAR DEPARTAMENTOS
    // ============================================================
    fun cargarDepartamentos() {
        val url = "$BASE/departamentos_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                listaDepartamentos.clear()
                idsDepartamentos.clear()

                for (i in 0 until response.length()) {
                    val d = response.getJSONObject(i)
                    listaDepartamentos.add("Depto ${d.getString("numero")} - Torre ${d.getString("torre")}")
                    idsDepartamentos.add(d.getString("id_departamento"))
                }

                spinnerDepartamento.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listaDepartamentos
                )
            },
            {
                Toast.makeText(this, "Error cargando departamentos", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(peticion)
    }

    // ============================================================
    // REGISTRAR USUARIO
    // ============================================================
    fun registrarUsuario() {
        val url = "$BASE/usuarios_agregar.php"

        val nombre = findViewById<EditText>(R.id.edtNombre).text.toString()
        val email = findViewById<EditText>(R.id.edtEmail).text.toString()
        val password = findViewById<EditText>(R.id.edtPassword).text.toString()
        val telefono = findViewById<EditText>(R.id.edtTelefono).text.toString()
        val rut = findViewById<EditText>(R.id.edtRut).text.toString()

        val estado = spinnerEstado.selectedItem.toString()
        val rol = spinnerRol.selectedItem.toString()
        val idDepartamento = idsDepartamentos[spinnerDepartamento.selectedItemPosition]

        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos obligatorios")
                .setContentText("Nombre, email y contraseña son obligatorios.")
                .show()
            return
        }

        val peticion = object : StringRequest(
            Method.POST, url,
            {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Usuario agregado")
                    .show()

                limpiarFormulario()
                cargarUsuarios()
            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al agregar usuario")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password,
                    "telefono" to telefono,
                    "rut" to rut,
                    "estado" to estado,
                    "rol" to rol,
                    "id_departamento" to idDepartamento
                )
        }

        Volley.newRequestQueue(this).add(peticion)
    }

    // ============================================================
    // ABRIR EDITAR USUARIO
    // ============================================================
    fun abrirEditarUsuario(pos: Int) {
        val u = ultimoJson[pos]

        val intent = Intent(this, EditarUsuario::class.java)

        intent.putExtra("id_usuario", u.getString("id_usuario"))
        intent.putExtra("nombre", u.getString("nombre"))
        intent.putExtra("email", u.getString("email"))
        intent.putExtra("telefono", u.getString("telefono"))
        intent.putExtra("rut", u.getString("rut"))
        intent.putExtra("estado", u.getString("estado"))
        intent.putExtra("rol", u.getString("rol"))
        intent.putExtra("id_departamento", u.getString("id_departamento"))

        startActivity(intent)
    }

    // ============================================================
    // CARGAR LISTA DESDE PHP
    // ============================================================
    fun cargarUsuarios() {
        val url = "$BASE/usuarios_listar.php"

        val peticion = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                datosUsuarios.clear()
                ultimoJson.clear()

                for (i in 0 until response.length()) {
                    val u = response.getJSONObject(i)

                    ultimoJson.add(u)

                    // SOLO EMAIL + RUT + DEPARTAMENTO
                    val texto =
                        "Email: ${u.getString("email")}\n" +
                                "RUT: ${u.getString("rut")}\n" +
                                "Departamento: Torre ${u.getString("torre")}, ${u.getString("piso")}, Nº ${u.getString("numero")}"

                    datosUsuarios.add(texto)
                }

                adaptadorUsuarios.notifyDataSetChanged()
            },
            {
                datosUsuarios.clear()
                datosUsuarios.add("Error cargando usuarios")
                adaptadorUsuarios.notifyDataSetChanged()
            }
        )

        Volley.newRequestQueue(this).add(peticion)
    }

    // ============================================================
    // LIMPIAR CAMPOS
    // ============================================================
    fun limpiarFormulario() {
        findViewById<EditText>(R.id.edtNombre).setText("")
        findViewById<EditText>(R.id.edtEmail).setText("")
        findViewById<EditText>(R.id.edtPassword).setText("")
        findViewById<EditText>(R.id.edtTelefono).setText("")
        findViewById<EditText>(R.id.edtRut).setText("")

        spinnerEstado.setSelection(0)
        spinnerRol.setSelection(0)
        spinnerDepartamento.setSelection(0)
    }
}
