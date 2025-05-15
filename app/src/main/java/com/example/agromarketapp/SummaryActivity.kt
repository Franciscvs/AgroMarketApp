package com.example.agromarketapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SummaryActivity : AppCompatActivity() {

    private lateinit var textTotalUsuarios: TextView
    private lateinit var textTotalTiendas: TextView
    private lateinit var textTotalProductos: TextView
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        textTotalUsuarios = findViewById(R.id.textTotalUsuarios)
        textTotalTiendas = findViewById(R.id.textTotalTiendas)
        textTotalProductos = findViewById(R.id.textTotalProductos)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)

        // Cargar usuarios
        val usuarios = Usuario.cargarUsuariosDesdePrefs(prefs)
        val totalUsuarios = usuarios.size
        textTotalUsuarios.text = "Total de usuarios: $totalUsuarios"

        // Contar tiendas y productos
        var totalTiendas = 0
        var totalProductos = 0

        for (usuario in usuarios) {
            val tiendaJson = prefs.getString("tienda_${usuario.usuario}", null)
            if (!tiendaJson.isNullOrEmpty()) {
                totalTiendas++
            }

            val productos = Producto.cargarProductosDesdePrefs(prefs, usuario.usuario)
            totalProductos += productos.size
        }

        textTotalTiendas.text = "Total de tiendas: $totalTiendas"
        textTotalProductos.text = "Total de productos: $totalProductos"

        buttonVolver.setOnClickListener {
            finish()
        }

    }
}
