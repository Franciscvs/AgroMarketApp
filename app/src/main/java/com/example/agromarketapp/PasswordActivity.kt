package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAtras = findViewById<Button>(R.id.button1)
        val btnContrasenaCambiada = findViewById<Button>(R.id.button2)
        val nuevaContrasena = findViewById<EditText>(R.id.editTextTextPassword1)
        val confirmarContrasena = findViewById<EditText>(R.id.editTextTextPassword2)

        val correoRecibido = intent.getStringExtra("correo")
        val textViewCorreo = findViewById<TextView>(R.id.textViewCorreo)
        textViewCorreo.text = "Correo: $correoRecibido"
        Toast.makeText(this, "Cambiando contraseña para: $correoRecibido", Toast.LENGTH_LONG).show()

        btnContrasenaCambiada.setOnClickListener {
            val nueva = nuevaContrasena.text.toString()
            val confirmar = confirmarContrasena.text.toString()

            if (nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
            val correoGuardado = prefs.getString("correo", null)

            if (correoRecibido == correoGuardado) {
                with(prefs.edit()) {
                    putString("contrasena", nueva)
                    apply()
                }
                Toast.makeText(this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error: El correo no coincide con ningún usuario registrado.", Toast.LENGTH_SHORT).show()
            }
        }

        btnAtras.setOnClickListener {
            val intent = Intent(this, EmailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}