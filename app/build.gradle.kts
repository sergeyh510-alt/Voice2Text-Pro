/*
 * ================================================================
 * Voice2Text Pro - build.gradle.kts (Module: app)
 * ================================================================
 *
 * 👨‍💻 Разработчик: Сергей Чекрыжов
 * 📧 Email: sergeyh510@gmail.com
 * 🐙 GitHub: https://github.com/sergeyh510-alt
 * 💡 Программа создана с ❤️ для удобства пользователей
 *
 * ================================================================
 *
 * ОПИСАНИЕ ЗАВИСИМОСТЕЙ:
 * ---------------------
 *
 * БАЗОВЫЕ ЗАВИСИМОСТИ (AndroidX):
 * - core-ktx          - Kotlin расширения для Android Core
 * - appcompat         - Поддержка старых версий Android
 * - material          - Material Design компоненты
 * - constraintlayout  - Гибкий макет
 * - activity          - Улучшенная работа с Activity
 *
 * ДОПОЛНИТЕЛЬНЫЕ ЗАВИСИМОСТИ:
 * - kotlinx-coroutines-android - Асинхронность (для отправки на сервер)
 * - org.json:json    - Работа с JSON (уже есть в Android SDK)
 *
 * ТЕСТИРОВАНИЕ:
 * - junit             - Модульные тесты
 * - androidx.test.*   - Инструментальные тесты
 * - espresso          - UI-тесты
 *
 * ================================================================
 *
 * НАСТРОЙКИ ПРОЕКТА:
 * -----------------
 * - compileSdk = 34      - Android 14 (Upside Down Cake)
 * - minSdk = 23          - Android 6.0 (Marshmallow) - для разрешений
 * - targetSdk = 34       - Android 14
 * - jvmTarget = 17       - Java 17 для совместимости
 * - namespace = "com.example.my_speek" - Уникальный идентификатор
 *
 * ================================================================
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // Уникальный идентификатор приложения (обратное доменное имя)
    namespace = "com.example.my_speek"

    // Версия Android SDK для компиляции (Android 14)
    compileSdk = 34

    defaultConfig {
        // Уникальный ID приложения в Google Play Store
        applicationId = "com.example.my_speek"

        // Минимальная версия Android (Android 6.0 Marshmallow)
        // API 23 - добавил поддержку разрешений во время выполнения
        minSdk = 23

        // Целевая версия Android (Android 14)
        targetSdk = 34

        // Версия приложения
        versionCode = 1
        versionName = "1.0"

        // Инструмент для запуска тестов
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Настройки сборки
    buildTypes {
        // Релизная сборка (для публикации)
        release {
            // Отключение обфускации (ProGuard)
            isMinifyEnabled = false

            // Правила ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        // Отладочная сборка (используется по умолчанию)
        debug {
            // Включаем отладку
            isDebuggable = true
        }
    }

    // Настройки совместимости Java
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Настройки Kotlin
    kotlinOptions {
        jvmTarget = "17"
    }

    // Настройки сборки APK (опционально)
    packaging {
        resources {
            // Исключаем дублирующиеся файлы
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// ================================================================
// ЗАВИСИМОСТИ
// ================================================================

dependencies {
    // ========== БАЗОВЫЕ ЗАВИСИМОСТИ (AndroidX) ==========

    // Kotlin расширения для Android Core
    // https://developer.android.com/kotlin/ktx
    implementation("androidx.core:core-ktx:1.12.0")

    // Поддержка старых версий Android (AppCompat)
    // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design компоненты
    // https://material.io/develop/android
    implementation("com.google.android.material:material:1.11.0")

    // ConstraintLayout для гибких макетов
    // https://developer.android.com/jetpack/androidx/releases/constraintlayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Activity KTX (расширения для Activity)
    // https://developer.android.com/jetpack/androidx/releases/activity
    implementation("androidx.activity:activity:1.8.2")

    // ========== ДОПОЛНИТЕЛЬНЫЕ ЗАВИСИМОСТИ ==========

    // Kotlin Coroutines для асинхронной работы
    // Используется для отправки данных на сервер без блокировки UI
    // https://kotlinlang.org/docs/coroutines-overview.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ========== ВСТРОЕННЫЕ В ANDROID SDK (НЕ ТРЕБУЮТ ДОБАВЛЕНИЯ) ==========
    //
    // ✅ org.json:JSONObject - работа с JSON (есть в Android SDK)
    // ✅ java.net.Socket - TCP-сокеты (есть в Android SDK)
    // ✅ android.speech.SpeechRecognizer - распознавание речи (есть в Android SDK)
    // ✅ android.os.Handler - таймеры (есть в Android SDK)
    // ✅ android.content.SharedPreferences - хранение настроек (есть в Android SDK)

    // ========== ТЕСТИРОВАНИЕ ==========

    // JUnit 4 для модульных тестов
    // https://junit.org/junit4/
    testImplementation("junit:junit:4.13.2")

    // AndroidX Test для инструментальных тестов
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // Espresso для UI-тестов
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ========== ОПЦИОНАЛЬНЫЕ ЗАВИСИМОСТИ (для будущих функций) ==========

    // Можно добавить при необходимости:
    // implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    // implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    // implementation("com.squareup.okhttp3:okhttp:4.12.0") - если понадобится HTTP-клиент
}

// ================================================================
// КОНЕЦ ФАЙЛА
// ================================================================