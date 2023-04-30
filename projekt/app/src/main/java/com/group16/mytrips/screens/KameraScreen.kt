package com.group16.mytrips.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group16.mytrips.R


@Composable
fun CamScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Location Name",
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)

        )
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_dummykamera),
                contentDescription = "My Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()

            )
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_dummygalerie),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_dummykameraknopf),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Box(modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center)
            {
                Image(painter = painterResource(id = R.drawable.ic_dummy_front_back_swap),
                contentDescription = null,
                modifier = Modifier.size(80.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ZeigHer() {
    CamScreen()
}