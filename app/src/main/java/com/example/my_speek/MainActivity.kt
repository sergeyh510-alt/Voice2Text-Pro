/*
 * ================================================================
 * 🎙️ Voice2Text Pro - Главная Активность
 * ================================================================
 *
 * 👨‍💻 Разработчик: Сергей Чекрыжов
 * 📧 Email: sergeyh510@gmail.com
 * 🐙 GitHub: https://github.com/sergeyh510-alt
 * 💡 Программа создана с ❤️ для удобства пользователей
 *
 * ================================================================
 * ОПИСАНИЕ ПРИЛОЖЕНИЯ:
 * -------------------
 * Voice2Text Pro - это Android-приложение для распознавания речи
 * с возможностью отправки текста на сервер через TCP-сокеты.
 *
 * КЛЮЧЕВЫЕ ВОЗМОЖНОСТИ:
 * ---------------------
 * 1. 🎤 Распознавание речи (одиночное и непрерывное)
 * 2. 📤 Отправка текста на сервер через TCP-сокеты
 * 3. ⏱️ Автоматическая отправка по паузе
 * 4. 💾 Сохранение настроек в SharedPreferences
 * 5. 🧹 Очистка текста перед отправкой (опционально)
 * 6. 📖 Встроенная справка на русском и английском языках
 *
 * ТЕХНИЧЕСКИЕ ДЕТАЛИ:
 * ------------------
 * - Используется Android SpeechRecognizer (Google Voice)
 * - TCP-сокеты с протоколом: [4 байта длины] + [JSON-данные]
 * - Асинхронная отправка через Coroutines (Dispatchers.IO)
 * - Работа в фоновом режиме с Handler для таймеров
 * - Автосохранение настроек при выходе
 *
 * АРХИТЕКТУРА:
 * ------------
 * MainActivity
 *   ├── UI-элементы (кнопки, поля, переключатели)
 *   ├── SpeechRecognizer (распознавание речи)
 *   ├── SharedPreferences (сохранение настроек)
 *   ├── TCP-клиент (отправка на сервер)
 *   └── Таймеры (пауза, перезапуск)
 *
 * ПРОТОКОЛ ОТПРАВКИ:
 * -----------------
 * 1. [4 байта] - длина JSON-данных (big-endian)
 * 2. [N байт]  - JSON-данные в кодировке UTF-8
 *
 * ФОРМАТ JSON:
 * ------------
 * {
 *   "text": "распознанный текст",
 *   "timestamp": 1234567890,
 *   "device": "Android"
 * }
 * ================================================================
 */

package com.example.my_speek

// ================================================================
// ИМПОРТЫ
// ================================================================
import android.Manifest                              // Разрешения Android
import android.app.AlertDialog                      // Диалоговые окна
import android.content.Context                      // Контекст приложения
import android.content.Intent                       // Intent для SpeechRecognizer
import android.content.SharedPreferences            // Хранение настроек
import android.content.pm.PackageManager            // Проверка разрешений
import android.os.Bundle                            // Состояние активности
import android.os.Handler                           // Таймеры
import android.os.Looper                            // Главный поток
import android.speech.RecognitionListener           // Слушатель распознавания
import android.speech.RecognizerIntent              // Intent для распознавания
import android.speech.SpeechRecognizer              // Распознаватель речи
import android.widget.Button                        // Кнопки
import android.widget.EditText                      // Поля ввода
import android.widget.Switch                        // Переключатели
import android.widget.TextView                      // Текстовые поля
import android.widget.Toast                         // Всплывающие сообщения
import androidx.appcompat.app.AppCompatActivity      // Базовая активность
import androidx.core.app.ActivityCompat              // Запрос разрешений
import androidx.core.content.ContextCompat           // Проверка разрешений
import kotlinx.coroutines.*                         // Асинхронность (Coroutines)
import org.json.JSONObject                          // Работа с JSON
import java.io.IOException                          // Ошибки ввода-вывода
import java.net.ConnectException                    // Ошибка подключения
import java.net.SocketTimeoutException              // Таймаут сокета
import java.net.UnknownHostException                // Неизвестный хост
import java.util.Locale                             // Локализация

/**
 * MainActivity - Главный класс приложения
 *
 * Отвечает за:
 * - Управление UI-элементами
 * - Распознавание речи через SpeechRecognizer
 * - Отправку текста на сервер
 * - Сохранение и загрузку настроек
 * - Отображение справки
 */
class MainActivity : AppCompatActivity() {

    // ================================================================
    // 1. ОБЪЯВЛЕНИЕ UI-ЭЛЕМЕНТОВ
    // ================================================================

    /** Кнопка одиночного распознавания речи (🎤 Speak) */
    private lateinit var btnListen: Button

    /** Кнопка очистки текста (🗑️ Clear) */
    private lateinit var btnClear: Button

    /** Кнопка непрерывного режима (🔄 Continuous) */
    private lateinit var btnContinuous: Button

    /** Большая кнопка отправки на сервер (📤 SEND) */
    private lateinit var btnSend: Button

    /** Кнопка краткой справки (❓ Help) */
    private lateinit var btnHelp: Button

    /** Кнопка полной справки (📖 Full Help) */
    private lateinit var btnHelpFull: Button

    /** Поле вывода распознанного текста */
    private lateinit var tvResult: TextView

    /** Строка статуса с текущим состоянием */
    private lateinit var tvStatus: TextView

    /** Поле ввода задержки паузы (секунды) */
    private lateinit var etDelay: EditText

    /** Поле ввода URL сервера */
    private lateinit var etServerUrl: EditText

    /** Переключатель автоматической отправки */
    private lateinit var switchAutoSend: Switch

    /** Переключатель очистки перед отправкой */
    private lateinit var switchClearBeforeSend: Switch

    /** Кнопка сохранения настроек */
    private lateinit var btnSaveSettings: Button

    // ================================================================
    // 2. ПЕРЕМЕННЫЕ СОСТОЯНИЯ
    // ================================================================

    /** Экземпляр SpeechRecognizer для распознавания речи */
    private var speechRecognizer: SpeechRecognizer? = null

    /** Флаг: идет ли распознавание в данный момент */
    private var isListening = false

    /** Флаг: включен ли непрерывный режим */
    private var isContinuousMode = false

    /** Накопленный текст (используется когда очистка выключена) */
    private var accumulatedText = ""

    /** Последний промежуточный текст (используется для таймера паузы) */
    private var lastPartialText = ""

    /** Таймер для отслеживания паузы в речи */
    private var silenceTimer: Handler? = null

