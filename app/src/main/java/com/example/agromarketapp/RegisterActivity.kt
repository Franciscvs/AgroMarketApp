package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterActivity : AppCompatActivity() {

    private lateinit var editUsuario: EditText
    private lateinit var editNombres: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editContrasena: EditText
    private lateinit var editConfirmarContrasena: EditText
    private lateinit var buttonRegistrar: Button
    private lateinit var buttonVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Referencias UI
        editUsuario = findViewById(R.id.editTextText2)
        editNombres = findViewById(R.id.editTextText1)
        editApellidos = findViewById(R.id.editTextText4)
        editCorreo = findViewById(R.id.editTextText3)
        editContrasena = findViewById(R.id.editTextTextPassword1)
        editConfirmarContrasena = findViewById(R.id.editTextTextPassword2)
        buttonRegistrar = findViewById(R.id.button2)
        buttonVolver = findViewById(R.id.button1)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        buttonRegistrar.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()
            val nombres = editNombres.text.toString().trim()
            val apellidos = editApellidos.text.toString().trim()
            val correo = editCorreo.text.toString().trim()
            val contrasena = editContrasena.text.toString()
            val confirmar = editConfirmarContrasena.text.toString()

            if (usuario.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lista = Usuario.cargarUsuariosDesdePrefs(prefs)

            if (lista.any { it.usuario == usuario }) {
                Toast.makeText(this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(usuario, nombres, apellidos, correo, contrasena)
            lista.add(nuevoUsuario)
            Usuario.guardarUsuariosEnPrefs(prefs, lista)

            // Guardar como usuario actual
            prefs.edit().putString("usuario_actual", usuario).apply()

            // Ir al menú principal
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonVolver.setOnClickListener {
            finish()
        }
    }
}
