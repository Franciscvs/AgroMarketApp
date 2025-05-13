package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class YourUsernameActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_username)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        imageView = findViewById(R.id.imageViewUsuario)

        val listaUsuarios = prefs.getString("usuarios", null)?.split(";")?.map {
            val campos = it.split("|")
            Usuario(campos[0], campos[1], campos[2], campos[3], campos[4])
        } ?: emptyList()

        val usuario = listaUsuarios.find { it.usuario == usuarioActual }

        if (usuario != null) {
            findViewById<TextView>(R.id.textUsuario).text = "Usuario: ${usuario.usuario}"
            findViewById<TextView>(R.id.textNombres).text = "Nombres: ${usuario.nombres}"
            findViewById<TextView>(R.id.textApellidos).text = "Apellidos: ${usuario.apellidos}"
            findViewById<TextView>(R.id.textCorreo).text = "Correo: ${usuario.correo}"

            val uri = prefs.getString("imagen_${usuario.usuario}", null)
            if (!uri.isNullOrEmpty()) {
                try {
                    imageView.setImageURI(Uri.parse(uri))
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar imagen de usuario", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontraron los datos del usuario", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.buttonEditar).setOnClickListener {
            startActivityForResult(Intent(this, EditUserActivity::class.java), 200)
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.button1).setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            recreate()
        }
    }

    private fun cerrarSesion() {
        prefs.edit().remove("usuario_actual").apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}