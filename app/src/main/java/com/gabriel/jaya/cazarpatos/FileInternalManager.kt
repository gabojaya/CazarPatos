package com.gabriel.jaya.cazarpatos

import android.app.Activity
import android.content.Context
import android.util.Log
import java.io.FileNotFoundException

class FileInternalManager(val actividad: Activity) : FileHandler {
    override fun SaveInformation(datosAGrabar:Pair<String,String>) {
        val texto = datosAGrabar.first + System.lineSeparator() + datosAGrabar.second
        actividad.openFileOutput("fichero.txt", Context.MODE_PRIVATE).bufferedWriter().use { fos ->
            fos.write(texto)
        }
    }

    override fun ReadInformation():Pair<String,String> {
        return try {
            actividad.openFileInput("fichero.txt").bufferedReader().use {
                val datoLeido = it.readText()
                if (datoLeido.isEmpty()) {
                    return "" to ""
                }
                val textArray = datoLeido.split(System.lineSeparator())

                val email = textArray.getOrElse(0) { "" }
                val clave = textArray.getOrElse(1) { "" }
                return email to clave
            }
        } catch (e: FileNotFoundException) {

            Log.e("FileInternalManager", "Fichero no encontrado, se creará al guardar.")
            "" to ""
        }
    }
}