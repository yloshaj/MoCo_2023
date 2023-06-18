package com.group16.mytrips.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.group16.mytrips.data.BottomNavigationItem


@Composable
fun BottomNavigationBar (
    items: List<BottomNavigationItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavigationItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val containerColor = Color(240,240,240)
    BottomAppBar(containerColor = containerColor, modifier = Modifier.shadow(30.dp)) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationRailItem(selected = selected, modifier = Modifier.weight(1f), colors = NavigationRailItemDefaults.colors(indicatorColor = containerColor),
                onClick = { onItemClick(item) },
                icon = {
                    var itemColor by remember {
                        mutableStateOf(Color.LightGray)
                    }
                    if (selected) itemColor = Color(59,88,145) else itemColor = Color.LightGray
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = item.icon, contentDescription = null, tint = itemColor)
                        if (selected)
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                    }
                })

        }
    }

}
