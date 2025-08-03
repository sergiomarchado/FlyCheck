package com.sergiom.flycheck

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 *  Punto de entrada de la aplicación FlyCheck.
 *
 * Esta clase extiende [Application] y está anotada con [@HiltAndroidApp],
 * lo que permite a Hilt generar y mantener el contenedor de dependencias a nivel de aplicación.
 *
 * Es esencial para que Dagger Hilt funcione correctamente en toda la app,
 * ya que desde aquí se configura el grafo de dependencias.
 */
@HiltAndroidApp
class FlyCheckApp: Application ()