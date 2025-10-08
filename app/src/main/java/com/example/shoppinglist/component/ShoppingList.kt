package com.example.shoppinglist.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController) {
    var items by remember { mutableStateOf(listOf<String>()) }

    var newItem by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // Filter item sesuai teks pencarian
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Input untuk menambah item baru
            ItemInput(
                text = newItem,
                onTextChange = { newItem = it },
                onAddItem = {
                    if (newItem.isNotBlank()) {
                        items = items + newItem.trim()
                        newItem = ""
                    }
                }
            )
            // Kolom pencarian
            SearchInput(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            //  tampil daftar belanja (hasil pencarian)
            ShoppingList(items = filteredItems)
        }
    }
}


@Composable
fun ShoppingList(items: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items, key = { it }) { item ->
            AnimatedShoppingListItem(item = item)
        }
    }
}

@Composable
fun AnimatedShoppingListItem(item: String) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
        delay(100)
        isVisible = true
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                expandVertically(animationSpec = tween(500))
    ) {
        ShoppingListItem(item)
    }
}

@Composable
fun ShoppingListItem(item: String) {
    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300), label = "bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300), label = "color"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 4.dp,
        animationSpec = tween(200), label = "elev"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isSelected = !isSelected },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(
            1.dp,
            if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon huruf depan
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, shape = CircleShape)
                    .background(
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Nama item
            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            // Icon status
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = if (isSelected) "Selected" else "Add to cart",
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}
