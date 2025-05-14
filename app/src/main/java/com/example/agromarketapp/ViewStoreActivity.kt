package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class ViewStoreActivity : AppCompatActivity() {

    private lateinit var imageViewTienda: ImageView
    private lateinit var textNombreTienda: TextView
    private lateinit var textDescripcionTienda: TextView
    private lateinit var textUbicacion: TextView
    private lateinit var textHorario: TextView
    private lateinit var textContacto: TextView
    private lateinit var textCategoria: TextView
    private lateinit var contenedorProductos: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var usuarioPropietario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_store)

        // Referencias UI
        imageViewTienda = findViewById(R.id.imageViewTienda)
        textNombreTienda = findViewById(R.id.textNombreTienda)
        textDescripcionTienda = findViewById(R.id.textDescripcionTienda)
        textUbicacion = findViewById(R.id.textUbicacion)
        textHorario = findViewById(R.id.textHorario)
        textContacto = findViewById(R.id.textContacto)
        textCategoria = findViewById(R.id.textCategoria)
        contenedorProductos = findViewById(R.id.contenedorProductos)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioPropietario = intent.getStringExtra("usuario_propietario") ?: return

        // Cargar datos de tienda del propietario
        val tiendaJson = prefs.getString("tienda_$usuarioPropietario", null)
        tiendaJson?.let {
            val tienda = Gson().fromJson(it, Tienda::class.java)
            textNombreTienda.text = tienda.nombre
            textDescripcionTienda.text = "Descripción de la tienda: ${tienda.descripcion}"
            textUbicacion.text = "Ubicación: ${tienda.ubicacion}"
            textHorario.text = "Horario: ${tienda.horario}"
            textContacto.text = "Contacto: ${tienda.contacto}"
            textCategoria.text = "Categoría: ${tienda.categoria}"
        }

        // Imagen de la tienda
        val uriStr = prefs.getString("imagen_tienda_$usuarioPropietario", null)
        if (!uriStr.isNullOrEmpty()) {
            imageViewTienda.setImageURI(Uri.parse(uriStr))
        }

        // Cargar productos del usuario propietario
        val productos = Producto.cargarProductosDesdePrefs(prefs, usuarioPropietario)
        contenedorProductos.removeAllViews()

        for (producto in productos) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val image = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(300, 300)
                val uri = prefs.getString("imagen_producto_${usuarioPropietario}_${producto.nombre}", null)
                if (!uri.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uri))
                } else {
                    setImageResource(R.drawable.imagen_producto) // imagen por defecto
                }
            }

            val texto = TextView(this).apply {
                text = "${producto.nombre} - ${producto.precio}"
                textSize = 18f
            }

            val botonVer = Button(this).apply {
                text = "Ver Producto"
                setOnClickListener {
                    val intent = Intent(this@ViewStoreActivity, ViewProductActivity::class.java)
                    intent.putExtra("usuario_propietario", usuarioPropietario)
                    intent.putExtra("producto", Gson().toJson(producto))
                    startActivity(intent)
                }
            }

            layout.addView(image)
            layout.addView(texto)
            layout.addView(botonVer)
            contenedorProductos.addView(layout)
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
