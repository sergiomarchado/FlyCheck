package com.sergiom.flycheck.ui.utils

import android.net.Uri
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object JsonUtils {
    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(Uri::class, UriSerializer)
        }
    }
}