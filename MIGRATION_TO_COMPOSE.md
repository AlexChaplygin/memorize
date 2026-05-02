# План миграции на Jetpack Compose

## Обзор

Документ описывает поэтапный план перевода приложения с XML/Views на Jetpack Compose.  
Каждый этап независим — после завершения любого этапа приложение остаётся полностью рабочим.

---

## Принципы миграции

1. **Поэкранная миграция** — переводим по одному экрану за раз
2. **Совместимость** — старые экраны на Views продолжают работать
3. **ViewModel-first** — сначала создаём ViewModel, потом UI
4. **Без регрессий** — каждый этап тестируется перед переходом к следующему

---

## Этап 1: AddWordActivity (пилотный экран)

**Срок:** 1-2 часа  
**Сложность:** Низкая  
**Почему первый:** Простой экран с одной формой ввода, нет сложной бизнес-логики.

### Задачи

- [ ] Создать `AddWordViewModel.kt`
  - StateFlow для состояния экрана
  - Метод `saveWord()` для сохранения
  - Обработка ошибок валидации
- [ ] Создать `AddWordScreen.kt`
  - TextField для слова и перевода
  - Кнопка сохранения
  - Индикатор загрузки
  - Обработка ошибок (Snackbar)
- [ ] Обновить `AddWordActivity.kt`
  - Заменить XML на `ComposeView`
  - Подключить ViewModel через Koin
  - Сохранить Intent-параметры (dictionaryId)

### Структура файлов

```
app/src/main/java/com/alex/che/memorize/
├── ui/
│   └── screens/
│       └── AddWordScreen.kt (новый)
├── viewmodel/
│   └── AddWordViewModel.kt (новый)
└── activity/
    └── AddWordActivity.kt (обновлённый)
```

### Критерии готовности

- [ ] Экран открывается и закрывается
- [ ] Слово сохраняется в базу
- [ ] Валидация работает (пустые поля)
- [ ] Возврат в DictionaryActivity работает

---

## Этап 2: CreateDictionaryActivity

**Срок:** 1 час  
**Сложность:** Низкая  
**Почему второй:** Ещё проще — одно поле ввода и кнопка.

### Задачи

- [ ] Создать `CreateDictionaryViewModel.kt`
  - StateFlow для названия словаря
  - Метод `createDictionary()`
- [ ] Создать `CreateDictionaryScreen.kt`
  - TextField для названия
  - Кнопки «Сохранить» и «Назад»
  - Валидация (непустое название)
- [ ] Обновить `CreateDictionaryActivity.kt`
  - Заменить XML на `ComposeView`

### Критерии готовности

- [ ] Словарь создаётся
- [ ] Валидация названия работает
- [ ] Возврат в MainActivity работает

---

## Этап 3: DictionaryActivity

**Срок:** 3-4 часа  
**Сложность:** Средняя  
**Почему третий:** Есть меню, список слов, но структура понятная.

### Задачи

- [ ] Создать `DictionaryViewModel.kt`
  - StateFlow для списка слов
  - Методы: `loadWords()`, `deleteWord()`, `exportDictionary()`, `importFromCsv()`
  - Подсчёт количества слов
- [ ] Создать `DictionaryScreen.kt`
  - TopAppBar с меню (через `DropdownMenu`)
  - `LazyColumn` для списка слов
  - Кнопки действий (тренировка, добавление слова)
  - Индикаторы количества слов
- [ ] Обновить `DictionaryActivity.kt`
  - Заменить XML на `ComposeView`
  - Сохранить обработку меню

### Особенности

- Menu через `ComposeMenu` или оставить OptionsMenu в Activity
- Список слов через `LazyColumn` с ключами
- File picker для импорта CSV (оставить как есть)

### Критерии готовности

- [ ] Список слов отображается
- [ ] Меню работает (добавить слово, импорт, экспорт, удаление)
- [ ] Кнопки «Тренировать» работают
- [ ] Возврат в MainActivity работает

---

## Этап 4: TrainWordsActivity

**Срок:** 4-6 часов
**Сложность:** Высокая
**Почему четвёртый:** Игровая логика, анимации, работа с состоянием.
**Статус:** ✅ Завершено

### Задачи

- [x] Создать `TrainViewModel.kt`
  - StateFlow для текущего слова
  - Методы: `loadWords()`, `checkAnswer()`, `nextWord()`, `markDifficult()`
  - Подсчёт прогресса
- [x] Создать `TrainScreen.kt`
  - Карточка слова с вопросом
  - Варианты ответов (кнопки)
  - Индикатор прогресса
  - Экран результатов
- [x] Обновить `TrainWordsActivity.kt`
  - Заменить XML на Compose
  - Сохранить старый XML для обратной совместимости

### Особенности

- Анимации через `animateContentSize()`, `AnimatedContent`
- Обратная связь (вибрация, цвета)
- Сохранение прогресса тренировки

### Критерии готовности

