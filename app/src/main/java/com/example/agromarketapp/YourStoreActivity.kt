package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class YourStoreActivity : AppCompatActivity() {

    private lateinit var imageViewTienda: ImageView
    private lateinit var textNombre: TextView
    private lateinit var textDescripcion: TextView
    private lateinit var textUbicacion: TextView
    private lateinit var textHorario: TextView
    private lateinit var textContacto: TextView
    private lateinit var textCategoria: TextView
    private lateinit var contenedorProductos: LinearLayout

    private lateinit var buttonEditarTienda: Button
    private lateinit var buttonAgregarProducto: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var usuarioActual: String
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_store)

        // Referencias UI
        imageViewTienda = findViewById(R.id.imageViewTienda)
        textNombre = findViewById(R.id.textNombreTienda)
        textDescripcion = findViewById(R.id.textDescripcionTienda)
        textUbicacion = findViewById(R.id.textUbicacion)
        textHorario = findViewById(R.id.textHorario)
        textContacto = findViewById(R.id.textContacto)
        textCategoria = findViewById(R.id.textCategoria)
        contenedorProductos = findViewById(R.id.contenedorProductos)

        buttonEditarTienda = findViewById(R.id.buttonEditarTienda)
        buttonAgregarProducto = findViewById(R.id.buttonAgregarProducto)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        // Cargar info tienda
        val tiendaJson = prefs.getString("tienda_$usuarioActual", null)
        tiendaJson?.let {
            val tienda = Gson().fromJson(it, Tienda::class.java)
            textNombre.text = tienda.nombre
            textDescripcion.text = tienda.descripcion
            textUbicacion.text = "Ubicación: ${tienda.ubicacion}"
            textHorario.text = "Horario: ${tienda.horario}"
            textContacto.text = "Contacto: ${tienda.contacto}"
            textCategoria.text = "Categoría: ${tienda.categoria}"
        }

        // Imagen de la tienda
        val uriTienda = prefs.getString("imagen_tienda_$usuarioActual", null)
        if (!uriTienda.isNullOrEmpty()) {
            imageViewTienda.setImageURI(Uri.parse(uriTienda))
        }

        // Cargar productos del usuario
        val productos = Producto.cargarProductosDesdePrefs(prefs, usuarioActual)
        contenedorProductos.removeAllViews()

        for (producto in productos) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val image = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(300, 300)
                val uri = prefs.getString("imagen_producto_${usuarioActual}_${producto.nombre}", null)
                if (!uri.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uri))
                } else {
                    setImageResource(R.drawable.imagen_producto) // por defecto
                }
            }

            val texto = TextView(this).apply {
                text = "${producto.nombre} - ${producto.precio}"
                textSize = 18f
            }

            val botonVer = Button(this).apply {
                text = "Ver"
                setOnClickListener {
                    val intent = Intent(this@YourStoreActivity, ViewProductActivity::class.java)
                    intent.putExtra("producto", Gson().toJson(producto))
                    startActivity(intent)
                }
            }

            val botonEditar = Button(this).apply {
                text = "Editar"
                setOnClickListener {
                    val intent = Intent(this@YourStoreActivity, EditProductActivity::class.java)
                    intent.putExtra("producto_editar", Gson().toJson(producto))
                    startActivity(intent)
                }
            }

            layout.addView(image)
            layout.addView(texto)
            layout.addView(botonVer)
            layout.addView(botonEditar)

            contenedorProductos.addView(layout)
        }

        buttonEditarTienda.setOnClickListener {
            startActivity(Intent(this, EditStoreActivity::class.java))
        }

        buttonAgregarProducto.setOnClickListener {
            startActivity(Intent(this, EditProductActivity::class.java))
        }

        buttonVolver.setOnClickListener {
            finish()
        }

        buttonCerrarSesion.setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
