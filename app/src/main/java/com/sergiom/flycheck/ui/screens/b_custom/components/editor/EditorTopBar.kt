package com.sergiom.flycheck.ui.screens.b_custom.components.editor


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    onBackClick: () -> Unit = {},
    onMenuOptionClick: (String) -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name) + " ✈️", color = MaterialTheme.colorScheme.primary) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Exportar PDF") },
                    onClick = {
                        expanded = false
                        onMenuOptionClick("export_pdf")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ajustes") },
                    onClick = {
                        expanded = false
                        onMenuOptionClick("settings")
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = Color.White
        )
    )
}


