package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class OtherStoresActivity : AppCompatActivity() {

    private lateinit var contenedorTiendas: LinearLayout
    private lateinit var editBuscarTienda: EditText
    private lateinit var buttonBuscarTienda: Button
    private lateinit var spinnerCategoriasTiendas: Spinner
    private lateinit var buttonFiltrarCategoriaTiendas: Button
    private lateinit var buttonLimpiarFiltros: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var todasLasTiendas = mutableListOf<Pair<Tienda, String>>() // Tienda + usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_stores)

        // Referencias UI
        contenedorTiendas = findViewById(R.id.contenedorTiendas)
        editBuscarTienda = findViewById(R.id.editBuscarTienda)
        buttonBuscarTienda = findViewById(R.id.buttonBuscarTienda)
        spinnerCategoriasTiendas = findViewById(R.id.spinnerCategoriasTiendas)
        buttonFiltrarCategoriaTiendas = findViewById(R.id.buttonFiltrarCategoriaTiendas)
        buttonLimpiarFiltros = findViewById(R.id.buttonLimpiarFiltros)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        cargarTodasLasTiendas()
        mostrarTiendas(todasLasTiendas)

        // Buscar por nombre o categoría
        buttonBuscarTienda.setOnClickListener {
            val filtro = editBuscarTienda.text.toString().trim().lowercase()
            val resultado = if (filtro.isEmpty()) {
                todasLasTiendas
            } else {
                todasLasTiendas.filter {
                    it.first.nombre.lowercase().contains(filtro) ||
                            it.first.categoria.lowercase().contains(filtro)
                }
            }
            mostrarTiendas(resultado)
        }

        // Spinner con categorías de tienda
        val categoriasTiendas = todasLasTiendas.map { it.first.categoria }.distinct().sorted()
        val adaptadorTiendas = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Todas") + categoriasTiendas)
        adaptadorTiendas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoriasTiendas.adapter = adaptadorTiendas

        buttonFiltrarCategoriaTiendas.setOnClickListener {
            val seleccion = spinnerCategoriasTiendas.selectedItem.toString()
            val resultado = if (seleccion == "Todas") {
                todasLasTiendas
            } else {
                todasLasTiendas.filter { it.first.categoria == seleccion }
            }
            mostrarTiendas(resultado)
        }

        buttonLimpiarFiltros.setOnClickListener {
            editBuscarTienda.setText("")
            spinnerCategoriasTiendas.setSelection(0)
            mostrarTiendas(todasLasTiendas)
        }

        buttonVolver.setOnClickListener {
            finish()
        }

        buttonCerrarSesion.setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun cargarTodasLasTiendas() {
        todasLasTiendas.clear()
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)
        for (usuario in listaUsuarios) {
            if (usuario.usuario == usuarioActual) continue
            val tiendaJson = prefs.getString("tienda_${usuario.usuario}", null)
            if (!tiendaJson.isNullOrEmpty()) {
                val tienda = Gson().fromJson(tiendaJson, Tienda::class.java)
                todasLasTiendas.add(Pair(tienda, usuario.usuario))
            }
        }
    }

    private fun mostrarTiendas(lista: List<Pair<Tienda, String>>) {
        contenedorTiendas.removeAllViews()

        if (lista.isEmpty()) {
            val texto = TextView(this).apply {
                text = "No se encontraron tiendas."
                textSize = 18f
                gravity = android.view.Gravity.CENTER
            }
            contenedorTiendas.addView(texto)
            return
        }

        for ((tienda, usuario) in lista) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 24, 24, 24)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 16)
                layoutParams = params
                background = getDrawable(R.drawable.card_background)
            }

            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200)
                val uri = prefs.getString("imagen_tienda_$usuario", null)
                if (!uri.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uri))
                } else {
                    setImageResource(R.drawable.imagen_tienda)
                }
            }

            val datos = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val nombre = TextView(this).apply {
                text = tienda.nombre
                textSize = 18f
            }

            val categoria = TextView(this).apply {
                text = "Categoría: ${tienda.categoria}"
                textSize = 16f
            }

            val botonVer = Button(this).apply {
                text = "Ver tienda"
                setOnClickListener {
                    val intent = Intent(this@OtherStoresActivity, ViewStoreActivity::class.java)
                    intent.putExtra("usuario_propietario", usuario)
                    startActivity(intent)
                }
            }

            datos.addView(nombre)
            datos.addView(categoria)
            datos.addView(botonVer)

            layout.addView(imageView)
            layout.addView(datos)

            contenedorTiendas.addView(layout)
        }
    }
}
