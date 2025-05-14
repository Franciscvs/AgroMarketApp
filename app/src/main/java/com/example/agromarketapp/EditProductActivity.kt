package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditProductActivity : AppCompatActivity() {

    private lateinit var imageViewProducto: ImageView
    private lateinit var editNombre: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var editPrecio: EditText
    private lateinit var editTipoCultivo: EditText
    private lateinit var editUnidadVenta: EditText
    private lateinit var editFechaCosecha: EditText
    private lateinit var editCertificacion: EditText
    private lateinit var editRegionOrigen: EditText
    private lateinit var editCategoria: EditText

    private lateinit var buttonGuardar: Button
    private lateinit var buttonSeleccionarImagen: Button
    private lateinit var buttonVolver: Button

    private var uriImagen: Uri? = null
    private var productoExistente: Producto? = null
    private lateinit var usuarioActual: String
    private lateinit var prefs: android.content.SharedPreferences

    private val REQUEST_IMAGE_PICK = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        // Referencias UI
        imageViewProducto = findViewById(R.id.imageViewProducto)
        editNombre = findViewById(R.id.editTextNombreProducto)
        editDescripcion = findViewById(R.id.editTextDescripcionProducto)
        editPrecio = findViewById(R.id.editTextPrecioProducto)
        editTipoCultivo = findViewById(R.id.editTextTipoCultivo)
        editUnidadVenta = findViewById(R.id.editTextUnidadVenta)
        editFechaCosecha = findViewById(R.id.editTextFechaCosecha)
        editCertificacion = findViewById(R.id.editTextCertificacion)
        editRegionOrigen = findViewById(R.id.editTextRegionOrigen)
        editCategoria = findViewById(R.id.editTextCategoria)

        buttonGuardar = findViewById(R.id.buttonGuardarProducto)
        buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagenProducto)
        buttonVolver = findViewById(R.id.buttonVolver)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        // Verificar si se está editando un producto existente
        val productoJson = intent.getStringExtra("producto_editar")
        if (!productoJson.isNullOrEmpty()) {
            productoExistente = Gson().fromJson(productoJson, Producto::class.java)
            cargarDatosProducto(productoExistente!!)
        }

        buttonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        buttonGuardar.setOnClickListener {
            val producto = Producto(
                nombre = editNombre.text.toString(),
                descripcion = editDescripcion.text.toString(),
                precio = editPrecio.text.toString(),
                tipoCultivo = editTipoCultivo.text.toString(),
                unidadVenta = editUnidadVenta.text.toString(),
                fechaCosecha = editFechaCosecha.text.toString(),
                certificacion = editCertificacion.text.toString(),
                regionOrigen = editRegionOrigen.text.toString(),
                categoria = editCategoria.text.toString()
            )

            if (producto.nombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val productos = Producto.cargarProductosDesdePrefs(prefs, usuarioActual)

            // Si es edición, reemplazar el producto antiguo
            productoExistente?.let {
                productos.removeIf { p -> p.nombre == it.nombre }
            }

            productos.add(producto)
            Producto.guardarProductosEnPrefs(prefs, usuarioActual, productos)

            // Guardar imagen si hay
            uriImagen?.let {
                prefs.edit().putString("imagen_producto_${usuarioActual}_${producto.nombre}", it.toString()).apply()
            } ?: run {
                // Si no se seleccionó una nueva imagen y es edición, mantener la anterior
                if (productoExistente != null) {
                    val uriAnterior = prefs.getString("imagen_producto_${usuarioActual}_${productoExistente!!.nombre}", null)
                    uriAnterior?.let {
                        prefs.edit().putString("imagen_producto_${usuarioActual}_${producto.nombre}", it).apply()
                    }
                    // Eliminar la imagen vieja asociada al nombre anterior si cambió el nombre
                    if (producto.nombre != productoExistente!!.nombre) {
                        prefs.edit().remove("imagen_producto_${usuarioActual}_${productoExistente!!.nombre}").apply()
                    }
                }
            }

            startActivity(Intent(this, YourStoreActivity::class.java))
            finish()
        }

        buttonVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosProducto(producto: Producto) {
        editNombre.setText(producto.nombre)
        editDescripcion.setText(producto.descripcion)
        editPrecio.setText(producto.precio)
        editTipoCultivo.setText(producto.tipoCultivo)
        editUnidadVenta.setText(producto.unidadVenta)
        editFechaCosecha.setText(producto.fechaCosecha)
        editCertificacion.setText(producto.certificacion)
        editRegionOrigen.setText(producto.regionOrigen)
        editCategoria.setText(producto.categoria)

        val uriStr = prefs.getString("imagen_producto_${usuarioActual}_${producto.nombre}", null)
        if (!uriStr.isNullOrEmpty()) {
            uriImagen = Uri.parse(uriStr)
            imageViewProducto.setImageURI(uriImagen)
        } else {
            imageViewProducto.setImageResource(R.drawable.imagen_producto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            uriImagen = data?.data
            imageViewProducto.setImageURI(uriImagen)
        }
    }
}
