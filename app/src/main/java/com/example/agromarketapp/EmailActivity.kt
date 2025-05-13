package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EmailActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        val editCorreo = findViewById<EditText>(R.id.editTextText1)
        val btnSiguiente = findViewById<Button>(R.id.button2)
        val btnVolver = findViewById<Button>(R.id.button1)

        btnSiguiente.setOnClickListener {
            val correoIngresado = editCorreo.text.toString().trim()

            if (correoIngresado.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarios = obtenerUsuariosGuardados(prefs.getString("usuarios", null))
            val usuario = usuarios.find { it.correo == correoIngresado }

            if (usuario != null) {

                prefs.edit().putString("usuario_actual", usuario.usuario).apply()
                startActivity(Intent(this, PasswordActivity::class.java))
            } else {
                Toast.makeText(this, "Correo no encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun obtenerUsuariosGuardados(prefString: String?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        if (!prefString.isNullOrEmpty()) {
            val registros = prefString.split(";")
            for (r in registros) {
                val campos = r.split("|")
                if (campos.size == 5) {
                    lista.add(Usuario(campos[0], campos[1], campos[2], campos[3], campos[4]))
                }
            }
        }
        return lista
    }
}