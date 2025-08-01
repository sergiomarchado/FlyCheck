package com.sergiom.flycheck.ui.screens.a_welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.util.LOGO_LETTERS_COLOR

@Composable
fun HomeScreen(
    onGoCustomCheckList: () -> Unit,
    onGoPredefinedCheckList: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource( R.string.app_name) + " ✈️",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LOGO_LETTERS_COLOR
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_screen_maintext),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onGoCustomCheckList, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_screen_button_customchecklist))
            }
        }
    }

}

