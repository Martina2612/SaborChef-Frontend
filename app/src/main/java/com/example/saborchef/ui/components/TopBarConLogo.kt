package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.saborchef.R
import com.example.saborchef.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarConLogo() {
    TopAppBar(
        title = {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_topbar),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )
            }
        },
        navigationIcon = {},
        actions = { Spacer(modifier = Modifier.width(48.dp)) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Orange),
        modifier = Modifier.height(80.dp)
    )
}

