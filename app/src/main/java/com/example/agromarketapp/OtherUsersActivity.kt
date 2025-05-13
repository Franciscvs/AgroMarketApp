package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class OtherUsersActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private lateinit var contenedor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        contenedor = findViewById(R.id.contenedorUsuarios)

        val usuarios = prefs.getString("usuarios", null)?.split(";")?.mapNotNull {
            val campos = it.split("|")
            if (campos.size == 5) Usuario(campos[0], campos[1], campos[2], campos[3], campos[4]) else null
        }?.filter { it.usuario != usuarioActual } ?: emptyList()

        contenedor.removeAllViews()

        usuarios.forEach { usuario ->
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(16)

            val nombre = TextView(this)
            nombre.text = "${usuario.nombres} ${usuario.apellidos}"
            nombre.textSize = 18f
            layout.addView(nombre)

            val correo = TextView(this)
            correo.text = usuario.correo
            layout.addView(correo)

            val uri = prefs.getString("imagen_${usuario.usuario}", null)
            if (!uri.isNullOrEmpty()) {
                try {
                    val image = ImageView(this)
                    image.setImageURI(Uri.parse(uri))
                    image.layoutParams = LinearLayout.LayoutParams(300, 300)
                    layout.addView(image)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar imagen de usuario", Toast.LENGTH_SHORT).show()
                }
            }

            layout.setOnClickListener {
                val intent = Intent(this, ViewUserActivity::class.java)
                intent.putExtra("usuarioSeleccionado", usuario.usuario)
                startActivity(intent)
            }

            contenedor.addView(layout)
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