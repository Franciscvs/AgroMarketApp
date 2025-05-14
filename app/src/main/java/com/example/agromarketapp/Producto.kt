package com.example.agromarketapp

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val tipoCultivo: String,
    val unidadVenta: String,
    val fechaCosecha: String,
    val certificacion: String,
    val regionOrigen: String
) {
    companion object {
        fun cargarProductosDesdePrefs(prefs: SharedPreferences, usuario: String): MutableList<Producto> {
            val json = prefs.getString("productos_$usuario", null)
            val tipo = object : TypeToken<MutableList<Producto>>() {}.type
            return if (json != null) Gson().fromJson(json, tipo) else mutableListOf()
        }

        fun guardarProductosEnPrefs(prefs: SharedPreferences, usuario: String, productos: MutableList<Producto>) {
            val editor = prefs.edit()
            val json = Gson().toJson(productos)
            editor.putString("productos_$usuario", json)
            editor.apply()
        }
    }
}
