# QuoteWidget / MotivateQuote

## Русский

QuoteWidget — Android-приложение с мотивационными цитатами и настоящим виджетом домашнего экрана.

Проект сделан как стабильный MVP без backend, платных API и лишней архитектурной сложности.

### Возможности

- Цитата дня на главном экране.
- Переключение на следующую цитату.
- Добавление и удаление цитат из избранного.
- Категории: философия, дисциплина, бизнес, успех, жизнь, наука.
- Экран избранного с поиском и фильтром по категориям.
- Настройки темы: системная, светлая, темная.
- Настройка категорий для виджета.
- Частота обновления виджета: раз в день, каждые 6 часов или вручную.
- Реальный Android home screen widget на Glance.
- Ручное обновление виджета кнопкой `Еще`.
- 100 локальных мотивационных цитат.
- Хранение настроек и избранного через DataStore.

### Стек

- Kotlin
- Jetpack Compose
- Material 3
- Glance AppWidget
- DataStore Preferences
- WorkManager
- Android only

### Требования

- Android Studio
- JDK 17 или новее
- Android SDK с `compileSdk 35`

### Запуск

Откройте корень проекта в Android Studio, дождитесь Gradle Sync и запустите конфигурацию `app`.

Через терминал:

```bash
gradle :app:assembleDebug
```

Установка на подключенное устройство:

```bash
gradle :app:installDebug
```

### APK

```bash
gradle :app:assembleDebug
```

Debug APK будет создан в:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Как проверить виджет

1. Установите приложение на устройство или эмулятор.
2. Откройте список виджетов на домашнем экране.
3. Выберите `QuoteWidget`.
4. Добавьте виджет на экран.
5. В приложении откройте `Настройки` и выберите категории виджета.
6. Нажмите `Обновить сейчас` в приложении или `Еще` прямо в виджете.

### Ограничения Glance

Glance поддерживает меньше визуальных возможностей, чем обычный Compose. Поэтому виджет сделан как надежная адаптивная карточка с несколькими размерами, светлой и темной цветовой схемой, ручным обновлением и автообновлением через WorkManager.

### Что можно улучшить дальше

- Добавить Gradle Wrapper в репозиторий.
- Добавить release signing configuration.
- Добавить UI-тесты и screenshot-тесты.
- Добавить больше размеров и вариантов виджета.
- Добавить импорт и экспорт избранного.

---

## English

QuoteWidget is an Android motivational quote app with a real home screen widget.

The project is built as a stable MVP with no backend, no paid APIs, and no unnecessary architecture complexity.

### Features

- Quote of the day on the main screen.
- Next quote action.
- Add and remove favorite quotes.
- Categories: philosophy, discipline, business, success, life, science.
- Favorites screen with search and category filtering.
- Theme settings: system, light, dark.
- Widget category selection.
- Widget update frequency: daily, every 6 hours, or manual.
- Real Android home screen widget powered by Glance.
- Manual widget refresh via the `More` button.
- 100 local motivational quotes.
- Local settings and favorites stored with DataStore.

### Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Glance AppWidget
- DataStore Preferences
- WorkManager
- Android only

### Requirements

- Android Studio
- JDK 17 or newer
- Android SDK with `compileSdk 35`

### Run

Open the project root in Android Studio, wait for Gradle Sync, and run the `app` configuration.

From terminal:

```bash
gradle :app:assembleDebug
```

Install on a connected device:

```bash
gradle :app:installDebug
```

### APK

```bash
gradle :app:assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### How to Test the Widget

1. Install the app on a device or emulator.
2. Open the home screen widget picker.
3. Select `QuoteWidget`.
4. Place the widget on the home screen.
5. Open `Settings` in the app and select widget categories.
6. Tap `Update now` in the app or `More` directly in the widget.

### Glance Limitations

Glance has fewer UI capabilities than regular Compose. The widget is therefore implemented as a reliable responsive card with multiple sizes, light and dark colors, manual refresh, and automatic updates through WorkManager.

### Possible v2 Improvements

- Add Gradle Wrapper to the repository.
- Add release signing configuration.
- Add UI tests and screenshot tests.
- Add more widget sizes and variants.
- Add favorites import and export.
