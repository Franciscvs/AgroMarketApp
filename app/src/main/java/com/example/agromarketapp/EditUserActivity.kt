package com.example.agromarketapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class EditUserActivity : AppCompatActivity() {

    private lateinit var imageViewUsuario: ImageView
    private lateinit var editUsuario: EditText
    private lateinit var editNombres: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editContrasena: EditText
    private lateinit var buttonSeleccionarImagen: Button
    private lateinit var buttonGuardar: Button
    private lateinit var buttonVolver: Button

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var uriImagen: Uri? = null

    private val REQUEST_IMAGE_PICK = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // Referencias UI
        imageViewUsuario = findViewById(R.id.imageViewUsuario)
        editUsuario = findViewById(R.id.editTextUsuario)
        editNombres = findViewById(R.id.editTextNombres)
        editApellidos = findViewById(R.id.editTextApellidos)
        editCorreo = findViewById(R.id.editTextCorreo)
        editContrasena = findViewById(R.id.editTextPasswordConfirm)
        buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagen)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonVolver = findViewById(R.id.buttonVolver)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        val lista = Usuario.cargarUsuariosDesdePrefs(prefs)
        val usuario = lista.find { it.usuario == usuarioActual }

        usuario?.let {
            editUsuario.setText(it.usuario)
            editNombres.setText(it.nombres)
            editApellidos.setText(it.apellidos)
            editCorreo.setText(it.correo)
            editContrasena.setText(it.contrasena)

            val uriStr = prefs.getString("imagen_usuario_${it.usuario}", null)
            if (!uriStr.isNullOrEmpty()) {
                uriImagen = Uri.parse(uriStr)
                imageViewUsuario.setImageURI(uriImagen)
            } else {
                imageViewUsuario.setImageResource(R.drawable.imagen_usuario)
            }
        }

        buttonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        buttonGuardar.setOnClickListener {
            val nuevoUsuario = editUsuario.text.toString().trim()
            val nombres = editNombres.text.toString().trim()
            val apellidos = editApellidos.text.toString().trim()
            val correo = editCorreo.text.toString().trim()
            val contrasena = editContrasena.text.toString()

            if (nuevoUsuario.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listaActual = Usuario.cargarUsuariosDesdePrefs(prefs).toMutableList()

            // Verificar si el nuevo nombre de usuario ya está en uso (por otro usuario)
            if (nuevoUsuario != usuarioActual && listaActual.any { it.usuario == nuevoUsuario }) {
                Toast.makeText(this, "Ese nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar el objeto y reemplazar en la lista
            listaActual.removeIf { it.usuario == usuarioActual }
            listaActual.add(Usuario(nuevoUsuario, nombres, apellidos, correo, contrasena))
            Usuario.guardarUsuariosEnPrefs(prefs, listaActual)

            // Transferir imagen si cambió el usuario
            if (nuevoUsuario != usuarioActual) {
                val imagenAntigua = prefs.getString("imagen_usuario_$usuarioActual", null)
                imagenAntigua?.let {
                    prefs.edit().putString("imagen_usuario_$nuevoUsuario", it).apply()
                    prefs.edit().remove("imagen_usuario_$usuarioActual").apply()
                }
            }

            // Guardar nueva imagen si seleccionó otra
            uriImagen?.let {
                prefs.edit().putString("imagen_usuario_$nuevoUsuario", it.toString()).apply()
            }

            // Actualizar usuario actual
            prefs.edit().putString("usuario_actual", nuevoUsuario).apply()

            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonVolver.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            uriImagen = data?.data
            imageViewUsuario.setImageURI(uriImagen)
        }
    }
}
