package com.example.agromarketapp

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Usuario(
    var usuario: String,
    var nombres: String,
    var apellidos: String,
    var correo: String,
    var contrasena: String
) {
    companion object {
        // Cargar la lista de usuarios desde SharedPreferences
        fun cargarUsuariosDesdePrefs(prefs: SharedPreferences): MutableList<Usuario> {
            val json = prefs.getString("lista_usuarios", null)
            val tipoLista = object : TypeToken<MutableList<Usuario>>() {}.type
            return if (json != null) Gson().fromJson(json, tipoLista) else mutableListOf()
        }

        // Guardar la lista de usuarios en SharedPreferences (opcional)
        fun guardarUsuariosEnPrefs(prefs: SharedPreferences, lista: MutableList<Usuario>) {
            val editor = prefs.edit()
            val json = Gson().toJson(lista)
            editor.putString("lista_usuarios", json)
            editor.apply()
        }
    }
}
