package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editUsuario = findViewById<EditText>(R.id.editTextText1)
        val editPassword = findViewById<EditText>(R.id.editTextTextPassword1)
        val btnAtras = findViewById<Button>(R.id.button1)
        val btnIngresar = findViewById<Button>(R.id.button2)
        val btnRecuperarCorreo = findViewById<Button>(R.id.button3)
        val btnCrearCuenta = findViewById<Button>(R.id.button4)

        btnIngresar.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()
            val contrasena = editPassword.text.toString()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa usuario y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
            val stringUsuarios = prefs.getString("usuarios", null)
            val usuarios = obtenerUsuariosGuardados(stringUsuarios)

            val usuarioValido = usuarios.find { it.usuario == usuario && it.contrasena == contrasena }

            if (usuarioValido != null) {
                prefs.edit().putString("usuario_actual", usuario).apply() // ← importante
                Toast.makeText(this, "Ingreso exitoso. Bienvenido ${usuarioValido.nombres}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
            }
        }

        btnAtras.setOnClickListener {
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnRecuperarCorreo.setOnClickListener {
            startActivity(Intent(this, EmailActivity::class.java))
        }

        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
}