- [x] Слова показываются по очереди
- [x] Проверка ответа работает
- [x] Прогресс сохраняется
- [x] Возврат в DictionaryActivity работает

---

## Этап 5: MainActivity + Навигация

**Срок:** 4-6 часов  
**Сложность:** Средняя  
**Почему последний:** Главный экран со списком словарей, требует навигации.

### Задачи

- [ ] Создать `MainViewModel.kt`
  - StateFlow для списка словарей
  - Метод `loadDictionaries()`
- [ ] Создать `MainScreen.kt`
  - TopAppBar с меню
  - `LazyColumn` для списка словарей
  - Кнопка создания нового словаря
- [ ] Создать `NavigationGraph.kt`
  - `NavHost` с маршрутами
  - Переходы между экранами
  - Передача параметров (dictionaryId)
- [ ] Обновить `MainActivity.kt`
  - Заменить XML на `ComposeView`
  - Подключить навигацию

### Особенности

- Навигация через `NavController`
- Передача параметров через `arguments`
- Back stack работает корректно

### Критерии готовности

- [ ] Список словарей отображается
- [ ] Переходы между экранами работают
- [ ] Кнопка «Назад» работает
- [ ] Меню работает (создать словарь, выход)

---

## Этап 6: Финальная зачистка

**Срок:** 2-3 часа  
**Сложность:** Низкая

### Задачи

- [ ] Удалить старые XML-лейауты
  - `activity_main.xml`
  - `activity_dictionary.xml`
  - `activity_create_dictionary.xml`
  - `activity_add_word.xml`
  - `activity_train_words.xml`
- [ ] Удалить старые View-компоненты (если были вынесены)
- [ ] Обновить темы (Material 3)
  - `Theme.kt` с токенами
  - Цветовая схема
  - Типографика
- [ ] Обновить `build.gradle.kts`
  - Убрать лишние зависимости (AppCompat, если не нужны)
  - Добавить Compose-зависимости (если ещё не все)

### Критерии готовности

- [ ] Приложение собирается без ошибок
- [ ] Все экраны работают
- [ ] Нет предупреждений о deprecated API
- [ ] Размер APK не увеличился значительно

---

## Технические требования

### Зависимости (уже подключены)

```kotlin
// Jetpack Compose
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.graphics)
implementation(libs.androidx.compose.ui.tooling.preview)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.material.icons.extended)
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.navigation.compose)
implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.koin.androidx.compose)
```

### Архитектурные паттерны

**ViewModel:**
```kotlin
class AddWordViewModel(
    private val wordDao: WordDao,
    private val dictionaryId: Int
) : ViewModel() {
    
    private val _state = MutableStateFlow(AddWordState())
    val state: StateFlow<AddWordState> = _state.asStateFlow()
    
    fun saveWord(word: String, translation: String) {
        viewModelScope.launch {
            // логика сохранения
        }
    }
}

data class AddWordState(
    val word: String = "",
    val translation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Screen:**
```kotlin
@Composable
fun AddWordScreen(
    viewModel: AddWordViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Добавить слово") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // UI компоненты
        }
    }
}
```

**Activity:**
```kotlin
class AddWordActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)
        
        setContent {
            MemorizeTheme {
                AddWordScreen(
                    viewModel = koinViewModel(parameters = { parametersOf(dictionaryId) }),
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}
```

---

## Тестирование каждого этапа

### Чек-лист перед завершением этапа

- [ ] Экран открывается без крашей
- [ ] Все кнопки нажимаются
- [ ] Данные сохраняются в базу
- [ ] Возврат назад работает
- [ ] Ориентация экрана зафиксирована (portrait)
- [ ] Нет утечек памяти (проверить в Profiler)
- [ ] Анимации плавные (60 FPS)

### Ручное тестирование

1. Открыть экран
2. Выполнить все действия
3. Повернуть устройство (убедиться, что состояние сохраняется)
4. Свернуть/развернуть приложение
5. Проверить возврат назад

---

## Риски и способы их устранения

| Риск | Вероятность | Решение |
|------|-------------|---------|
| Потеря состояния при повороте | Низкая | Использовать `viewModelScope` и `StateFlow` |
| Увеличение размера APK | Средняя | Проверить после каждого этапа, удалить лишние зависимости |
| Падение производительности | Низкая | Использовать `derivedStateOf`, `remember`, ключи в `LazyColumn` |
| Сложности с навигацией | Средняя | Тщательно тестировать back stack на каждом этапе |
| Конфликты с старым кодом | Низкая | Изолировать Compose-экраны в отдельных Activity |

---

## Метрики успеха

После завершения всех этапов:

- [ ] 100% экранов на Compose
- [ ] Нет XML-лейаутов в проекте
- [ ] Все тесты проходят
- [ ] FPS ≥ 60 на всех экранах
- [ ] Время запуска не увеличилось
- [ ] Размер APK ≤ +10% от исходного

---

## История изменений

- **2026-04-28** — Создан первоначальный план миграции

---

## Контакты

Вопросы и предложения направлять автору документа.
