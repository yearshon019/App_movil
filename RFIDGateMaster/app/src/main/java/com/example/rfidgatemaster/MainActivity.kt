package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private val BASE = "http://100.106.124.81/rfid_api/"

    private lateinit var emaillogin: EditText
    private lateinit var passwordlogin: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnTarjeta: Button
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            insets
        }

        emaillogin = findViewById(R.id.emaillogin)
        passwordlogin = findViewById(R.id.passwordlogin)
        btnLogin = findViewById(R.id.btnLogin)
        requestQueue = Volley.newRequestQueue(this)

        // -----------------------------
        // LOGIN NORMAL (EMAIL + PASS)
        // -----------------------------
        btnLogin.setOnClickListener {
            val email = emaillogin.text.toString().trim()
            val password_hash = passwordlogin.text.toString().trim()

            if (email.isEmpty() || password_hash.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Campos obligatorios")
                    .setContentText("Por favor completa todos los campos.")
                    .show()
                return@setOnClickListener
            }

            if (email.contains("@") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email inválido")
                    .setContentText("Por favor ingresa un correo válido.")
                    .show()
                return@setOnClickListener
            }

            consultarDatos(email, password_hash)
        }
    }

    // --------------------------------------------------------------------
    // LOGIN normal con email y contraseña
    // --------------------------------------------------------------------
    private fun consultarDatos(email: String, password_hash: String) {
        val url = "$BASE/login.php?email=$email&password_hash=$password_hash"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val ok = response.getBoolean("ok")

                    if (!ok) {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Credenciales inválidas")
                            .setContentText("Usuario o contraseña incorrectos.")
                            .show()
                        return@JsonObjectRequest
                    }

                    val estado = response.getString("estado")
                    val rol = response.getString("rol")

                    when (estado) {
                        "BLOQUEADO" -> {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Usuario Bloqueado")
                                .setContentText("Tu cuenta ha sido bloqueada.")
                                .show()
                        }

                        "INACTIVO" -> {
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Usuario Inactivo")
                                .setContentText("Tu cuenta está inactiva.")
                                .show()
                        }

                        "ACTIVO" -> {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Bienvenido")
                                .setContentText("Inicio de sesión exitoso.")
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()

                                    val idUsuario = response.getString("id_usuario")  // ← IMPORTANTE
                                    val rol = response.getString("rol")

                                    val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
                                    prefs.edit()
                                        .putString("id_usuario", idUsuario)
                                        .putString("rol", rol)
                                        .apply()

                                    val ventana = Intent(this@MainActivity, Dashboard::class.java)
                                    startActivity(ventana)
                                }
                                .show()
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error en la respuesta")
                        .setContentText("El servidor no devolvió datos válidos.")
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de conexión")
                    .setContentText("No se pudo conectar al servidor.")
                    .show()
                error.printStackTrace()
            }
        )

        requestQueue.add(request)
    }
}
