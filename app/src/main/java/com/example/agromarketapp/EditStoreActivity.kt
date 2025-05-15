package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class EditStoreActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 1
    private lateinit var imageViewTienda: ImageView
    private var imageUriSeleccionada: Uri? = null

    private lateinit var editNombre: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var editUbicacion: EditText
    private lateinit var editHorario: EditText
    private lateinit var editContacto: EditText
    private lateinit var editCategoria: EditText
    private lateinit var buttonVolver: Button
    private lateinit var buttonGuardar: Button
    private lateinit var buttonSeleccionarImagen: Button

    private lateinit var usuarioActual: String
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_store)

        // Referencias UI
        imageViewTienda = findViewById(R.id.imageViewTienda)
        editNombre = findViewById(R.id.editTextNombreTienda)
        editDescripcion = findViewById(R.id.editTextDescripcionTienda)
        editUbicacion = findViewById(R.id.editTextUbicacion)
        editHorario = findViewById(R.id.editTextHorario)
        editContacto = findViewById(R.id.editTextContacto)
        editCategoria = findViewById(R.id.editTextCategoria)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonGuardar = findViewById(R.id.buttonGuardarTienda)
        buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagenTienda)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", null) ?: ""

        // Cargar tienda existente si existe
        val tiendaJson = prefs.getString("tienda_$usuarioActual", null)
        tiendaJson?.let {
            val tienda = Gson().fromJson(it, Tienda::class.java)
            editNombre.setText(tienda.nombre)
            editDescripcion.setText(tienda.descripcion)
            editUbicacion.setText(tienda.ubicacion)
            editHorario.setText(tienda.horario)
            editContacto.setText(tienda.contacto)
            editCategoria.setText(tienda.categoria)
        }

        // Cargar imagen guardada
        val uriStr = prefs.getString("imagen_tienda_$usuarioActual", null)
        if (!uriStr.isNullOrEmpty()) {
            imageViewTienda.setImageURI(Uri.parse(uriStr))
        }

        // Selección de imagen
        buttonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        buttonVolver.setOnClickListener {
            startActivity(Intent(this, YourStoreActivity::class.java))
        }

        // Guardar tienda
        buttonGuardar.setOnClickListener {
            val tienda = Tienda(
                nombre = editNombre.text.toString(),
                descripcion = editDescripcion.text.toString(),
                ubicacion = editUbicacion.text.toString(),
                horario = editHorario.text.toString(),
                contacto = editContacto.text.toString(),
                categoria = editCategoria.text.toString()
            )

            val editor = prefs.edit()
            editor.putString("tienda_$usuarioActual", Gson().toJson(tienda))

            // Guardar imagen si hay nueva
            imageUriSeleccionada?.let {
                editor.putString("imagen_tienda_$usuarioActual", it.toString())
            }

            editor.apply()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                imageUriSeleccionada = uri
                imageViewTienda.setImageURI(uri)
            }
        }
    }
}