    /** Действие, выполняемое по таймеру паузы */
    private var silenceRunnable: Runnable? = null

    /** Флаг: говорит ли пользователь в данный момент */
    private var isSpeaking = false

    /** Последний отправленный текст (защита от дубликатов) */
    private var lastSentText = ""

    /** Флаг: идет ли отправка данных на сервер */
    private var isSending = false

    /** Хранилище настроек приложения (SharedPreferences) */
    private lateinit var sharedPrefs: SharedPreferences

    // ================================================================
    // 3. КОНСТАНТЫ
    // ================================================================

    companion object {
        /** Код запроса разрешения на запись аудио */
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 1

        /** Задержка паузы по умолчанию (секунды) */
        private const val DEFAULT_DELAY = 2

        /** URL сервера по умолчанию */
        private const val DEFAULT_SERVER_URL = "http://192.168.1.100:5000"

        /** Имя файла для хранения настроек */
        private const val PREFS_NAME = "AppSettings"

        /** Ключ для сохранения URL сервера */
        private const val KEY_SERVER_URL = "server_url"

        /** Ключ для сохранения задержки */
        private const val KEY_DELAY = "delay"

        /** Ключ для сохранения состояния автоотправки */
        private const val KEY_AUTO_SEND = "auto_send"

        /** Ключ для сохранения состояния очистки */
        private const val KEY_CLEAR_BEFORE_SEND = "clear_before_send"
    }

    // ================================================================
    // 4. ЖИЗНЕННЫЙ ЦИКЛ: onCreate()
    // ================================================================

    /**
     * onCreate() - вызывается при создании активности
     *
     * Инициализирует все компоненты приложения:
     * 1. Загружает UI-элементы
     * 2. Загружает сохраненные настройки
     * 3. Настраивает слушатели событий
     * 4. Проверяет разрешения
     * 5. Инициализирует SpeechRecognizer
     * 6. Настраивает обработчики кнопок
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // --- Инициализация SharedPreferences ---
            // Получаем доступ к хранилищу настроек приложения
            sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // --- Привязка UI-элементов ---
            // Находим все элементы управления по их ID
            btnListen = findViewById(R.id.btnListen)
            btnClear = findViewById(R.id.btnClear)
            btnContinuous = findViewById(R.id.btnContinuous)
            btnSend = findViewById(R.id.btnSend)
            btnHelp = findViewById(R.id.btnHelp)
            btnHelpFull = findViewById(R.id.btnHelpFull)
            tvResult = findViewById(R.id.tvResult)
            tvStatus = findViewById(R.id.tvStatus)
            etDelay = findViewById(R.id.etDelay)
            etServerUrl = findViewById(R.id.etServerUrl)
            switchAutoSend = findViewById(R.id.switchAutoSend)
            switchClearBeforeSend = findViewById(R.id.switchClearBeforeSend)
            btnSaveSettings = findViewById(R.id.btnSaveSettings)

            // --- Загрузка сохраненных настроек ---
            // Восстанавливаем предыдущие настройки пользователя
            loadSettings()

            // --- Настройка слушателей для автосохранения ---
            // При изменении полей настройки сохраняются автоматически
            setupSettingsListeners()

            // --- Обработчик кнопки "💾 Save Settings" ---
            // Сохраняет текущие настройки при нажатии
            btnSaveSettings.setOnClickListener {
                saveSettings()
                Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
            }

            // --- Обработчик кнопки "❓ Help" (краткая справка) ---
            // Показывает диалог с кратким описанием функций
            btnHelp.setOnClickListener {
                showQuickHelp()
            }

            // --- Обработчик кнопки "📖 Full Help" (полная справка) ---
            // Показывает диалог с подробным описанием приложения
            btnHelpFull.setOnClickListener {
                showFullHelp()
            }

            // --- Проверка разрешений ---
            // Запрашиваем разрешение на запись аудио, если его нет
            checkPermissions()

            // --- Инициализация распознавателя речи ---
            // Создаем и настраиваем SpeechRecognizer
            setupSpeechRecognizer()

            // --- Обработчик кнопки "🎤 Speak" ---
            // Запускает одиночное распознавание речи
            btnListen.setOnClickListener {
                if (!isListening) {
                    if (!isContinuousMode) {
                        startListening()  // Запускаем распознавание
                    } else {
                        // Если включен непрерывный режим - сообщаем пользователю
                        Toast.makeText(this, "First disable Continuous mode", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // --- Обработчик кнопки "🔄 Continuous" ---
            // Включает/выключает непрерывный режим распознавания
            btnContinuous.setOnClickListener {
                if (isContinuousMode) {
                    stopContinuousMode()   // Останавливаем непрерывный режим
                } else {
                    startContinuousMode()  // Запускаем непрерывный режим
                }
            }

            // --- Обработчик кнопки "🗑️ Clear" ---
            // Очищает текст и сбрасывает состояние
            btnClear.setOnClickListener {
                accumulatedText = ""
                lastSentText = ""
                tvResult.text = "Recognized text will appear here..."
                tvStatus.text = "Text cleared"
                cancelSilenceTimer()  // Отменяем таймер паузы
            }

            // --- Обработчик БОЛЬШОЙ КНОПКИ "📤 SEND" ---
            // Отправляет текущий текст на сервер
            btnSend.setOnClickListener {
                sendTextToServer()
            }

        } catch (e: Exception) {
            // Обработка ошибок инициализации
            Toast.makeText(this, "Init error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // ================================================================
    // 5. РАБОТА С НАСТРОЙКАМИ (SharedPreferences)
    // ================================================================

    /**
     * loadSettings() - Загрузка настроек
     *
     * Восстанавливает все сохраненные настройки из SharedPreferences:
     * - URL сервера
     * - Задержку паузы
     * - Состояние автоотправки
     * - Состояние очистки
     *
     * Если настройки отсутствуют, используются значения по умолчанию.
     */
    private fun loadSettings() {
        try {
            // Получаем URL сервера (или значение по умолчанию)
            val serverUrl = sharedPrefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL

            // Получаем задержку паузы (или значение по умолчанию)
            val delay = sharedPrefs.getInt(KEY_DELAY, DEFAULT_DELAY)

            // Получаем состояние автоотправки (по умолчанию - включена)
            val autoSend = sharedPrefs.getBoolean(KEY_AUTO_SEND, true)

            // Получаем состояние очистки (по умолчанию - включена)
            val clearBeforeSend = sharedPrefs.getBoolean(KEY_CLEAR_BEFORE_SEND, true)

            // Устанавливаем значения в UI-элементы
            etServerUrl.setText(serverUrl)
            etDelay.setText(delay.toString())
            switchAutoSend.isChecked = autoSend
            switchClearBeforeSend.isChecked = clearBeforeSend

            // Обновляем статус
            tvStatus.text = "Settings loaded"
        } catch (e: Exception) {
            // Обработка ошибки загрузки настроек
            tvStatus.text = "Error loading settings"
            e.printStackTrace()
        }
    }

