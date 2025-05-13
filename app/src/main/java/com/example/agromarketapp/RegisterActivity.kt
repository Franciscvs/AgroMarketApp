package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newUsername = findViewById<EditText>(R.id.editTextText1)
        val names = findViewById<EditText>(R.id.editTextText2)
        val lastNames = findViewById<EditText>(R.id.editTextText3)
        val email = findViewById<EditText>(R.id.editTextText4)
        val password = findViewById<EditText>(R.id.editTextTextPassword1)
        val confirmPassword = findViewById<EditText>(R.id.editTextTextPassword2)

        val btnAtras = findViewById<Button>(R.id.button1)
        val btnCrearCuenta = findViewById<Button>(R.id.button2)

        btnCrearCuenta.setOnClickListener {
            val usuario = newUsername.text.toString().trim()
            val nombres = names.text.toString().trim()
            val apellidos = lastNames.text.toString().trim()
            val correo = email.text.toString().trim()
            val contrasena = password.text.toString()
            val confirmar = confirmPassword.text.toString()

            if (usuario.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
            val stringGuardado = prefs.getString("usuarios", null)
            val listaUsuarios = obtenerUsuariosGuardados(stringGuardado)

            val yaExiste = listaUsuarios.any { it.usuario == usuario || it.correo == correo }
            if (yaExiste) {
                Toast.makeText(this, "El usuario o correo ya existe.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(usuario, nombres, apellidos, correo, contrasena)
            listaUsuarios.add(nuevoUsuario)

            val nuevoString = serializarUsuarios(listaUsuarios)
            prefs.edit().putString("usuarios", nuevoString).apply()
            prefs.edit().putString("usuario_actual", usuario).apply()  // <- ESTA LÍNEA ES CLAVE


            Toast.makeText(this, "Cuenta creada exitosamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnAtras.setOnClickListener {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
        }
    }

    private fun obtenerUsuariosGuardados(prefString: String?): MutableList<Usuario> {
        val lista = mutableListOf<Usuario>()
        if (!prefString.isNullOrEmpty()) {
            val registros = prefString.split(";")
            for (r in registros) {
                val campos = r.split("|")
                if (campos.size == 5) {
                    lista.add(Usuario(campos[0], campos[1], campos[2], campos[3], campos[4]))
                }
            }
        }
        return lista
    }

    private fun serializarUsuarios(usuarios: List<Usuario>): String {
        return usuarios.joinToString(";") { "${it.usuario}|${it.nombres}|${it.apellidos}|${it.correo}|${it.contrasena}" }
    }

}