

<!-- ============================================================
     Voice2Text Pro - README.md
     ============================================================
     👨‍💻 Developer: Sergey Chekryzhov
     📧 Email: sergeyh510@gmail.com
     🐙 GitHub: https://github.com/sergeyh510-alt
     💡 Made with ❤️ for user convenience
     ============================================================ -->

<div align="center">

# 🎙️ Voice2Text Pro

### Speech Recognition App with Server Sending

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0-blue.svg)](https://github.com/sergeyh510-alt/Voice2Text-Pro)

</div>

---

## 📖 Table of Contents / Содержание

- [🇬🇧 English](#english)
- [🇷🇺 Русский](#russian)
- [📡 Protocol / Протокол](#protocol--протокол)
- [📞 Contacts / Контакты](#contacts--контакты)

---

<a name="english"></a>
## 🇬🇧 English

### 📱 About

**Voice2Text Pro** is an Android application for speech recognition with the ability to send text to a server via TCP sockets.

> 👨‍💻 **Developer:** Sergey Chekryzhov  
> 📧 **Email:** sergeyh510@gmail.com  
> 🐙 **GitHub:** [sergeyh510-alt](https://github.com/sergeyh510-alt)

---

### ✨ Features

- 🎤 **Speech Recognition** — uses Google Speech Recognizer
- 🔄 **Continuous Mode** — for long dictations
- 📤 **Server Sending** — via TCP sockets with JSON format
- ⏱️ **Auto-Send by Pause** — automatic sending after speech ends
- 🧹 **Clear Before Send** — optional text clearing
- 💾 **Settings Persistence** — saved between sessions
- 📖 **Built-in Help** — in Russian and English

---

### 📸 Screenshots

| Main Screen | Help Dialog |
|:---:|:---:|
| ![Main Screen](screenshots/main.png) | ![Help](screenshots/help.png) |

*(Screenshots will be added soon)*

---

### 🛠️ Technologies

| Technology | Description |
|------------|-------------|
| **Kotlin** | Programming language |
| **Android SDK** | Development platform |
| **SpeechRecognizer** | Speech recognition (Google Voice) |
| **TCP Sockets** | Data sending |
| **Coroutines** | Asynchronous operations |
| **SharedPreferences** | Settings storage |

---

### 📡 Protocol

[4 bytes: JSON length] + [JSON data in UTF-8]
text


#### JSON Format:
```json
{
  "text": "recognized text",
  "timestamp": 1234567890,
  "device": "Android"
}
```
### nstallation

### Requirements:

*    Android Studio 2022.3.1+

*    Android SDK 23+ (Android 6.0+)

*    Gradle 8.0+

## Steps:

###    Clone the repository:

```bach

it clone https://github.com/sergeyh510-alt/Voice2Text-Pro.git
cd Voice2Text-Pro
```
###    Open in Android Studio:

*    File → Open → select project folder

*    Build and run:

*    Click Run (▶️) or Shift + F10

📱 Usage
Button	Action
🎤 Speak	Single speech recognition
🔄 Continuous	Continuous recognition mode
📤 SEND	Send text to server
🗑️ Clear	Clear text
❓ Help	Quick help
📖 Full Help	Full documentation
💾 Save Settings	Save current settings
📁 Project Structure
text

* app/
* ├── src/
* │   ├── main/
* │   │   ├── java/com/example/my_speek/
* │   │   │   └── MainActivity.kt
* │   │   ├── res/
* │   │   │   ├── layout/
* │   │   │   │   └── activity_main.xml
* │   │   │   └── values/
* │   │   │       ├── colors.xml
* │   │   │       └── strings.xml
* │   │   └── AndroidManifest.xml
* │   └── ...
* ├── build.gradle.kts
* └── ...

## 🤝 Contributing

*    Fork the repository

*    Create a feature branch (git checkout -b feature/amazing)

*    Commit changes (git commit -m 'Add amazing feature')

*    Push to branch (git push origin feature/amazing)

*    Open a Pull Request

### 📝 License

* MIT License — see LICENSE file

<a name="russian"></a>
* 🇷🇺 Русский
* 📱 О приложении

### Voice2Text Pro — это Android-приложение для распознавания речи с возможностью отправки текста на сервер через TCP-сокеты.

    👨‍💻 Разработчик: Сергей Чекрыжов
    📧 Email: sergeyh510@gmail.com
    🐙 GitHub: sergeyh510-alt

### ✨ Возможности

*    🎤 Распознавание речи — использует Google Speech Recognizer

*    🔄 Непрерывный режим — для длительных диктовок

*    📤 Отправка на сервер — через TCP-сокеты с JSON-форматом

*    ⏱️ Автоотправка по паузе — автоматическая отправка после окончания речи

*    🧹 Очистка перед отправкой — опционально

*    💾 Сохранение настроек — между сессиями

*    📖 Встроенная справка — на русском и английском языках

### 📸 Скриншоты
Главный экран	Диалог справки
"https://screenshots/main.png	https://screenshots/help.png"

(Скриншоты будут добавлены позже)


### 🛠️ Технологии
Технология	Описание
Kotlin	Язык программирования
Android SDK	Платформа разработки
SpeechRecognizer	Распознавание речи (Google Voice)
TCP Sockets	Отправка данных
Coroutines	Асинхронные операции
SharedPreferences	Хранение настроек
📡 Протокол
text

[4 байта: длина JSON] + [JSON-данные в UTF-8]

### Формат JSON:
json

{
  "text": "распознанный текст",
  "timestamp": 1234567890,
  "device": "Android"
}

###  🚀 Установка
###Требования:

*    Android Studio 2022.3.1+

*    Android SDK 23+ (Android 6.0+)

*    Gradle 8.0+

### Шаги:

####    Клонируйте репозиторий:

```bash

git clone https://github.com/sergeyh510-alt/Voice2Text-Pro.git
cd Voice2Text-Pro
```
###    Откройте в Android Studio:

*    File → Open → выберите папку проекта

*    Соберите и запустите:

*    Нажмите Run (▶️) или Shift + F10

*      📱  Использование
*        Кнопка	Действие
*      🎤 Speak	Одиночное распознавание
*      🔄 Continuous	Непрерывный режим
*      📤 SEND	Отправить текст на сервер
*      🗑️ Clear	Очистить текст
*      ❓ Help	Краткая справка
*      📖 Full Help	Полная документация
*      💾 Save Settings	Сохранить настройки
*      📁 Структура проекта
text

app/
* ├── src/
* │   ├── main/
* │   │   ├── java/com/example/my_speek/
* │   │   │   └── MainActivity.kt
* │   │   ├── res/
* │   │   │   ├── layout/
* │   │   │   │   └── activity_main.xml
* │   │   │   └── values/
* │   │   │       ├── colors.xml
* │   │   │       └── strings.xml
* │   │   └── AndroidManifest.xml
* │   └── ...
* ├── build.gradle.kts
* └── ...

### 🤝 Вклад в проект

    Форкните репозиторий

*    Создайте ветку для новой функции (git checkout -b feature/amazing)

*    Зафиксируйте изменения (git commit -m 'Add amazing feature')

*    Отправьте в ветку (git push origin feature/amazing)

*    Откройте Pull Request

### 📝 Лицензия

MIT License — см. файл LICENSE


* 📡 Protocol / Протокол

## English

### TCP Socket Protocol:
* Bytes	Description
* 0-3	JSON data length (big-endian, 4 bytes)
* 4-N	JSON data (UTF-8 encoded)

### JSON Structure:
json

{
  "text": "recognized text",
  "timestamp": 1234567890,
  "device": "Android"
}

### Русский

Протокол TCP-сокетов:
Байты	Описание
0-3	Длина JSON-данных (big-endian, 4 байта)
4-N	JSON-данные (в кодировке UTF-8)

### Структура JSON:
json

{
  "text": "распознанный текст",
  "timestamp": 1234567890,
  "device": "Android"
}

### контакты
* 📞 Contacts / Контакты
Contact	Link
Email	sergeyh510@gmail.com
GitHub	sergeyh510-alt
Project	Voice2Text-Pro