    /**
     * saveSettings() - Сохранение настроек
     *
     * Сохраняет текущие настройки в SharedPreferences:
     * - URL сервера из поля etServerUrl
     * - Задержку паузы из поля etDelay
     * - Состояние переключателя switchAutoSend
     * - Состояние переключателя switchClearBeforeSend
     *
     * Использует apply() для асинхронного сохранения.
     */
    private fun saveSettings() {
        try {
            // Получаем редактор SharedPreferences
            val editor = sharedPrefs.edit()

            // Сохраняем все настройки
            editor.putString(KEY_SERVER_URL, etServerUrl.text.toString())
            editor.putInt(KEY_DELAY, etDelay.text.toString().toIntOrNull() ?: DEFAULT_DELAY)
            editor.putBoolean(KEY_AUTO_SEND, switchAutoSend.isChecked)
            editor.putBoolean(KEY_CLEAR_BEFORE_SEND, switchClearBeforeSend.isChecked)

            // Применяем изменения (асинхронно)
            editor.apply()

            // Обновляем статус
            tvStatus.text = "Settings saved"
        } catch (e: Exception) {
            // Обработка ошибки сохранения
            tvStatus.text = "Error saving settings"
            e.printStackTrace()
        }
    }

    /**
     * setupSettingsListeners() - Настройка автосохранения
     *
     * Добавляет слушатели к UI-элементам для автоматического сохранения:
     * - При потере фокуса полем URL
     * - При потере фокуса полем задержки (с валидацией)
     * - При изменении переключателя автоотправки
     * - При изменении переключателя очистки
     *
     * Валидация поля задержки: значение должно быть > 0
     */
    private fun setupSettingsListeners() {
        try {
            // Слушатель для поля URL: сохранение при потере фокуса
            etServerUrl.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveSettings()  // Сохраняем настройки
                }
            }

