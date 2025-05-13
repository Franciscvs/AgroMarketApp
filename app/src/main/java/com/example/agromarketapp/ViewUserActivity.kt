package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ViewUserActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioSeleccionado: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_user)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioSeleccionado = intent.getStringExtra("usuarioSeleccionado") ?: ""

        val usuarios = prefs.getString("usuarios", null)?.split(";")?.map {
            val campos = it.split("|")
            Usuario(campos[0], campos[1], campos[2], campos[3], campos[4])
        } ?: emptyList()

        val encontrado = usuarios.find { it.usuario == usuarioSeleccionado }

        if (encontrado != null) {
            findViewById<TextView>(R.id.textUsuario).text = "Usuario: ${encontrado.usuario}"
            findViewById<TextView>(R.id.textNombres).text = "Nombres: ${encontrado.nombres}"
            findViewById<TextView>(R.id.textApellidos).text = "Apellidos: ${encontrado.apellidos}"
            findViewById<TextView>(R.id.textCorreo).text = "Correo: ${encontrado.correo}"
            findViewById<TextView>(R.id.textContrasena).text = "Contraseña: ${encontrado.contrasena}"

            val uri = prefs.getString("imagen_${encontrado.usuario}", null)
            if (!uri.isNullOrEmpty()) {
                try {
                    findViewById<ImageView>(R.id.imageViewUsuario).setImageURI(Uri.parse(uri))
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar imagen de usuario", Toast.LENGTH_SHORT).show()
                }
            }

            findViewById<Button>(R.id.buttonVerTienda).setOnClickListener {
                val intent = Intent(this, ViewStoreActivity::class.java)
                intent.putExtra("usuario", encontrado.usuario)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "No se encontró el usuario.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonCerrarSesion).setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}