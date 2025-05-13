package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class OtherStoresActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private lateinit var contenedor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_stores)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        contenedor = findViewById(R.id.contenedorTiendas)

        val usuarios = prefs.getString("usuarios", null)?.split(";")?.mapNotNull {
            val campos = it.split("|")
            campos.getOrNull(0)
        }?.filter { it != usuarioActual } ?: emptyList()

        contenedor.removeAllViews()

        usuarios.forEach { usuario ->
            val tiendaStr = prefs.getString("tienda_$usuario", null)
            val tiendaCampos = tiendaStr?.split("|") ?: return@forEach

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(16)

            val nombre = TextView(this)
            nombre.text = tiendaCampos.getOrNull(0) ?: "Tienda sin nombre"
            nombre.textSize = 18f
            layout.addView(nombre)

            val descripcion = TextView(this)
            descripcion.text = tiendaCampos.getOrNull(1) ?: ""
            layout.addView(descripcion)

            val uri = prefs.getString("imagen_tienda_$usuario", null)
            if (!uri.isNullOrEmpty()) {
                try {
                    val image = ImageView(this)
                    image.setImageURI(Uri.parse(uri))
                    image.layoutParams = LinearLayout.LayoutParams(300, 300)
                    layout.addView(image)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar imagen de tienda", Toast.LENGTH_SHORT).show()
                }
            }

            layout.setOnClickListener {
                val intent = Intent(this, ViewStoreActivity::class.java)
                intent.putExtra("usuario", usuario)
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