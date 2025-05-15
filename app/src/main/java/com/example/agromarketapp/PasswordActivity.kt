package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PasswordActivity : AppCompatActivity() {

    private lateinit var editNuevaContrasena: EditText
    private lateinit var editConfirmarContrasena: EditText
    private lateinit var textCorreo: TextView
    private lateinit var buttonCambiar: Button
    private lateinit var buttonAtras: Button

    private lateinit var prefs: android.content.SharedPreferences
    private var usuarioReset: String? = null
    private var correoUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // Referencias UI con los IDs existentes en tu layout
        editNuevaContrasena = findViewById(R.id.editTextTextPassword1)
        editConfirmarContrasena = findViewById(R.id.editTextTextPassword2)
        textCorreo = findViewById(R.id.textViewCorreo)
        buttonCambiar = findViewById(R.id.button2)
        buttonAtras = findViewById(R.id.button1)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioReset = prefs.getString("usuario_para_reset", null)

        if (usuarioReset.isNullOrEmpty()) {
            Toast.makeText(this, "No hay usuario para restaurar contraseña", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val lista = Usuario.cargarUsuariosDesdePrefs(prefs)
        val usuario = lista.find { it.usuario == usuarioReset }

        correoUsuario = usuario?.correo ?: "Correo no disponible"
        textCorreo.text = "Correo: $correoUsuario"

        buttonCambiar.setOnClickListener {
            val nueva = editNuevaContrasena.text.toString().trim()
            val confirmar = editConfirmarContrasena.text.toString().trim()

            if (nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Complete ambos campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listaActual = lista.toMutableList()
            val index = listaActual.indexOfFirst { it.usuario == usuarioReset }

            if (index != -1) {
                val usuarioActualizado = listaActual[index].copy(contrasena = nueva)
                listaActual[index] = usuarioActualizado
                Usuario.guardarUsuariosEnPrefs(prefs, listaActual)

                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                prefs.edit().remove("usuario_para_reset").apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        buttonAtras.setOnClickListener {
            finish()
        }
    }
}
