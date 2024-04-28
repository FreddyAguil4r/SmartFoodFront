package com.example.smartfood.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseHelper {
    // Define la URL de conexión con los parámetros de Heroku
    private val url = "jdbc:postgresql://ec2-44-213-228-107.compute-1.amazonaws.com:5432/d7sbmqhkqionqb?sslmode=require&user=djejclknbwuslq&password=a4a6297780ecf179879e4f0df311e77665a482d1ffbc259041cb0641f61114fe"

    // Crea una variable para almacenar la conexión
    private var connection: Connection? = null

    // Crea un método para obtener la conexión
    fun getConnection(): Connection? {
        try {
            // Si la conexión es nula o está cerrada, la crea
            if (connection == null || connection!!.isClosed) {
                // Registra el driver de PostgreSQL
                Class.forName("org.postgresql.Driver")
                // Establece la conexión con la URL definida
                connection = DriverManager.getConnection(url)
            }
        } catch (e: ClassNotFoundException) {
            // Maneja la excepción si no se encuentra el driver
            e.printStackTrace()
        } catch (e: SQLException) {
            // Maneja la excepción si hay un error en la conexión
            e.printStackTrace()
        }
        // Devuelve la conexión o null si no se pudo crear
        return connection
    }

    // Crea un método para cerrar la conexión
    fun closeConnection() {
        try {
            // Si la conexión no es nula y está abierta, la cierra
            if (connection != null && !connection!!.isClosed) {
                connection!!.close()
            }
        } catch (e: SQLException) {
            // Maneja la excepción si hay un error al cerrar la conexión
            e.printStackTrace()
        }
    }
}