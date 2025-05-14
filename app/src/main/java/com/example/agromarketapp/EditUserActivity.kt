package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class EditUserActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 1
    private lateinit var imageViewUsuario: ImageView
    private var imageUriSeleccionada: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // Referencias UI
        imageViewUsuario = findViewById(R.id.imageViewUsuario)
        val botonSeleccionarImagen = findViewById<Button>(R.id.buttonSeleccionarImagen)
        val botonGuardar = findViewById<Button>(R.id.buttonGuardar)

        // Cargar imagen guardada (si existe)
        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)
        if (usuarioActual != null) {
            val uriGuardada = prefs.getString("imagen_usuario_$usuarioActual", null)
            if (!uriGuardada.isNullOrEmpty()) {
                imageViewUsuario.setImageURI(Uri.parse(uriGuardada))
            }
        }

        // Selección de imagen
        botonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // Guardar datos (imagen incluida)
        botonGuardar.setOnClickListener {
            val editor = prefs.edit()
            if (usuarioActual != null && imageUriSeleccionada != null) {
                editor.putString("imagen_usuario_$usuarioActual", imageUriSeleccionada.toString())
            }
            // Aquí también deberías guardar otros campos si los estás editando
            editor.apply()
            finish() // vuelve atrás después de guardar
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                imageUriSeleccionada = uri
                imageViewUsuario.setImageURI(uri)
            }
        }
    }
}
