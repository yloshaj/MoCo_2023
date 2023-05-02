package com.group16.mytrips.screens

import androidx.compose.foundation.layout.Column

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    BottomAppBar() {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationRailItem(selected = selected, modifier = Modifier.weight(1f),
                onClick = { onItemClick(item) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = item.icon, contentDescription = null)
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
