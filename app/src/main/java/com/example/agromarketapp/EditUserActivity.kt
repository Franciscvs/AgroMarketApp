// EditUserActivity.kt
package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditUserActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var uriImagen: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""
        imageView = findViewById(R.id.imageViewUsuario)

        val uri = prefs.getString("imagen_${usuarioActual}", null)
        if (!uri.isNullOrEmpty()) {
            uriImagen = Uri.parse(uri)
            imageView.setImageURI(uriImagen)
        }

        val usuarios = prefs.getString("usuarios", null)?.split(";")?.map {
            val campos = it.split("|")
            Usuario(campos[0], campos[1], campos[2], campos[3], campos[4])
        }?.toMutableList() ?: mutableListOf()

        val usuario = usuarios.find { it.usuario == usuarioActual }

        val editUsuario = findViewById<EditText>(R.id.editTextUsuario)
        val editNombres = findViewById<EditText>(R.id.editTextNombres)
        val editApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val editCorreo = findViewById<EditText>(R.id.editTextCorreo)
        val editContrasena = findViewById<EditText>(R.id.editTextPasswordConfirm)

        usuario?.let {
            editUsuario.setText(it.usuario)
            editNombres.setText(it.nombres)
            editApellidos.setText(it.apellidos)
            editCorreo.setText(it.correo)
            editContrasena.setText(it.contrasena)
        }

        findViewById<Button>(R.id.buttonSeleccionarImagen).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 111)
        }

        findViewById<Button>(R.id.buttonGuardar).setOnClickListener {
            val nuevoUsuario = editUsuario.text.toString().trim()
            val nuevosDatos = Usuario(
                nuevoUsuario,
                editNombres.text.toString().trim(),
                editApellidos.text.toString().trim(),
                editCorreo.text.toString().trim(),
                editContrasena.text.toString().trim()
            )

            val actualizados = usuarios.map {
                if (it.usuario == usuarioActual) nuevosDatos else it
            }

            val nuevoString = actualizados.joinToString(";") {
                "${it.usuario}|${it.nombres}|${it.apellidos}|${it.correo}|${it.contrasena}"
            }

            // Guardar nuevos datos
            prefs.edit().putString("usuarios", nuevoString).apply()

            // Si se cambió el nombre del usuario, también actualizar imagen y sesión
            if (nuevoUsuario != usuarioActual) {
                val imagenUri = prefs.getString("imagen_${usuarioActual}", null)
                prefs.edit().remove("imagen_${usuarioActual}").apply()
                imagenUri?.let {
                    prefs.edit().putString("imagen_${nuevoUsuario}", it).apply()
                }
                prefs.edit().putString("usuario_actual", nuevoUsuario).apply()
            }

            uriImagen?.let {
                prefs.edit().putString("imagen_${nuevoUsuario}", it.toString()).apply()
            }

            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
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
        if (requestCode == 111 && resultCode == RESULT_OK) {
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
