// ui/screens/a_welcome/HomeScreen.kt
package com.sergiom.flycheck.ui.screens.a_welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.common.LOGO_LETTERS_COLOR

@Composable
fun HomeScreen(
    onGoCustomCheckList: () -> Unit,
    onOpenFromDevice: () -> Unit,
    onOpenFromLocal: () -> Unit,
    onOpenCommunity: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.app_name) + " ✈️",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LOGO_LETTERS_COLOR
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_screen_maintext),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(32.dp))

            // Crear nueva checklist (editor)
            Button(onClick = onGoCustomCheckList, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_screen_button_customchecklist))
            }

            Spacer(Modifier.height(12.dp))

            // Cargar de memoria privada (export Local)
            Button(onClick = onOpenFromLocal, modifier = Modifier.fillMaxWidth()) {
                Text("Checklist Guardadas en Local")
            }

            Spacer(Modifier.height(12.dp))

            // Abrir archivo externo (Downloads, etc.)
            OutlinedButton(onClick = onOpenFromDevice, modifier = Modifier.fillMaxWidth()) {
                Text("Abrir desde archivo (.zip)")
            }

            Spacer(Modifier.height(12.dp))

            // Comunidad (placeholder)
            OutlinedButton(onClick = onOpenCommunity, modifier = Modifier.fillMaxWidth()) {
                Text("Checklists de la comunidad")
            }
        }
    }
}
