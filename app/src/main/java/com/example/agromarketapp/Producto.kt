package com.example.agromarketapp

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUri: String? = null,
    val tipoCultivo: String = "",
    val unidadVenta: String = "",
    val fechaCosecha: String = "",
    val certificacion: String = "",
    val regionOrigen: String = ""
)