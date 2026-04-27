package com.quotewidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.update
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.quotewidget.MainActivity
import com.quotewidget.data.Quote
import com.quotewidget.data.QuoteRepository
import com.quotewidget.settings.SettingsDataStore
import kotlinx.coroutines.flow.first

class QuoteGlanceWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(250.dp, 110.dp),
            DpSize(320.dp, 160.dp),
            DpSize(420.dp, 220.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settings = SettingsDataStore(context).settings.first()
        val quote = QuoteRepository().quoteForWidget(settings.widgetCategories, settings.widgetSeed)

        provideContent {
            QuoteWidgetContent(quote = quote)
        }
    }
}

class QuoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuoteGlanceWidget()
}

class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        SettingsDataStore(context).nextWidgetSeed()
        QuoteGlanceWidget().update(context, glanceId)
    }
}

suspend fun updateAllQuoteWidgets(context: Context) {
    QuoteGlanceWidget().updateAll(context)
}

@Composable
private fun QuoteWidgetContent(quote: Quote) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF7F2EA), Color(0xFF191714)))
            .cornerRadius(24.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "QUOTEWIDGET / ${quote.category.uppercase()}",
            style = TextStyle(
                color = ColorProvider(Color(0xFF7B654B), Color(0xFFD8C0A3)),
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(8.dp))
        Text(
            text = quote.text,
            style = TextStyle(
                color = ColorProvider(Color(0xFF25211C), Color(0xFFF4EFE7)),
                fontWeight = FontWeight.Bold
            ),
            maxLines = 4
        )
        Spacer(GlanceModifier.height(8.dp))
        Text(
            text = "- ${quote.author}",
            style = TextStyle(color = ColorProvider(Color(0xFF6F6256), Color(0xFFCFC4B8))),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(10.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.End
        ) {
            Button(
                text = "Открыть",
                onClick = actionStartActivity<MainActivity>()
            )
            Button(
                text = "Еще",
                onClick = actionRunCallback<RefreshWidgetAction>()
            )
        }
    }
}
