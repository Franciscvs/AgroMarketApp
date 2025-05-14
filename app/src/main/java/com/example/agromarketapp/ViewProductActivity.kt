package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class ViewProductActivity : AppCompatActivity() {

    private lateinit var imageViewProducto: ImageView
    private lateinit var textNombre: TextView
    private lateinit var textDescripcion: TextView
    private lateinit var textPrecio: TextView
    private lateinit var textTipoCultivo: TextView
    private lateinit var textUnidadVenta: TextView
    private lateinit var textFechaCosecha: TextView
    private lateinit var textCertificacion: TextView
    private lateinit var textRegionOrigen: TextView

    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button
    private lateinit var buttonComprar: Button

    private lateinit var producto: Producto
    private lateinit var usuarioPropietario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        // Referencias UI
        imageViewProducto = findViewById(R.id.imageViewProducto)
        textNombre = findViewById(R.id.textNombreProducto)
        textDescripcion = findViewById(R.id.textDescripcionProducto)
        textPrecio = findViewById(R.id.textPrecioProducto)
        textTipoCultivo = findViewById(R.id.textTipoCultivo)
        textUnidadVenta = findViewById(R.id.textUnidadVenta)
        textFechaCosecha = findViewById(R.id.textFechaCosecha)
        textCertificacion = findViewById(R.id.textCertificacion)
        textRegionOrigen = findViewById(R.id.textRegionOrigen)

        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)
        buttonComprar = findViewById(R.id.buttonComprar)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)
        usuarioPropietario = intent.getStringExtra("usuario_propietario") ?: return

        // Cargar producto desde intent
        val productoJson = intent.getStringExtra("producto")
        producto = Gson().fromJson(productoJson, Producto::class.java)

        // Mostrar datos del producto
        textNombre.text = producto.nombre
        textDescripcion.text = "Descripción: ${producto.descripcion}"
        textPrecio.text = "Precio: ${producto.precio}"
        textTipoCultivo.text = "Tipo de cultivo: ${producto.tipoCultivo}"
        textUnidadVenta.text = "Unidad de venta: ${producto.unidadVenta}"
        textFechaCosecha.text = "Fecha de cosecha: ${producto.fechaCosecha}"
        textCertificacion.text = "Certificación: ${producto.certificacion}"
        textRegionOrigen.text = "Región de origen: ${producto.regionOrigen}"

        // Imagen del producto (del usuario propietario)
        val uriStr = prefs.getString("imagen_producto_${usuarioPropietario}_${producto.nombre}", null)
        if (!uriStr.isNullOrEmpty()) {
            imageViewProducto.setImageURI(Uri.parse(uriStr))
        } else {
            imageViewProducto.setImageResource(R.drawable.imagen_producto) // imagen por defecto
        }

        // Comprar
        buttonComprar.setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("producto_comprado", Gson().toJson(producto))
            intent.putExtra("usuario_propietario", usuarioPropietario)
            startActivity(intent)
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
