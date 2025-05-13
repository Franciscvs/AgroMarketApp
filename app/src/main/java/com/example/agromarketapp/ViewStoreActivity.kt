package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class ViewStoreActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuario: String
    private lateinit var contenedor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_store)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuario = intent.getStringExtra("usuario") ?: ""
        contenedor = findViewById(R.id.contenedorProductos)

        val tiendaStr = prefs.getString("tienda_$usuario", null)
        val campos = tiendaStr?.split("|") ?: listOf("", "", "", "", "", "")

        findViewById<TextView>(R.id.textNombreTienda).text = "Nombre: ${campos.getOrNull(0) ?: ""}"
        findViewById<TextView>(R.id.textDescripcionTienda).text = "Descripción: ${campos.getOrNull(1) ?: ""}"
        findViewById<TextView>(R.id.textUbicacion).text = "Ubicación: ${campos.getOrNull(2) ?: ""}"
        findViewById<TextView>(R.id.textHorario).text = "Horario: ${campos.getOrNull(3) ?: ""}"
        findViewById<TextView>(R.id.textContacto).text = "Contacto: ${campos.getOrNull(4) ?: ""}"
        findViewById<TextView>(R.id.textCategoria).text = "Categoría: ${campos.getOrNull(5) ?: ""}"

        val imageView = findViewById<ImageView>(R.id.imageViewTienda)
        val uri = prefs.getString("imagen_tienda_$usuario", null)
        if (!uri.isNullOrEmpty()) {
            try {
                imageView.setImageURI(Uri.parse(uri))
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar la imagen de la tienda", Toast.LENGTH_SHORT).show()
            }
        }

        val productosStr = prefs.getString("productos_$usuario", null)
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

        contenedor.removeAllViews()

        productos.forEachIndexed { index, producto ->
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(16)

            val nombre = TextView(this)
            nombre.text = "${producto.nombre} - $${producto.precio}"
            nombre.textSize = 18f
            layout.addView(nombre)

            val uriProd = prefs.getString("imagen_producto_${usuario}_$index", null)
            if (!uriProd.isNullOrEmpty()) {
                val image = ImageView(this)
                image.setImageURI(Uri.parse(uriProd))
                image.layoutParams = LinearLayout.LayoutParams(300, 300)
                layout.addView(image)
            }

            layout.setOnClickListener {
                val intent = Intent(this, ViewProductActivity::class.java)
                intent.putExtra("nombre", producto.nombre)
                intent.putExtra("descripcion", producto.descripcion)
                intent.putExtra("precio", producto.precio)
                intent.putExtra("tipoCultivo", producto.tipoCultivo)
                intent.putExtra("unidadVenta", producto.unidadVenta)
                intent.putExtra("fechaCosecha", producto.fechaCosecha)
                intent.putExtra("certificacion", producto.certificacion)
                intent.putExtra("regionOrigen", producto.regionOrigen)
                intent.putExtra("imagenUri", uriProd)
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