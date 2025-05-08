package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditStoreActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var uriImagen: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_store)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        imageView = findViewById(R.id.imageViewTienda)

        val uri = prefs.getString("imagen_tienda_$usuarioActual", null)
        if (!uri.isNullOrEmpty()) {
            uriImagen = Uri.parse(uri)
            imageView.setImageURI(uriImagen)
        }

        val tiendaStr = prefs.getString("tienda_$usuarioActual", null)
        val campos = tiendaStr?.split("|") ?: listOf("", "", "", "", "", "")

        val editNombre = findViewById<EditText>(R.id.editTextNombreTienda)
        val editDescripcion = findViewById<EditText>(R.id.editTextDescripcionTienda)
        val editUbicacion = findViewById<EditText>(R.id.editTextUbicacion)
        val editHorario = findViewById<EditText>(R.id.editTextHorario)
        val editContacto = findViewById<EditText>(R.id.editTextContacto)
        val editCategoria = findViewById<EditText>(R.id.editTextCategoria)

        editNombre.setText(campos.getOrNull(0) ?: "")
        editDescripcion.setText(campos.getOrNull(1) ?: "")
        editUbicacion.setText(campos.getOrNull(2) ?: "")
        editHorario.setText(campos.getOrNull(3) ?: "")
        editContacto.setText(campos.getOrNull(4) ?: "")
        editCategoria.setText(campos.getOrNull(5) ?: "")

        findViewById<Button>(R.id.buttonSeleccionarImagenTienda).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 109)
        }

        findViewById<Button>(R.id.buttonGuardarTienda).setOnClickListener {
            val nuevaTienda = listOf(
                editNombre.text.toString().trim(),
                editDescripcion.text.toString().trim(),
                editUbicacion.text.toString().trim(),
                editHorario.text.toString().trim(),
                editContacto.text.toString().trim(),
                editCategoria.text.toString().trim()
            ).joinToString("|")

            prefs.edit().putString("tienda_$usuarioActual", nuevaTienda).apply()

            uriImagen?.let {
                prefs.edit().putString("imagen_tienda_$usuarioActual", it.toString()).apply()
            }

            Toast.makeText(this, "Tienda actualizada correctamente", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
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
        if (requestCode == 109 && resultCode == RESULT_OK) {
            uriImagen = data?.data
            imageView.setImageURI(uriImagen)

            uriImagen?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        }
    }
}
