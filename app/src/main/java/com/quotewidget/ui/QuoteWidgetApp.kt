package com.quotewidget.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.quotewidget.data.Quote
import com.quotewidget.data.QuoteCategory
import com.quotewidget.data.QuoteRepository
import com.quotewidget.settings.AppSettings
import com.quotewidget.settings.SettingsDataStore
import com.quotewidget.settings.ThemeMode
import com.quotewidget.settings.WidgetFrequency
import com.quotewidget.widget.updateAllQuoteWidgets
import kotlinx.coroutines.launch

private enum class AppScreen {
    Main,
    Favorites,
    Settings
}

@Composable
fun QuoteWidgetApp() {
    val context = LocalContext.current
    val store = remember { SettingsDataStore(context.applicationContext) }
    val settings by store.settings.collectAsState(initial = AppSettings())
    val repository = remember { QuoteRepository() }

    LaunchedEffect(settings.widgetFrequency) {
        SettingsDataStore.scheduleWidgetUpdates(context.applicationContext, settings.widgetFrequency)
    }

    val darkTheme = when (settings.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) premiumDarkScheme() else premiumLightScheme(),
        shapes = MaterialTheme.shapes.copy(
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AppScaffold(repository, store, settings)
        }
    }
}

private fun premiumLightScheme() = lightColorScheme(
    primary = Color(0xFF67523A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1E3D0),
    onPrimaryContainer = Color(0xFF24180C),
    secondary = Color(0xFF4F675B),
    secondaryContainer = Color(0xFFD3E8DB),
    tertiary = Color(0xFF7A4E5E),
    background = Color(0xFFFFFBF5),
    surface = Color(0xFFFFFBF5),
    surfaceVariant = Color(0xFFEDE3D8),
    outline = Color(0xFF837568)
)

private fun premiumDarkScheme() = darkColorScheme(
    primary = Color(0xFFE2C7A8),
    primaryContainer = Color(0xFF4D3923),
    onPrimaryContainer = Color(0xFFFFEEDB),
    secondary = Color(0xFFAFCDBE),
    secondaryContainer = Color(0xFF314B40),
    tertiary = Color(0xFFE7B8C7),
    background = Color(0xFF151310),
    surface = Color(0xFF1D1A16),
    surfaceVariant = Color(0xFF4F463D),
    outline = Color(0xFF9F9184)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    repository: QuoteRepository,
    store: SettingsDataStore,
    settings: AppSettings
) {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Main) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("QuoteWidget", fontWeight = FontWeight.Bold)
                        Text(
                            "мотивация без лишнего шума",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen == AppScreen.Main,
                    onClick = { screen = AppScreen.Main },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = screen == AppScreen.Favorites,
                    onClick = { screen = AppScreen.Favorites },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Избранное") }
                )
                NavigationBarItem(
                    selected = screen == AppScreen.Settings,
                    onClick = { screen = AppScreen.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Настройки") }
                )
            }
        }
    ) { padding ->
        when (screen) {
            AppScreen.Main -> MainScreen(repository, store, settings, padding)
            AppScreen.Favorites -> FavoritesScreen(repository, store, settings, padding)
            AppScreen.Settings -> SettingsScreen(store, settings, padding)
        }
    }
}

@Composable
private fun MainScreen(
    repository: QuoteRepository,
    store: SettingsDataStore,
    settings: AppSettings,
    padding: PaddingValues
) {
    val scope = rememberCoroutineScope()
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var quote by remember { mutableStateOf(repository.quoteOfDay()) }
    val isFavorite = quote.id in settings.favoriteIds

    LaunchedEffect(selectedCategory) {
        quote = repository.nextQuote(null, selectedCategory)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HeaderBlock(
                title = "Цитата дня",
                subtitle = "Выберите категорию или листайте дальше",
                icon = Icons.Default.AutoAwesome
            )
        }
        item {
            QuoteCard(
                quote = quote,
                isFavorite = isFavorite,
                onFavoriteClick = { scope.launch { store.toggleFavorite(quote.id) } }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { quote = repository.nextQuote(quote.id, selectedCategory) }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Следующая")
                }
                OutlinedButton(onClick = { scope.launch { store.toggleFavorite(quote.id) } }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isFavorite) "Сохранено" else "В избранное")
                }
            }
        }
        item {
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategoryClick = { category ->
                    selectedCategory = if (category.isBlank() || selectedCategory == category) null else category
                }
            )
        }
        item {
            StatsRow(
                quotesCount = repository.allQuotes.size,
                favoriteCount = settings.favoriteIds.size,
                widgetCategoryCount = settings.widgetCategories.size
            )
        }
    }
}

