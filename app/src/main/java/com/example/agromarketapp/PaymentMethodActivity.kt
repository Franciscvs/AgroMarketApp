package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var buttonConfirmar: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        buttonConfirmar = findViewById(R.id.buttonConfirmar)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)
        val usuarioPropietario = intent.getStringExtra("usuario_propietario")
        val productoJson = intent.getStringExtra("producto_comprado")
        val producto = Gson().fromJson(productoJson, Producto::class.java)
        val listaCompras = ArrayList<Producto>()

        buttonConfirmar.setOnClickListener {
            if (usuarioActual != null && producto != null) {
                // Cargar lista de productos comprados del usuario actual
                val comprasJson = prefs.getString("productos_comprados_$usuarioActual", null)
                val tipoLista: java.lang.reflect.Type = object : TypeToken<MutableList<Producto>>() {}.type
                val listaCompras: MutableList<Producto> = if (comprasJson != null)
                    Gson().fromJson(comprasJson, tipoLista)
                else mutableListOf()

                listaCompras.add(producto)

                // Guardar lista actualizada
                prefs.edit().putString("productos_comprados_$usuarioActual", Gson().toJson(listaCompras)).apply()

                // Ir a pantalla de confirmación
                val intent = Intent(this, PurchasedProductsActivity::class.java)
                intent.putExtra("compra_realizada", true)
                startActivity(intent)
                finish()
            }
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
}
