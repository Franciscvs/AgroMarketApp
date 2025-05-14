package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editUsuario: EditText
    private lateinit var editContrasena: EditText
    private lateinit var buttonIngresar: Button
    private lateinit var buttonIraRegistro: Button
    private lateinit var buttonOlvidoContrasena: Button
    private lateinit var buttonVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUsuario = findViewById(R.id.editTextText1)
        editContrasena = findViewById(R.id.editTextTextPassword1)
        buttonIngresar = findViewById(R.id.button2)
        buttonIraRegistro = findViewById(R.id.button4)
        buttonOlvidoContrasena = findViewById(R.id.button3)
        buttonVolver = findViewById(R.id.button1)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        buttonIngresar.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()
            val contrasena = editContrasena.text.toString()

            val lista = Usuario.cargarUsuariosDesdePrefs(prefs)

            val encontrado = lista.find { it.usuario == usuario && it.contrasena == contrasena }

            if (encontrado != null) {
                prefs.edit().putString("usuario_actual", usuario).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        buttonIraRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        buttonOlvidoContrasena.setOnClickListener {
            startActivity(Intent(this, EmailActivity::class.java))
        }

        buttonVolver.setOnClickListener {
            finish()
        }
    }
}
