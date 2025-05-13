package com.example.agromarketapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String

    private lateinit var nombre: String
    private lateinit var descripcion: String
    private var precio: Double = 0.0
    private lateinit var tipoCultivo: String
    private lateinit var unidadVenta: String
    private lateinit var fechaCosecha: String
    private lateinit var certificacion: String
    private lateinit var regionOrigen: String
    private var imagenUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        // Recibir datos del producto
        nombre = intent.getStringExtra("nombre") ?: ""
        descripcion = intent.getStringExtra("descripcion") ?: ""
        precio = intent.getDoubleExtra("precio", 0.0)
        tipoCultivo = intent.getStringExtra("tipoCultivo") ?: ""
        unidadVenta = intent.getStringExtra("unidadVenta") ?: ""
        fechaCosecha = intent.getStringExtra("fechaCosecha") ?: ""
        certificacion = intent.getStringExtra("certificacion") ?: ""
        regionOrigen = intent.getStringExtra("regionOrigen") ?: ""
        imagenUri = intent.getStringExtra("imagenUri")

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodos)
        val buttonConfirmar = findViewById<Button>(R.id.buttonConfirmar)
        val buttonVolver = findViewById<Button>(R.id.buttonVolver)
        val buttonCerrarSesion = findViewById<Button>(R.id.buttonCerrarSesion)

        buttonConfirmar.setOnClickListener {
            val metodoSeleccionado = when (radioGroup.checkedRadioButtonId) {
                R.id.radioPSE -> "PSE"
                R.id.radioTarjeta -> "Tarjeta de crédito o débito"
                R.id.radioBilletera -> "Billetera Digital"
                else -> null
            }

            if (metodoSeleccionado != null) {
                // Guardar producto comprado
                val productoString = listOf(
                    nombre,
                    descripcion,
                    precio.toString(),
                    tipoCultivo,
                    unidadVenta,
                    fechaCosecha,
                    certificacion,
                    regionOrigen,
                    imagenUri ?: ""
                ).joinToString("||")

                val claveCompras = "compras_$usuarioActual"
                val comprasPrevias = prefs.getString(claveCompras, null)
                val nuevasCompras = if (comprasPrevias.isNullOrEmpty()) {
                    productoString
                } else {
                    "$comprasPrevias;;$productoString"
                }
                prefs.edit().putString(claveCompras, nuevasCompras).apply()

                Toast.makeText(this, "Compra confirmada con $metodoSeleccionado", Toast.LENGTH_SHORT).show()

                // Redirigir a pantalla de confirmación
                val intent = Intent(this, PurchasedProductsActivity::class.java)
                intent.putExtra("compra_confirmada", true)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVolver.setOnClickListener {
            finish()
        }

        buttonCerrarSesion.setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, BaseActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}