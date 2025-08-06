package com.sergiom.flycheck.ui.screens.a_welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    // Usamos LaunchEffect para ejecutar el delay una sola vez
    LaunchedEffect(true) {
        delay(1500)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(R.drawable.ic_icon_splash),
                contentDescription = stringResource(R.string.splash_screen_contentdescription))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.splash_screen_loadingtext),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

