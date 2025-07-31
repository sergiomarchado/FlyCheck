package com.sergiom.flycheck.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R


@Composable
fun CheckListHeader(
    name: String,
    model: String,
    airline: String,
    includeLogo: Boolean
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp,bottom = 12.dp),
        horizontalArrangement = Arrangement.Center
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = name, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)

            Spacer(modifier = Modifier.height(12.dp))

            Row (verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth())

            {
                Text(text = airline, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                if(includeLogo){
                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_ryanair),
                        contentDescription = stringResource(R.string.checklistheader_contentdescription),
                        modifier = Modifier
                            .height(32.dp)
                            .width(32.dp)
                    )
                }
                Text(text = model, style = MaterialTheme.typography.titleMedium , color = MaterialTheme.colorScheme.onPrimary)
            }



        }
    }

}