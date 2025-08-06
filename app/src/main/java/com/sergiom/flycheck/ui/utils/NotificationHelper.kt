package com.sergiom.flycheck.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.sergiom.flycheck.R

object NotificationHelper {

    // ID único del canal (debe coincidir con el usado en el builder)
    private const val CHANNEL_ID = "export_channel"

    // Nombre visible del canal (aparece en ajustes del sistema)
    private const val CHANNEL_NAME = "Exportaciones"

    // ID de notificación (puede ser fijo o dinámico si se muestran varias a la vez)
    private const val NOTIFICATION_ID = 101


    /**
     * Notificación enriquecida: muestra acciones ("Abrir" y "Compartir") directamente en la notificación.
     */
    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showExportWithActionsNotification(context: Context, fileName: String, fileUri: Uri) {
        createNotificationChannel(context)

        // --- Acción 1: Abrir carpeta de Descargas ---
        val openFolderIntent = Intent(Intent.ACTION_VIEW).apply {
            val downloadsUri =
                "content://com.android.externalstorage.documents/document/primary:Download".toUri()
            setDataAndType(downloadsUri, "vnd.android.document/directory")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Verificamos si existe alguna app que pueda manejar este intent
        val canOpenFolder = openFolderIntent.resolveActivity(context.packageManager) != null

        val openPendingIntent = if (canOpenFolder) {
            PendingIntent.getActivity(
                context,
                1,
                openFolderIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            null // No se agrega acción si no hay soporte
        }

        // --- Acción 2: Compartir archivo JSON ---
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            setType("application/json")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val sharePendingIntent = PendingIntent.getActivity(
            context,
            2,
            Intent.createChooser(shareIntent, context.getString(R.string.share_file_chooser_title)),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // --- Construcción de la notificación ---
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(context.getString(R.string.export_success))
            .setContentText(context.getString(R.string.export_saved_to_downloads))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_share,
                context.getString(R.string.action_share),
                sharePendingIntent
            )

        // Solo añadimos "Abrir carpeta" si el intent es válido
        if (openPendingIntent != null) {
            builder.addAction(
                R.drawable.ic_open,
                context.getString(R.string.open_downloads),
                openPendingIntent
            )
        }

        // --- Mostrar notificación ---
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }


    /**
     * Crea el canal de notificación si es necesario (solo Android 8+).
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones para archivos exportados"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}