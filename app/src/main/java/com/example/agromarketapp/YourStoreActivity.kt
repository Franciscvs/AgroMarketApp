package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class YourStoreActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private lateinit var contenedorProductos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_store)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        contenedorProductos = findViewById(R.id.contenedorProductos)
        val imageViewTienda = findViewById<ImageView>(R.id.imageViewTienda)

        // Mostrar datos de tienda
        val tiendaStr = prefs.getString("tienda_$usuarioActual", null)
        val campos = tiendaStr?.split("|") ?: listOf("", "", "", "", "", "")

        findViewById<TextView>(R.id.textNombreTienda).text = campos.getOrNull(0) ?: ""
        findViewById<TextView>(R.id.textDescripcionTienda).text = campos.getOrNull(1) ?: ""
        findViewById<TextView>(R.id.textUbicacion).text = "Ubicación: ${campos.getOrNull(2) ?: ""}"
        findViewById<TextView>(R.id.textHorario).text = "Horario: ${campos.getOrNull(3) ?: ""}"
        findViewById<TextView>(R.id.textContacto).text = "Contacto: ${campos.getOrNull(4) ?: ""}"
        findViewById<TextView>(R.id.textCategoria).text = "Categoría: ${campos.getOrNull(5) ?: ""}"

        val uri = prefs.getString("imagen_tienda_$usuarioActual", null)
        if (!uri.isNullOrEmpty()) {
            imageViewTienda.setImageURI(Uri.parse(uri))
        }

        // Cargar productos propios
        val productosStr = prefs.getString("productos_$usuarioActual", null)
        val productos = productosStr?.split(";")?.map {
            val campos = it.split("|")
            Producto(
                campos.getOrElse(0) { "" },
                campos.getOrElse(1) { "" },
                campos.getOrElse(2) { "0.0" }.toDoubleOrNull() ?: 0.0,
                campos.getOrElse(3) { "" },
                campos.getOrElse(4) { "" },
                campos.getOrElse(5) { "" },
                campos.getOrElse(6) { "" },
                campos.getOrElse(7) { "" }
            )
        } ?: emptyList()

        contenedorProductos.removeAllViews()

        productos.forEachIndexed { index, producto ->
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(16)

            val nombre = TextView(this)
            nombre.text = "${producto.nombre} - $${producto.precio}"
            nombre.textSize = 18f
            layout.addView(nombre)

            val uriProd = prefs.getString("imagen_producto_${usuarioActual}_$index", null)
            if (!uriProd.isNullOrEmpty()) {
                val image = ImageView(this)
                image.setImageURI(Uri.parse(uriProd))
                image.layoutParams = LinearLayout.LayoutParams(300, 300)
                layout.addView(image)
            }

            layout.setOnClickListener {
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("indice", index)
                startActivityForResult(intent, 202)
            }

            contenedorProductos.addView(layout)
        }

        // Botón editar tienda
        findViewById<Button>(R.id.buttonEditarTienda).setOnClickListener {
            startActivityForResult(Intent(this, EditStoreActivity::class.java), 201)
        }

        // Botón agregar nuevo producto
        findViewById<Button>(R.id.buttonAgregarProducto).setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("indice", -1) // modo nuevo
            startActivityForResult(intent, 203)
        }

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.buttonCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode in listOf(201, 202, 203)) {
            recreate() // Recargar datos luego de editar tienda o productos
        }
    }

    private fun cerrarSesion() {
        prefs.edit().remove("usuario_actual").apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}