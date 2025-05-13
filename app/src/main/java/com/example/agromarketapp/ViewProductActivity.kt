package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ViewProductActivity : AppCompatActivity() {

    private lateinit var nombre: String
    private lateinit var descripcion: String
    private var precio: Double = 0.0
    private lateinit var tipoCultivo: String
    private lateinit var unidadVenta: String
    private lateinit var fechaCosecha: String
    private lateinit var certificacion: String
    private lateinit var regionOrigen: String
    private var uriImagen: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        nombre = intent.getStringExtra("nombre") ?: ""
        descripcion = intent.getStringExtra("descripcion") ?: ""
        precio = intent.getDoubleExtra("precio", 0.0)
        tipoCultivo = intent.getStringExtra("tipoCultivo") ?: ""
        unidadVenta = intent.getStringExtra("unidadVenta") ?: ""
        fechaCosecha = intent.getStringExtra("fechaCosecha") ?: ""
        certificacion = intent.getStringExtra("certificacion") ?: ""
        regionOrigen = intent.getStringExtra("regionOrigen") ?: ""
        uriImagen = intent.getStringExtra("imagenUri")

        findViewById<TextView>(R.id.textNombreProducto).text = "Nombre: $nombre"
        findViewById<TextView>(R.id.textDescripcionProducto).text = "Descripción: $descripcion"
        findViewById<TextView>(R.id.textPrecioProducto).text = "Precio: $$precio"
        findViewById<TextView>(R.id.textTipoCultivo).text = "Tipo de cultivo: $tipoCultivo"
        findViewById<TextView>(R.id.textUnidadVenta).text = "Unidad de venta: $unidadVenta"
        findViewById<TextView>(R.id.textFechaCosecha).text = "Fecha de cosecha: $fechaCosecha"
        findViewById<TextView>(R.id.textCertificacion).text = "Certificación: $certificacion"
        findViewById<TextView>(R.id.textRegionOrigen).text = "Región de origen: $regionOrigen"

        val imageView = findViewById<ImageView>(R.id.imageViewProducto)
        if (!uriImagen.isNullOrEmpty()) {
            try {
                imageView.setImageURI(Uri.parse(uriImagen))
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.buttonComprar).setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("precio", precio)
            intent.putExtra("tipoCultivo", tipoCultivo)
            intent.putExtra("unidadVenta", unidadVenta)
            intent.putExtra("fechaCosecha", fechaCosecha)
            intent.putExtra("certificacion", certificacion)
            intent.putExtra("regionOrigen", regionOrigen)
            intent.putExtra("imagenUri", uriImagen ?: "")
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonCerrarSesion).setOnClickListener {
            getSharedPreferences("datos_usuario", MODE_PRIVATE).edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}