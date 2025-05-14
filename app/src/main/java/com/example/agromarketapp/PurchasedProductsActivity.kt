package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PurchasedProductsActivity : AppCompatActivity() {

    private lateinit var contenedorCompras: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchased_products)

        contenedorCompras = findViewById(R.id.contenedorCompras)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        if (intent.getBooleanExtra("compra_realizada", false)) {
            val mensaje = TextView(this)
            mensaje.text = "¡Gracias por tu compra!"
            mensaje.textSize = 20f
            mensaje.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            mensaje.gravity = Gravity.CENTER
            contenedorCompras.addView(mensaje)
        }

        if (usuarioActual != null) {
            val comprasJson = prefs.getString("productos_comprados_$usuarioActual", null)
            val tipoLista = object : TypeToken<MutableList<Producto>>() {}.type
            val productos = if (comprasJson != null)
                Gson().fromJson<MutableList<Producto>>(comprasJson, tipoLista)
            else mutableListOf()

            contenedorCompras.removeAllViews()

            for (producto in productos.toList()) {
                val layout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                }

                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(300, 300)
                    val uriStr = prefs.getString("imagen_producto_${usuarioActual}_${producto.nombre}", null)
                    if (!uriStr.isNullOrEmpty()) {
                        setImageURI(Uri.parse(uriStr))
                    } else {
                        setImageResource(R.drawable.imagen_producto)
                    }
                }

                val texto = TextView(this).apply {
                    text = "${producto.nombre} - ${producto.precio}"
                    textSize = 18f
                }

                val botonEliminar = Button(this).apply {
                    text = "Eliminar"
                    setOnClickListener {
                        AlertDialog.Builder(this@PurchasedProductsActivity)
                            .setTitle("Eliminar compra")
                            .setMessage("¿Deseas eliminar '${producto.nombre}' de tu historial de compras?")
                            .setPositiveButton("Sí") { _, _ ->
                                productos.remove(producto)
                                val nuevoJson = Gson().toJson(productos)
                                prefs.edit().putString("productos_comprados_$usuarioActual", nuevoJson).apply()
                                recreate()
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }

                layout.addView(imageView)
                layout.addView(texto)
                layout.addView(botonEliminar)

                contenedorCompras.addView(layout)
            }

            if (productos.isEmpty()) {
                val texto = TextView(this).apply {
                    text = "No hay productos comprados aún."
                    textSize = 18f
                    gravity = Gravity.CENTER
                }
                contenedorCompras.addView(texto)
            }
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
