package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editUsuario: EditText
    private lateinit var editContrasena: EditText
    private lateinit var buttonIniciarSesion: Button
    private lateinit var buttonIrARegistro: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonOlvidoContrasena: Button
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUsuario = findViewById(R.id.editTextText1)
        editContrasena = findViewById(R.id.editTextTextPassword1)
        buttonIniciarSesion = findViewById(R.id.button2)
        buttonIrARegistro = findViewById(R.id.button4)
        buttonVolver = findViewById(R.id.button1)
        buttonOlvidoContrasena = findViewById(R.id.button3)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        buttonIniciarSesion.setOnClickListener {
            val usuarioIngresado = editUsuario.text.toString().trim()
            val contrasenaIngresada = editContrasena.text.toString().trim()

            if (usuarioIngresado.isEmpty() || contrasenaIngresada.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)
            val usuario = listaUsuarios.find {
                it.usuario == usuarioIngresado && it.contrasena == contrasenaIngresada
            }

            if (usuario != null) {
                prefs.edit().putString("usuario_actual", usuario.usuario).apply()
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVolver.setOnClickListener {
            startActivity(Intent(this, BaseActivity::class.java))
        }

        buttonOlvidoContrasena.setOnClickListener {
            startActivity(Intent(this, EmailActivity::class.java))
        }

        buttonIrARegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
