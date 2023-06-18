package com.group16.mytrips.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(3f)
            .background(Color.LightGray))

        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.SpaceEvenly) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    tint = Color.Unspecified,
                    painter = painterResource(id = R.drawable.ic_dummygalerievektor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
            )
            {
                Icon(
                    tint = Color.Unspecified,
                    painter = painterResource(id = R.drawable.kameraknopfv),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }
            Box(modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center)
            {
                Icon(
                    tint = Color.Unspecified,
                    painter = painterResource(id = R.drawable.ic_frontbackswapv),
                contentDescription = null,
                modifier = Modifier.size(60.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ZeigHer() {
    CamScreen()
}