            // Слушатель для поля задержки: сохранение с валидацией
            etDelay.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    // Парсим введенное значение
                    val delay = etDelay.text.toString().toIntOrNull()
                    if (delay != null && delay > 0) {
                        // Если значение корректное - сохраняем
                        saveSettings()
                    } else {
                        // Если некорректное - восстанавливаем значение по умолчанию
                        etDelay.setText(DEFAULT_DELAY.toString())
                        Toast.makeText(this, "Enter valid value (number > 0)", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Слушатель для переключателя автоотправки
            switchAutoSend.setOnCheckedChangeListener { _, _ ->
                saveSettings()
            }

            // Слушатель для переключателя очистки
            switchClearBeforeSend.setOnCheckedChangeListener { _, _ ->
                saveSettings()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * saveSettingsOnExit() - Сохранение настроек при выходе
     *
     * Использует commit() для синхронного сохранения,
     * чтобы гарантировать запись перед закрытием приложения.
     * Вызывается в onPause(), onStop(), onDestroy().
     */
    private fun saveSettingsOnExit() {
        try {
            val editor = sharedPrefs.edit()

            editor.putString(KEY_SERVER_URL, etServerUrl.text.toString())
            editor.putInt(KEY_DELAY, etDelay.text.toString().toIntOrNull() ?: DEFAULT_DELAY)
            editor.putBoolean(KEY_AUTO_SEND, switchAutoSend.isChecked)
            editor.putBoolean(KEY_CLEAR_BEFORE_SEND, switchClearBeforeSend.isChecked)

            // Синхронное сохранение для гарантии записи
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ================================================================
    // 6. СПРАВКА (Help)
    // ================================================================

    /**
     * showQuickHelp() - Отображение краткой справки
     *
     * Показывает диалоговое окно с кратким описанием всех кнопок
     * и основных функций приложения на русском и английском языках.
     */
    private fun showQuickHelp() {
        AlertDialog.Builder(this)
            .setTitle("❓ Quick Help / Краткая справка")
            .setMessage(getQuickHelpText())
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * showFullHelp() - Отображение полной справки
     *
     * Показывает диалоговое окно с подробным описанием:
     * - Назначение приложения
     * - Режимы работы
     * - Настройки
     * - Протокол отправки
     * - Информация о разработчике
     *
     * Текст представлен на русском и английском языках.
     */
    private fun showFullHelp() {
        AlertDialog.Builder(this)
            .setTitle("📖 Full Help / Полная справка")
            .setMessage(getFullHelpText())
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * getQuickHelpText() - Текст краткой справки
     *
     * @return String с кратким описанием функций на RU и EN
     */
    private fun getQuickHelpText(): String {
        return """
            |============================================================
            |📖 Voice2Text Pro - Quick Help / Краткая справка
            |============================================================
            |
            |🇷🇺 РУССКИЙ:
            |• 🎤 Speak - Нажмите и говорите (один раз)
            |• 🔄 Continuous - Непрерывное распознавание
            |• 📤 SEND - ОТПРАВИТЬ текст на сервер
            |• 🗑️ Clear - Очистить текст
            |• ⏱️ Delay - Пауза перед автоотправкой (сек)
            |• 🤖 Auto Send - Автоматическая отправка
            |• 🧹 Clear before Send - Очищать перед отправкой
            |
            |🇬🇧 ENGLISH:
            |• 🎤 Speak - Press and speak (single)
            |• 🔄 Continuous - Continuous recognition
            |• 📤 SEND - SEND text to server
            |• 🗑️ Clear - Clear text
            |• ⏱️ Delay - Pause before auto-send (sec)
            |• 🤖 Auto Send - Automatic sending
            |• 🧹 Clear before Send - Clear before sending
            |
            |📡 Server: TCP socket with JSON format
            |============================================================
        """.trimMargin()
    }

    /**
     * getFullHelpText() - Текст полной справки
     *
     * @return String с подробным описанием приложения на RU и EN
     *         Включает информацию о разработчике
     */
    private fun getFullHelpText(): String {
        return """
            |============================================================
            |📖 Voice2Text Pro - Full Help / Полная справка
            |============================================================
            |
            |👨‍💻 Разработчик: Сергей Чекрыжов
            |📧 Email: sergeyh510@gmail.com
            |🐙 GitHub: https://github.com/sergeyh510-alt
            |💡 Программа создана с ❤️ для удобства пользователей
            |
            |============================================================
            |
            |🇷🇺 РУССКИЙ:
            |-----------------
            |🎯 НАЗНАЧЕНИЕ:
            |Приложение для распознавания речи с отправкой на сервер.
            |Позволяет быстро преобразовывать голос в текст и передавать
            |его на удаленный сервер для дальнейшей обработки.
            |
            |🎤 РЕЖИМЫ РАБОТЫ:
            |1. Одиночный режим (Speak):
            |   - Нажмите кнопку 🎤 Speak и скажите фразу
            |   - После окончания речи текст появится на экране
            |   - Подходит для коротких фраз и команд
            |   
            |2. Непрерывный режим (Continuous):
            |   - Распознавание идет непрерывно
            |   - Текст добавляется по мере распознавания
            |   - Для остановки нажмите кнопку еще раз
            |   - Идеально для длинных диктовок
            |
            |📤 ОТПРАВКА НА СЕРВЕР:
            |• Кнопка 📤 SEND - отправить текущий текст вручную
            |• Автоотправка - при включенном "Auto Send"
            |• Протокол: TCP-сокеты
            |• Формат: JSON (UTF-8)
            |• Структура: {"text":"...", "timestamp":..., "device":"Android"}
            |
            |⏱️ АВТООТПРАВКА ПО ПАУЗЕ:
            |Если включена опция "Auto Send", текст будет автоматически
            |отправлен на сервер через указанную задержку (Delay) после
            |окончания речи. Это удобно для непрерывного режима.
            |
            |🧹 ОЧИСТКА ПЕРЕД ОТПРАВКОЙ:
            |• ВКЛЮЧЕНО (🧹 Clear before Send): каждый новый текст
            |  ЗАМЕНЯЕТ предыдущий (только последний текст)
            |• ВЫКЛЮЧЕНО: тексты НАКАПЛИВАЮТСЯ с новой строки
            |
            |💾 СОХРАНЕНИЕ НАСТРОЕК:
            |Все настройки автоматически сохраняются при выходе
            |или при нажатии кнопки "💾 Save Settings"
            |
            |📡 ТЕХНИЧЕСКИЕ ДЕТАЛИ:
            |• Платформа: Android (Kotlin)
            |• Распознавание: Google Speech Recognizer
            |• Сеть: TCP-сокеты с таймаутами
            |• Асинхронность: Coroutines
            |• Хранение: SharedPreferences
            |
            |------------------------------------------------------------
            |
            |🇬🇧 ENGLISH:
            |-----------------
            |🎯 PURPOSE:
            |Speech recognition app with server sending.
            |Quickly convert voice to text and send to remote server.
            |
            |🎤 OPERATING MODES:
            |1. Single mode (Speak):
            |   - Press 🎤 Speak button and say a phrase
            |   - Text appears after speech ends
            |   - Good for short phrases and commands
            |   
            |2. Continuous mode (Continuous):
            |   - Recognition runs continuously
            |   - Text accumulates as recognized
            |   - Press again to stop
            |   - Perfect for long dictations
            |
            |📤 SERVER SENDING:
            |• 📤 SEND button - send current text manually
            |• Auto-send - when "Auto Send" is enabled
            |• Protocol: TCP sockets
            |• Format: JSON (UTF-8)
            |• Structure: {"text":"...", "timestamp":..., "device":"Android"}
            |
            |⏱️ AUTO-SEND BY PAUSE:
            |If "Auto Send" is enabled, text will be automatically
            |sent to server after specified Delay (seconds) after
            |speech ends. Useful for continuous mode.
            |
            |🧹 CLEAR BEFORE SEND:
            |• ON (🧹 Clear before Send): each new text REPLACES
            |  previous (only last text)
            |• OFF: texts ACCUMULATE with new lines
            |
            |💾 SETTINGS SAVING:
            |All settings are automatically saved on exit
            |or when pressing "💾 Save Settings"
            |
            |📡 TECHNICAL DETAILS:
            |• Platform: Android (Kotlin)
            |• Recognition: Google Speech Recognizer
            |• Network: TCP sockets with timeouts
            |• Async: Coroutines
            |• Storage: SharedPreferences
            |
            |============================================================
            |📡 PROTOCOL / ПРОТОКОЛ:
            |[4 bytes length] + [JSON data]
            |JSON: {"text":"...", "timestamp":..., "device":"Android"}
            |============================================================
        """.trimMargin()
    }

    // ================================================================
    // 7. РАСПОЗНАВАНИЕ РЕЧИ (SpeechRecognizer)
    // ================================================================

    /**
     * setupSpeechRecognizer() - Инициализация распознавателя речи
     *
     * Создает и настраивает SpeechRecognizer с полным набором
     * RecognitionListener для обработки всех событий:
     *
     * События:
     * - onReadyForSpeech()  - готов к приему речи
     * - onBeginningOfSpeech() - начало речи
     * - onEndOfSpeech()    - окончание речи
     * - onPartialResults() - промежуточные результаты
     * - onResults()        - финальные результаты
     * - onError()          - ошибки
     *
     * Обработка ошибок:
     * - ERROR_AUDIO, ERROR_CLIENT, ERROR_NETWORK,
     * - ERROR_NO_MATCH, ERROR_RECOGNIZER_BUSY и др.
     *
     * Особенности:
     * - Автоматический перезапуск в непрерывном режиме
     * - Отображение промежуточных результатов
     * - Таймер паузы для автоотправки
     */
    private fun setupSpeechRecognizer() {
        try {
            // Проверяем, поддерживается ли распознавание на устройстве
            if (!SpeechRecognizer.isRecognitionAvailable(this)) {
                Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_LONG).show()
                btnListen.isEnabled = false
                btnContinuous.isEnabled = false
                return
            }

            // Создаем экземпляр SpeechRecognizer
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

            // Настраиваем слушателя событий распознавания
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {

                /**
                 * onReadyForSpeech() - Распознаватель готов
                 *
                 * Вызывается, когда распознаватель готов к приему речи.
                 * Обновляет UI: кнопки, статус, отменяет таймер.
                 */
                override fun onReadyForSpeech(params: Bundle?) {
                    try {
                        isListening = true  // Устанавливаем флаг прослушивания

                        // Обновляем UI в зависимости от режима
                        if (!isContinuousMode) {
                            // Одиночный режим: меняем кнопку Speak
                            btnListen.text = "🎤 Listening..."
                            btnListen.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_orange_dark
                            ))
                        } else {
                            // Непрерывный режим: меняем кнопку Continuous
                            btnContinuous.text = "🔄 Listening..."
                            btnContinuous.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_orange_dark
                            ))
                        }

                        tvStatus.text = "Speak now..."  // Обновляем статус
                        isSpeaking = true               // Пользователь говорит
                        cancelSilenceTimer()            // Отменяем предыдущий таймер
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                /**
                 * onBeginningOfSpeech() - Начало речи
                 *
                 * Вызывается, когда пользователь начал говорить.
                 * Обновляет статус и отменяет таймер паузы.
                 */
                override fun onBeginningOfSpeech() {
                    try {
                        tvStatus.text = "Recognizing..."  // Идет распознавание
                        isSpeaking = true                 // Пользователь говорит
                        cancelSilenceTimer()              // Отменяем таймер
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                /**
                 * onRmsChanged() - Уровень громкости
                 *
                 * Не используется в приложении, но требуется для интерфейса.
                 */
                override fun onRmsChanged(rmsdB: Float) {}

                /**
                 * onBufferReceived() - Получение аудио-буфера
                 *
                 * Не используется в приложении, но требуется для интерфейса.
                 */
                override fun onBufferReceived(buffer: ByteArray?) {}

                /**
                 * onEndOfSpeech() - Окончание речи
                 *
                 * Вызывается, когда пользователь закончил говорить.
                 * Запускает таймер паузы для автоотправки.
                 */
                override fun onEndOfSpeech() {
                    try {
                        tvStatus.text = "Processing..."  // Обработка результата
                        isSpeaking = false               // Пользователь замолчал
                        startSilenceTimer()              // Запускаем таймер паузы
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                /**
                 * onError() - Ошибка распознавания
                 *
                 * Вызывается при любой ошибке распознавания.
                 * Обрабатывает все типы ошибок с понятными сообщениями.
                 *
                 * Особое поведение:
                 * - ERROR_NO_MATCH в непрерывном режиме - игнорируется
                 * - ERROR_INSUFFICIENT_PERMISSIONS - не перезапускает режим
                 * - Остальные ошибки - показываются пользователю
                 */
                override fun onError(error: Int) {
                    try {
                        // Сбрасываем флаги
                        isListening = false
                        isSpeaking = false
                        cancelSilenceTimer()  // Отменяем таймер

                        // Восстанавливаем UI
                        if (isContinuousMode) {
                            // Непрерывный режим: возвращаем кнопку в исходное состояние
                            btnContinuous.text = "🔄 Continuous"
                            btnContinuous.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_green_dark
                            ))
                            // Перезапускаем непрерывный режим (кроме ошибки прав)
                            if (error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                                restartContinuousListening()
                            }
                        } else {
                            // Одиночный режим: возвращаем кнопку в исходное состояние
                            btnListen.text = "🎤 Speak"
                            btnListen.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_blue_dark
                            ))
                        }

                        // Формируем понятное сообщение об ошибке
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio error / Ошибка аудио"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error / Клиентская ошибка"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions / Недостаточно прав"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error / Сетевая ошибка"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout / Таймаут сети"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized / Речь не распознана"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy / Распознаватель занят"
                            SpeechRecognizer.ERROR_SERVER -> "Server error / Ошибка сервера"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout / Таймаут речи"
                            else -> "Unknown error: $error / Неизвестная ошибка: $error"
                        }

                        // Показываем ошибку (кроме NO_MATCH в непрерывном режиме)
                        if (!isContinuousMode || error != SpeechRecognizer.ERROR_NO_MATCH) {
                            tvStatus.text = "Error: $errorMessage"
                            Toast.makeText(this@MainActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        tvStatus.text = "Recognition error / Ошибка распознавания"
                    }
                }

                /**
                 * onResults() - Финальный результат распознавания
                 *
                 * Самый важный метод! Вызывается, когда распознавание завершено.
                 *
                 * Алгоритм работы:
                 * 1. Сбрасываем флаги и отменяем таймер
                 * 2. Восстанавливаем UI кнопок
                 * 3. Получаем лучший результат распознавания
                 * 4. Обрабатываем текст в зависимости от настроек:
                 *    - Очистка ON: заменяем предыдущий текст
                 *    - Очистка OFF: добавляем с новой строки
                 * 5. Автоотправка в непрерывном режиме (если включена)
                 * 6. Перезапускаем непрерывный режим
                 */
                override fun onResults(results: Bundle?) {
                    try {
                        // Сбрасываем флаги
                        isListening = false
                        isSpeaking = false
                        cancelSilenceTimer()  // Отменяем таймер

                        // Восстанавливаем UI
                        if (isContinuousMode) {
                            btnContinuous.text = "🔄 Continuous"
                            btnContinuous.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_green_dark
                            ))
                        } else {
                            btnListen.text = "🎤 Speak"
                            btnListen.setBackgroundColor(ContextCompat.getColor(
                                this@MainActivity, android.R.color.holo_blue_dark
                            ))
                        }

                        // Получаем список распознанных вариантов
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null && !matches.isEmpty()) {
                            // Берем лучший результат (первый в списке)
                            val spokenText = matches[0] ?: ""
                            if (spokenText.isNotEmpty()) {
                                // Обработка текста в зависимости от настройки очистки
                                if (switchClearBeforeSend.isChecked) {
                                    // Режим "только последний текст" - заменяем
                                    accumulatedText = spokenText
                                    tvResult.text = spokenText
                                    tvStatus.text = "Recognized (cleared): $spokenText"
                                } else {
                                    // Режим накопления - добавляем с новой строки
                                    addText(spokenText)
                                    tvStatus.text = "Recognized: $spokenText"
                                }

                                // Автоотправка в непрерывном режиме (если включена)
                                if (isContinuousMode && switchAutoSend.isChecked) {
                                    sendTextToServer()
                                }
                            }
                            lastPartialText = ""  // Очищаем промежуточный текст
                        } else {
                            tvStatus.text = "Nothing recognized / Ничего не распознано"
                        }

                        // Перезапускаем непрерывный режим
                        if (isContinuousMode) {
                            restartContinuousListening()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        tvStatus.text = "Result processing error / Ошибка обработки результата"
                    }
                }

                /**
                 * onPartialResults() - Промежуточные результаты
                 *
                 * Вызывается во время речи для отображения промежуточных
                 * результатов распознавания. Используется для:
                 * - Отображения текста в реальном времени
                 * - Сохранения последнего промежуточного текста для таймера паузы
                 */
                override fun onPartialResults(partialResults: Bundle?) {
                    try {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null && !matches.isEmpty()) {
                            val text = matches[0] ?: ""
                            if (text.isNotEmpty()) {
                                // Сохраняем промежуточный текст
                                lastPartialText = text
                                tvStatus.text = "Partial: $text"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                /**
                 * onEvent() - Другие события
                 *
                 * Не используется в приложении, но требуется для интерфейса.
                 */
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } catch (e: Exception) {
            // Обработка ошибки настройки распознавателя
            Toast.makeText(this, "Recognizer setup error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // ================================================================
    // 8. ТАЙМЕР ПАУЗЫ (Silence Timer)
    // ================================================================

    /**
     * startSilenceTimer() - Запуск таймера паузы
     *
     * Запускает таймер, который срабатывает через указанное количество
     * секунд после окончания речи. Используется для:
     * - Фиксации промежуточного текста как финального
     * - Автоматической отправки текста
     *
     * Алгоритм:
     * 1. Отменяем предыдущий таймер
     * 2. Получаем задержку из поля etDelay
     * 3. Создаем Runnable с проверкой:
     *    - Пользователь не говорит
     *    - Есть промежуточный текст
     *    - Включен непрерывный режим
     * 4. Запускаем таймер с задержкой
     *
     * Если условия выполнены - текст фиксируется и отправляется.
     */
    private fun startSilenceTimer() {
        try {
            cancelSilenceTimer()  // Отменяем предыдущий таймер

            // Получаем задержку в секундах
            val delaySeconds = etDelay.text.toString().toIntOrNull() ?: DEFAULT_DELAY

            // Создаем Handler для работы в главном потоке
            silenceTimer = Handler(Looper.getMainLooper())

            // Создаем Runnable с действием
            val runnable = Runnable {
                try {
                    // Проверяем условия для фиксации текста
                    if (!isSpeaking && lastPartialText.isNotEmpty() && isContinuousMode) {
                        val text = lastPartialText
                        if (text.isNotEmpty()) {
                            // Фиксируем текст в зависимости от настройки очистки
                            if (switchClearBeforeSend.isChecked) {
                                accumulatedText = text
                                tvResult.text = text
                                tvStatus.text = "Recognized by pause (cleared): $text"
                            } else {
                                addText(text)
                                tvStatus.text = "Recognized by pause: $text"
                            }

                            lastPartialText = ""  // Очищаем промежуточный текст

                            // Автоотправка, если включена
                            if (switchAutoSend.isChecked) {
                                tvStatus.text = "Sending by pause..."
                                sendTextToServer()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Сохраняем Runnable и запускаем таймер
            silenceRunnable = runnable
            silenceTimer?.postDelayed(runnable, delaySeconds * 1000L)  // Конвертация в миллисекунды
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * cancelSilenceTimer() - Отмена таймера паузы
     *
     * Отменяет запущенный таймер паузы.
     * Вызывается при:
     * - Начале новой речи
     * - Ошибке распознавания
     * - Получении финального результата
     * - Очистке текста
     * - Остановке непрерывного режима
     * - Уничтожении активности
     */
    private fun cancelSilenceTimer() {
        try {
            // Отменяем Runnable, если он существует
            silenceRunnable?.let { runnable ->
                silenceTimer?.removeCallbacks(runnable)
            }
            // Очищаем ссылки
            silenceTimer = null
            silenceRunnable = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ================================================================
    // 9. УПРАВЛЕНИЕ ТЕКСТОМ
    // ================================================================

    /**
     * addText() - Добавление текста с новой строки
     *
     * Используется в режиме накопления (когда очистка выключена).
     * Добавляет новый текст к существующему с разделителем "\n".
     *
     * @param text Новый текст для добавления
     */
    private fun addText(text: String) {
        try {
            val currentText = tvResult.text.toString()

            // Если поле пустое или содержит текст-заглушку - заменяем
            if (currentText == "Recognized text will appear here..." || currentText.isEmpty()) {
                accumulatedText = text
                tvResult.text = text
            } else {
                // Иначе добавляем с новой строки
                accumulatedText = "$currentText\n$text"
                tvResult.text = accumulatedText
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ================================================================
    // 10. УПРАВЛЕНИЕ РАСПОЗНАВАНИЕМ
    // ================================================================

    /**
     * startListening() - Запуск распознавания речи
     *
     * Алгоритм:
     * 1. Проверяем инициализацию SpeechRecognizer
     * 2. Проверяем разрешение на запись аудио
     * 3. Создаем Intent с параметрами распознавания
     * 4. Запускаем распознавание
     *
     * Параметры распознавания:
     * - LANGUAGE_MODEL_FREE_FORM - свободная форма
     * - LANGUAGE - язык системы
     * - MAX_RESULTS - только 1 результат
     * - SPEECH_INPUT_MINIMUM_LENGTH_MILLIS - минимальная длина речи
     * - SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS - таймаут тишины
     */
    private fun startListening() {
        try {
            // Проверяем, инициализирован ли распознаватель
            if (speechRecognizer == null) {
                Toast.makeText(this, "Recognizer not initialized", Toast.LENGTH_SHORT).show()
                return
            }

            // Проверяем разрешение на запись аудио
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                // Если разрешения нет - запрашиваем
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_RECORD_AUDIO
                )
                return
            }

            // Создаем Intent для распознавания
            val intent = createSpeechIntent()

            // Запускаем распознавание
            speechRecognizer?.startListening(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Start error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * startContinuousMode() - Запуск непрерывного режима
     *
     * Включает непрерывное распознавание речи.
     *
     * Алгоритм:
     * 1. Проверяем разрешение на запись аудио
     * 2. Устанавливаем флаг isContinuousMode = true
     * 3. Обновляем UI (кнопка "Stop", красный цвет)
     * 4. Очищаем текст (если включена очистка)
     * 5. Запускаем распознавание
     */
    private fun startContinuousMode() {
        try {
            // Проверяем разрешение на запись аудио
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_RECORD_AUDIO
                )
                return
            }

            // Устанавливаем флаг непрерывного режима
            isContinuousMode = true

            // Обновляем UI
            btnContinuous.text = "⏹️ Stop"
            btnContinuous.setBackgroundColor(ContextCompat.getColor(
                this, android.R.color.holo_red_dark
            ))

            // Очищаем текст перед началом (если включена очистка)
            if (switchClearBeforeSend.isChecked) {
                accumulatedText = ""
                tvResult.text = "Recognized text will appear here..."
                tvStatus.text = "Continuous mode activated (clearing ON)"
            } else {
                tvStatus.text = "Continuous mode activated"
            }

            // Запускаем распознавание
            startListening()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * stopContinuousMode() - Остановка непрерывного режима
     *
     * Выключает непрерывное распознавание речи.
     *
     * Алгоритм:
     * 1. Сбрасываем флаг isContinuousMode = false
     * 2. Сбрасываем флаг isListening = false
     * 3. Отменяем таймер паузы
     * 4. Останавливаем и отменяем распознавание
     * 5. Восстанавливаем UI
     */
    private fun stopContinuousMode() {
        try {
            // Сбрасываем флаги
            isContinuousMode = false
            isListening = false

            // Отменяем таймер и останавливаем распознавание
            cancelSilenceTimer()
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()

            // Восстанавливаем UI
            btnContinuous.text = "🔄 Continuous"
            btnContinuous.setBackgroundColor(ContextCompat.getColor(
                this, android.R.color.holo_green_dark
            ))
            btnListen.text = "🎤 Speak"
            btnListen.setBackgroundColor(ContextCompat.getColor(
                this, android.R.color.holo_blue_dark
            ))
            tvStatus.text = "Continuous mode stopped"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * restartContinuousListening() - Перезапуск непрерывного режима
     *
     * Перезапускает распознавание в непрерывном режиме после
     * получения результата или ошибки.
     *
     * Особенности:
     * - Небольшая задержка (100 мс) для стабильности
     * - Проверка флага isContinuousMode перед запуском
     */
    private fun restartContinuousListening() {
        try {
            if (isContinuousMode) {
                // Запускаем с задержкой для стабильности
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isContinuousMode) {
                        startListening()
                    }
                }, 100)  // 100 мс задержки
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * createSpeechIntent() - Создание Intent для распознавания
     *
     * Настраивает параметры распознавания речи:
     * - ACTION_RECOGNIZE_SPEECH - действие распознавания
     * - LANGUAGE_MODEL_FREE_FORM - свободная языковая модель
     * - EXTRA_LANGUAGE - язык системы (Locale.getDefault())
     * - EXTRA_PROMPT - подсказка для пользователя
     * - EXTRA_MAX_RESULTS - только 1 результат
     * - EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS - мин. длина речи (1 сек)
     * - EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS - таймаут (2 сек)
     *
     * @return Intent для распознавания речи
     */
    private fun createSpeechIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())  // Язык системы
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")          // Подсказка
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)                 // Только лучший результат
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)  // 1 сек
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)  // 2 сек
        }
    }

    // ================================================================
    // 11. ОТПРАВКА НА СЕРВЕР (TCP-СОКЕТЫ)
    // ================================================================

    /**
     * sendTextToServer() - Отправка текста на сервер
     *
     * Отправляет текущий распознанный текст на сервер через TCP-сокеты.
     *
     * ПРОТОКОЛ:
     * ---------
     * 1. [4 байта] - длина JSON-данных (big-endian)
     * 2. [N байт]  - JSON-данные в кодировке UTF-8
     *
     * ФОРМАТ JSON:
     * ------------
     * {
     *   "text": "распознанный текст",
     *   "timestamp": 1234567890,
     *   "device": "Android"
     * }
     *
     * ОСОБЕННОСТИ:
     * ------------
     * - Fire-and-forget: не ждем подтверждения от сервера
     * - Таймаут соединения: 2 секунды
     * - Таймаут чтения: 3 секунды
     * - Асинхронная отправка в фоновом потоке (Coroutines)
     * - Защита от дубликатов (lastSentText)
     * - Автоматическая очистка после успешной отправки
     *
     * ЭТАПЫ:
     * -------
     * 1. Проверка: не идет ли отправка, есть ли текст
     * 2. Проверка: не дубликат ли текст
     * 3. Проверка: URL сервера
     * 4. Парсинг URL (хост и порт)
     * 5. Сохранение настроек
     * 6. Асинхронная отправка в Coroutine
     * 7. Обновление UI после отправки
     */
    private fun sendTextToServer() {
        try {
            // --- ШАГ 1: Проверка, не идет ли отправка ---
            if (isSending) {
                tvStatus.text = "⏳ Sending in progress..."
                return
            }

            // --- ШАГ 2: Проверка наличия текста ---
            val text = tvResult.text?.toString() ?: ""
            if (text.isEmpty() || text == "Recognized text will appear here...") {
                tvStatus.text = "⚠️ No text to send"
                Toast.makeText(this, "No text to send", Toast.LENGTH_SHORT).show()
                return
            }

            // --- ШАГ 3: Проверка на дубликат ---
            // Если очистка выключена и текст уже отправлен - пропускаем
            if (text == lastSentText && !switchClearBeforeSend.isChecked) {
                tvStatus.text = "ℹ️ Text already sent"
                return
            }

            // --- ШАГ 4: Проверка URL сервера ---
            val serverUrl = etServerUrl.text?.toString()?.trim() ?: ""
            if (serverUrl.isEmpty()) {
                tvStatus.text = "⚠️ Enter server URL"
                Toast.makeText(this, "Enter server URL", Toast.LENGTH_SHORT).show()
                return
            }

            // --- ШАГ 5: Парсинг URL ---
            // Извлекаем хост и порт из URL
            var host = "127.0.0.1"
            var port = 5000
            try {
                // Убираем http:// или https://
                val url = serverUrl.replace("http://", "").replace("https://", "")
                val parts = url.split(":")
                host = parts[0]  // Получаем хост
                if (parts.size > 1) {
                    // Получаем порт (убираем всё после /)
                    val portPart = parts[1].split("/")[0]
                    port = portPart.toInt()
                }
            } catch (e: Exception) {
                tvStatus.text = "⚠️ Invalid URL format"
                Toast.makeText(this, "Invalid URL format", Toast.LENGTH_SHORT).show()
                return
            }

            // --- ШАГ 6: Сохранение настроек ---
            saveSettings()

            // --- ШАГ 7: Подготовка к отправке ---
            isSending = true
            lastSentText = text  // Запоминаем отправленный текст
            tvStatus.text = "📤 Sending..."

            // --- ШАГ 8: АСИНХРОННАЯ ОТПРАВКА ---
            // Используем Coroutine для отправки в фоновом потоке
            CoroutineScope(Dispatchers.IO).launch {
                var socket: java.net.Socket? = null
                var success = false
                var errorMessage = ""

                try {
                    // --- СОЗДАНИЕ И ПОДКЛЮЧЕНИЕ СОКЕТА ---
                    socket = java.net.Socket()
                    socket.soTimeout = 3000  // Таймаут чтения: 3 сек
                    socket.connect(java.net.InetSocketAddress(host, port), 2000)  // Таймаут подключения: 2 сек

                    // --- ФОРМИРОВАНИЕ JSON ---
                    val json = JSONObject()
                    json.put("text", text)
                    json.put("timestamp", System.currentTimeMillis())
                    json.put("device", "Android")

                    val jsonString = json.toString()
                    val dataBytes = jsonString.toByteArray(Charsets.UTF_8)
                    val dataLength = dataBytes.size

                    // --- ОТПРАВКА ДЛИНЫ (4 байта, big-endian) ---
                    val lengthBytes = byteArrayOf(
                        (dataLength shr 24).toByte(),
                        (dataLength shr 16).toByte(),
                        (dataLength shr 8).toByte(),
                        dataLength.toByte()
                    )

                    // --- ОТПРАВКА ДАННЫХ ---
                    val output = socket.getOutputStream()
                    output.write(lengthBytes)   // Отправляем длину
                    output.write(dataBytes)     // Отправляем JSON
                    output.flush()

                    // --- ПОПЫТКА ПРОЧИТАТЬ ОТВЕТ (fire-and-forget) ---
                    // Если ответа нет - считаем отправку успешной
                    try {
                        val input = socket.getInputStream()
                        val responseLengthBytes = ByteArray(4)
                        var bytesRead = 0
                        while (bytesRead < 4) {
                            val read = input.read(responseLengthBytes, bytesRead, 4 - bytesRead)
                            if (read < 0) break
                            bytesRead += read
                        }

                        // Если получили ответ - проверяем
                        if (bytesRead == 4) {
                            val responseLength = ((responseLengthBytes[0].toInt() and 0xFF) shl 24) or
                                    ((responseLengthBytes[1].toInt() and 0xFF) shl 16) or
                                    ((responseLengthBytes[2].toInt() and 0xFF) shl 8) or
                                    (responseLengthBytes[3].toInt() and 0xFF)

                            if (responseLength > 0 && responseLength < 1024) {
                                val responseBytes = ByteArray(responseLength)
                                var readTotal = 0
                                while (readTotal < responseLength) {
                                    val read = input.read(responseBytes, readTotal, responseLength - readTotal)
                                    if (read < 0) break
                                    readTotal += read
                                }

                                val response = String(responseBytes, 0, readTotal, Charsets.UTF_8)
                                // Даже если ответ не содержит "ok", отправка могла быть успешной
                                success = true
                            } else {
                                success = true  // Некорректный ответ, но данные отправлены
                            }
                        } else {
                            success = true  // Нет ответа, но данные отправлены
                        }
                    } catch (e: SocketTimeoutException) {
                        // Таймаут при чтении ответа - данные отправлены
                        success = true
                    } catch (e: Exception) {
                        // Ошибка при чтении ответа - данные отправлены
                        success = true
                    }

                } catch (e: UnknownHostException) {
                    errorMessage = "Server unavailable / Сервер недоступен"
                } catch (e: ConnectException) {
                    errorMessage = "No connection to server / Нет соединения с сервером"
                } catch (e: SocketTimeoutException) {
                    errorMessage = "Connection timeout / Таймаут соединения"
                } catch (e: IOException) {
                    errorMessage = "Error: ${e.message} / Ошибка: ${e.message}"
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Unknown error / Неизвестная ошибка"
                } finally {
                    try {
                        socket?.close()  // Закрываем сокет
                    } catch (e: Exception) {}
                }

                // --- ШАГ 9: ОБНОВЛЕНИЕ UI ПОСЛЕ ОТПРАВКИ ---
                withContext(Dispatchers.Main) {
                    isSending = false

                    if (success) {
                        tvStatus.text = "✅ Sent!"
                        // Очищаем текст после успешной отправки (если включена опция)
                        if (switchClearBeforeSend.isChecked) {
                            accumulatedText = ""
                            tvResult.text = "Recognized text will appear here..."
                            lastSentText = ""
                        }
                    } else {
                        tvStatus.text = "❌ $errorMessage"
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                        lastSentText = ""  // Сбрасываем для возможности повторной отправки
                    }
                }
            }
        } catch (e: Exception) {
            // Обработка ошибок в основном потоке
            isSending = false
            tvStatus.text = "❌ Error: ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // ================================================================
    // 12. РАБОТА С РАЗРЕШЕНИЯМИ
    // ================================================================

    /**
     * checkPermissions() - Проверка разрешений
     *
     * Проверяет наличие разрешения на запись аудио.
     * Если разрешения нет - запрашивает его у пользователя.
     *
     * Разрешение: Manifest.permission.RECORD_AUDIO
     * Код запроса: PERMISSION_REQUEST_RECORD_AUDIO
     */
    private fun checkPermissions() {
        try {
            // Проверяем наличие разрешения
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                // Если разрешения нет - запрашиваем
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_RECORD_AUDIO
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * onRequestPermissionsResult() - Обработка результата запроса разрешений
     *
     * Вызывается после ответа пользователя на запрос разрешения.
     *
     * @param requestCode Код запроса
     * @param permissions Массив запрашиваемых разрешений
     * @param grantResults Результаты запроса
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение получено
                    Toast.makeText(this, "Permission granted / Разрешение получено", Toast.LENGTH_SHORT).show()
                } else {
                    // Разрешение не получено - отключаем кнопки
                    Toast.makeText(this, "Permission required for operation / Разрешение необходимо для работы", Toast.LENGTH_LONG).show()
                    btnListen.isEnabled = false
                    btnContinuous.isEnabled = false
                }
            }
        }
    }

    // ================================================================
    // 13. ЖИЗНЕННЫЙ ЦИКЛ: onPause, onStop, onDestroy
    // ================================================================

    /**
     * onPause() - Вызывается при приостановке активности
     *
     * Сохраняет настройки перед уходом в фон.
     */
    override fun onPause() {
        super.onPause()
        saveSettingsOnExit()
    }

    /**
     * onStop() - Вызывается при остановке активности
     *
     * Сохраняет настройки перед остановкой.
     */
    override fun onStop() {
        super.onStop()
        saveSettingsOnExit()
    }

    /**
     * onDestroy() - Вызывается при уничтожении активности
     *
     * Освобождает ресурсы:
     * - Уничтожает SpeechRecognizer
     * - Отменяет таймер паузы
     * - Сохраняет настройки
     */
    override fun onDestroy() {
        super.onDestroy()
        try {
            // Освобождаем распознаватель речи
            speechRecognizer?.destroy()
            // Отменяем таймер
            cancelSilenceTimer()
            // Сохраняем настройки
            saveSettingsOnExit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}