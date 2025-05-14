package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewProductsActivity : AppCompatActivity() {

    private lateinit var contenedorProductos: LinearLayout
    private lateinit var editBuscarProducto: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var spinnerCategorias: Spinner
    private lateinit var buttonFiltrarCategoria: Button
    private lateinit var buttonLimpiarFiltros: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String

    private var todosLosProductos = mutableListOf<Pair<Producto, String>>() // Producto + propietario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_products)

        // Referencias UI
        contenedorProductos = findViewById(R.id.contenedorProductos)
        editBuscarProducto = findViewById(R.id.editBuscarProducto)
        buttonBuscar = findViewById(R.id.buttonBuscar)
        spinnerCategorias = findViewById(R.id.spinnerCategorias)
        buttonFiltrarCategoria = findViewById(R.id.buttonFiltrarCategoria)
        buttonLimpiarFiltros = findViewById(R.id.buttonLimpiarFiltros)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        // Cargar productos globales
        cargarTodosLosProductos()
        mostrarProductos(todosLosProductos)

        // Buscar por nombre
        buttonBuscar.setOnClickListener {
            val filtro = editBuscarProducto.text.toString().trim().lowercase()
            val filtrados = if (filtro.isEmpty()) {
                todosLosProductos
            } else {
                todosLosProductos.filter {
                    it.first.nombre.lowercase().contains(filtro)
                }
            }
            mostrarProductos(filtrados)
        }

        // Cargar categorías al Spinner
        val categoriasDisponibles = todosLosProductos.map { it.first.categoria }.distinct().sorted()
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Todas") + categoriasDisponibles)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategorias.adapter = adaptador

        // Filtrar por categoría
        buttonFiltrarCategoria.setOnClickListener {
            val seleccion = spinnerCategorias.selectedItem.toString()
            val filtrados = if (seleccion == "Todas") {
                todosLosProductos
            } else {
                todosLosProductos.filter { it.first.categoria == seleccion }
            }
            mostrarProductos(filtrados)
        }

        // Limpiar filtros
        buttonLimpiarFiltros.setOnClickListener {
            editBuscarProducto.setText("")
            spinnerCategorias.setSelection(0)
            mostrarProductos(todosLosProductos)
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

    private fun cargarTodosLosProductos() {
        todosLosProductos.clear()
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)
        for (usuario in listaUsuarios) {
            val productos = Producto.cargarProductosDesdePrefs(prefs, usuario.usuario)
            for (producto in productos) {
                todosLosProductos.add(Pair(producto, usuario.usuario))
            }
        }
    }

    private fun mostrarProductos(productos: List<Pair<Producto, String>>) {
        contenedorProductos.removeAllViews()

        if (productos.isEmpty()) {
            val texto = TextView(this).apply {
                text = "No se encontraron productos."
                textSize = 18f
                gravity = android.view.Gravity.CENTER
            }
            contenedorProductos.addView(texto)
            return
        }

        for ((producto, propietario) in productos) {
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
                val uriStr = prefs.getString("imagen_producto_${propietario}_${producto.nombre}", null)
                if (!uriStr.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uriStr))
                } else {
                    setImageResource(R.drawable.imagen_producto)
                }
            }

            val datos = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val nombre = TextView(this).apply {
                text = producto.nombre
                textSize = 18f
            }

            val precio = TextView(this).apply {
                text = "Precio: ${producto.precio}"
                textSize = 16f
            }

            val botonVer = Button(this).apply {
                text = "Ver producto"
                setOnClickListener {
                    val intent = Intent(this@NewProductsActivity, ViewProductActivity::class.java)
                    intent.putExtra("producto", Gson().toJson(producto))
                    intent.putExtra("usuario_propietario", propietario)
                    startActivity(intent)
                }
            }

            datos.addView(nombre)
            datos.addView(precio)
            datos.addView(botonVer)

            layout.addView(imageView)
            layout.addView(datos)

            contenedorProductos.addView(layout)
        }
    }
}
