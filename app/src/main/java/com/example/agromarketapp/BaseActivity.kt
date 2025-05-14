package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val botonBorrarTodo = findViewById<Button>(R.id.buttonBorrarTodo)
        botonBorrarTodo.setOnClickListener {
            val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
            prefs.edit().clear().apply()
            Toast.makeText(this, "Todos los datos han sido borrados", Toast.LENGTH_LONG).show()

            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val botonResumen: Button = findViewById(R.id.buttonResumen)
        botonResumen.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        val btnCrearCuenta = findViewById<Button>(R.id.button1)
        val btnIniciarSesion = findViewById<Button>(R.id.button2)

        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}