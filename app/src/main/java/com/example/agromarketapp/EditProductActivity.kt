package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditProductActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var productoIndex: Int = -1
    private var uriImagen: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        productoIndex = intent.getIntExtra("productoIndex", -1)

        imageView = findViewById(R.id.imageViewProducto)

        val uri = prefs.getString("imagen_producto_${usuarioActual}_$productoIndex", null)
        if (!uri.isNullOrEmpty()) {
            uriImagen = Uri.parse(uri)
            imageView.setImageURI(uriImagen)
        }

        val productosStr = prefs.getString("productos_$usuarioActual", null)
        val productos = productosStr?.split(";")?.toMutableList() ?: mutableListOf()

        val productoCampos = productos.getOrNull(productoIndex)?.split("|") ?: listOf("", "", "", "", "", "", "", "")

        val editNombre = findViewById<EditText>(R.id.editTextNombreProducto)
        val editDescripcion = findViewById<EditText>(R.id.editTextDescripcionProducto)
        val editPrecio = findViewById<EditText>(R.id.editTextPrecioProducto)
        val editTipoCultivo = findViewById<EditText>(R.id.editTextTipoCultivo)
        val editUnidadVenta = findViewById<EditText>(R.id.editTextUnidadVenta)
        val editFechaCosecha = findViewById<EditText>(R.id.editTextFechaCosecha)
        val editCertificacion = findViewById<EditText>(R.id.editTextCertificacion)
        val editRegion = findViewById<EditText>(R.id.editTextRegionOrigen)

        editNombre.setText(productoCampos.getOrNull(0) ?: "")
        editDescripcion.setText(productoCampos.getOrNull(1) ?: "")
        editPrecio.setText(productoCampos.getOrNull(2) ?: "")
        editTipoCultivo.setText(productoCampos.getOrNull(3) ?: "")
        editUnidadVenta.setText(productoCampos.getOrNull(4) ?: "")
        editFechaCosecha.setText(productoCampos.getOrNull(5) ?: "")
        editCertificacion.setText(productoCampos.getOrNull(6) ?: "")
        editRegion.setText(productoCampos.getOrNull(7) ?: "")

        findViewById<Button>(R.id.buttonSeleccionarImagenProducto).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 110)
        }

        findViewById<Button>(R.id.buttonGuardarProducto).setOnClickListener {
            val nuevoProducto = listOf(
                editNombre.text.toString().trim(),
                editDescripcion.text.toString().trim(),
                editPrecio.text.toString().trim(),
                editTipoCultivo.text.toString().trim(),
                editUnidadVenta.text.toString().trim(),
                editFechaCosecha.text.toString().trim(),
                editCertificacion.text.toString().trim(),
                editRegion.text.toString().trim()
            ).joinToString("|")

            if (productoIndex >= 0 && productoIndex < productos.size) {
                productos[productoIndex] = nuevoProducto
            } else {
                productos.add(nuevoProducto)
            }

            prefs.edit().putString("productos_$usuarioActual", productos.joinToString(";")).apply()

            uriImagen?.let {
                prefs.edit().putString("imagen_producto_${usuarioActual}_$productoIndex", it.toString()).apply()
            }


            Toast.makeText(this, "Producto guardado correctamente", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 110 && resultCode == RESULT_OK) {
            uriImagen = data?.data
            imageView.setImageURI(uriImagen)

            uriImagen?.let {
                try {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }


        }
    }
}
