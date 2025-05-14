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

    private val REQUEST_IMAGE_PICK = 1
    private lateinit var imageViewProducto: ImageView
    private var imageUriSeleccionada: Uri? = null

    private lateinit var editNombre: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var editPrecio: EditText
    private lateinit var editTipoCultivo: EditText
    private lateinit var editUnidadVenta: EditText
    private lateinit var editFechaCosecha: EditText
    private lateinit var editCertificacion: EditText
    private lateinit var editRegionOrigen: EditText

    private lateinit var botonGuardar: Button
    private lateinit var botonSeleccionarImagen: Button

    private var nombreOriginal: String = ""

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

        botonGuardar = findViewById(R.id.buttonGuardarProducto)
        botonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagenProducto)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        // Cargar producto recibido
        val productoJson = intent.getStringExtra("producto_editar")
        val producto = if (!productoJson.isNullOrEmpty())
            Gson().fromJson(productoJson, Producto::class.java)
        else null

        producto?.let {
            nombreOriginal = it.nombre
            editNombre.setText(it.nombre)
            editDescripcion.setText(it.descripcion)
            editPrecio.setText(it.precio)
            editTipoCultivo.setText(it.tipoCultivo)
            editUnidadVenta.setText(it.unidadVenta)
            editFechaCosecha.setText(it.fechaCosecha)
            editCertificacion.setText(it.certificacion)
            editRegionOrigen.setText(it.regionOrigen)

            if (!usuarioActual.isNullOrEmpty()) {
                val uriStr = prefs.getString("imagen_producto_${usuarioActual}_${it.nombre}", null)
                if (!uriStr.isNullOrEmpty()) {
                    imageViewProducto.setImageURI(Uri.parse(uriStr))
                }
            }
        }

        botonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        botonGuardar.setOnClickListener {
            val nuevoNombre = editNombre.text.toString().trim()

            if (usuarioActual != null && nuevoNombre.isNotEmpty()) {
                val nuevaImagenUri = imageUriSeleccionada?.toString()

                // Guardar nueva imagen si fue seleccionada
                if (nuevaImagenUri != null) {
                    prefs.edit().putString("imagen_producto_${usuarioActual}_$nuevoNombre", nuevaImagenUri).apply()
                    if (nuevoNombre != nombreOriginal) {
                        prefs.edit().remove("imagen_producto_${usuarioActual}_$nombreOriginal").apply()
                    }
                } else if (nuevoNombre != nombreOriginal) {
                    // Mover imagen existente a la nueva clave
                    val antiguaUri = prefs.getString("imagen_producto_${usuarioActual}_$nombreOriginal", null)
                    if (!antiguaUri.isNullOrEmpty()) {
                        prefs.edit().putString("imagen_producto_${usuarioActual}_$nuevoNombre", antiguaUri).apply()
                        prefs.edit().remove("imagen_producto_${usuarioActual}_$nombreOriginal").apply()
                    }
                }

                // Crear nuevo producto con los datos ingresados
                val nuevoProducto = Producto(
                    nombre = nuevoNombre,
                    descripcion = editDescripcion.text.toString(),
                    precio = editPrecio.text.toString(),
                    tipoCultivo = editTipoCultivo.text.toString(),
                    unidadVenta = editUnidadVenta.text.toString(),
                    fechaCosecha = editFechaCosecha.text.toString(),
                    certificacion = editCertificacion.text.toString(),
                    regionOrigen = editRegionOrigen.text.toString()
                )

                // Actualizar la lista de productos
                val productos = Producto.cargarProductosDesdePrefs(prefs, usuarioActual)
                val index = productos.indexOfFirst { it.nombre == nombreOriginal }

                if (index != -1) {
                    productos[index] = nuevoProducto
                } else {
                    productos.add(nuevoProducto)
                }

                Producto.guardarProductosEnPrefs(prefs, usuarioActual, productos)

                finish()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                imageUriSeleccionada = uri
                imageViewProducto.setImageURI(uri)
            }
        }
    }
}
