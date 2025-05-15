package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EmailActivity : AppCompatActivity() {

    private lateinit var editCorreo: EditText
    private lateinit var buttonConfirmar: Button
    private lateinit var buttonVolver: Button
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        editCorreo = findViewById(R.id.editTextText1)
        buttonConfirmar = findViewById(R.id.button2)
        buttonVolver = findViewById(R.id.button1)
        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        buttonConfirmar.setOnClickListener {
            val correoIngresado = editCorreo.text.toString().trim()

            if (correoIngresado.isEmpty()) {
                Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lista = Usuario.cargarUsuariosDesdePrefs(prefs)
            val usuario = lista.find { it.correo == correoIngresado }

            if (usuario != null) {
                // Guardamos temporalmente el usuario a recuperar contraseña
                prefs.edit().putString("usuario_para_reset", usuario.usuario).apply()
                startActivity(Intent(this, PasswordActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Correo no registrado", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVolver.setOnClickListener {
            finish()
        }
    }
}