@Composable
private fun HeaderBlock(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.FormatQuote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = quote.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "- ${quote.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(quote.category) })
                if (quote.isInspiredBy) {
                    AssistChip(onClick = {}, label = { Text("по мотивам") })
                }
            }
        }
    }
}

@Composable
private fun CategorySelector(
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Категории", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategoryClick(selectedCategory ?: "") },
                label = { Text("все") }
            )
            QuoteCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category.title,
                    onClick = { onCategoryClick(category.title) },
                    label = { Text(category.title) }
                )
            }
        }
    }
}

@Composable
private fun StatsRow(
    quotesCount: Int,
    favoriteCount: Int,
    widgetCategoryCount: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard("Цитат", quotesCount.toString(), Modifier.weight(1f))
        StatCard("Избранных", favoriteCount.toString(), Modifier.weight(1f))
        StatCard("В виджете", widgetCategoryCount.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FavoritesScreen(
    repository: QuoteRepository,
    store: SettingsDataStore,
    settings: AppSettings,
    padding: PaddingValues
) {
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    val favorites = repository.byIds(settings.favoriteIds)
    val visibleFavorites = favorites.filter { quote ->
        val matchesQuery = query.isBlank() ||
            quote.text.contains(query, ignoreCase = true) ||
            quote.author.contains(query, ignoreCase = true)
        val matchesCategory = category == null || quote.category == category
        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeaderBlock(
                title = "Избранное",
                subtitle = "${favorites.size} сохраненных цитат",
                icon = Icons.Default.Star
            )
        }
        if (favorites.isNotEmpty()) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Поиск по тексту или автору") }
                )
            }
            item {
                CategorySelector(selectedCategory = category, onCategoryClick = { selected ->
                    category = if (selected.isBlank() || category == selected) null else selected
                })
            }
        }
        if (favorites.isEmpty()) {
            item {
                EmptyState(
                    title = "Пока нет избранных цитат",
                    subtitle = "Добавляйте сильные мысли с главного экрана."
                )
            }
        } else if (visibleFavorites.isEmpty()) {
            item {
                EmptyState(
                    title = "Ничего не найдено",
                    subtitle = "Попробуйте другой запрос или категорию."
                )
            }
        } else {
            items(visibleFavorites, key = { it.id }) { quote ->
                FavoriteQuoteCard(quote = quote, onRemove = {
                    scope.launch { store.toggleFavorite(quote.id) }
                })
            }
        }
    }
}

@Composable
private fun FavoriteQuoteCard(quote: Quote, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                quote.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Text("- ${quote.author}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AssistChip(onClick = {}, label = { Text(quote.category) })
                OutlinedButton(onClick = onRemove) {
                    Icon(Icons.Default.BookmarkRemove, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Удалить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    store: SettingsDataStore,
    settings: AppSettings,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HeaderBlock(
                title = "Настройки",
                subtitle = "Тема, категории и частота виджета",
                icon = Icons.Default.Settings
            )
        }
        item {
            SettingsCard(title = "Тема", icon = Icons.Default.Palette) {
                SingleChoiceSegmentedButtonRow {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = settings.themeMode == mode,
                            onClick = { scope.launch { store.setThemeMode(mode) } },
                            shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                            label = { Text(mode.title) }
                        )
                    }
                }
            }
        }
        item {
            SettingsCard(title = "Категории виджета", icon = Icons.Default.Widgets) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuoteCategory.entries.forEach { category ->
                        FilterChip(
                            selected = category.title in settings.widgetCategories,
                            onClick = {
                                scope.launch {
                                    store.toggleWidgetCategory(category.title)
                                    updateAllQuoteWidgets(context.applicationContext)
                                }
                            },
                            label = { Text(category.title) }
                        )
                    }
                }
            }
        }
        item {
            SettingsCard(title = "Обновление виджета", icon = Icons.Default.Refresh) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WidgetFrequency.entries.forEach { frequency ->
                            FilterChip(
                                selected = settings.widgetFrequency == frequency,
                                onClick = { scope.launch { store.setWidgetFrequency(frequency) } },
                                label = { Text(frequency.title) }
                            )
                        }
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                store.nextWidgetSeed()
                                updateAllQuoteWidgets(context.applicationContext)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Обновить сейчас")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            content()
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
