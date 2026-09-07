
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

- [English](#english)
- [Русский](#russian)
- [📡 Protocol / Протокол](#protocol--протокол)
- [📞 Contacts / Контакты](#contacts--контакты)

---
# 🎙️ Voice2Text Pro
**Recommendations:** The app has been tested in conjunction with the Windows application DeepSeek_Voice_Bridge (repository: https://github.com/sergeyh510-alt/DeepSeek_Voice_Bridge). 
This setup allows seamless voice input from the mobile device to be transmitted directly to the Windows environment, enabling voice-controlled interaction with local applications and services.

**Voice2Text Pro** — is a mobile application for Android that converts voice into text and sends it to a server via TCP sockets. The app is designed for fast voice-based text input into any systems that accept data over the TCP protocol. Instead of typing on a keyboard, you simply speak, and the app turns your speech into text. This is especially convenient when your hands are busy, when you need to quickly jot something down, for people with disabilities, or for dictating long texts. The app sends the recognized text to a server, which can be used to control a smart home (voice commands), transmit data to CRM systems, fill out forms on a website, control robots and devices, or keep a voice diary. Developers can use Voice2Text Pro as a voice input method for their applications, a voice control interface, or a tool for testing voice systems.

---

## 🎤 Main functions

**Speak** Mode (Single Recognition) — you press a button, say one phrase, and the app recognizes it and displays the text. Use it for short commands, questions, or individual phrases. Example: you say "Turn on the light" — the text appears on the screen.

**Continuous** Mode (Continuous Recognition) — the app listens to you constantly and recognizes speech continuously. Use it for long dictations, lectures, or monologues. Example: you're giving a lecture, and the app transcribes everything you say.

Send to Server (SEND) — sends the recognized text to the specified server via TCP sockets, transmitting data in JSON format so that other systems can use your text.

**Auto Send**  on Pause — automatically sends the text when you pause speaking for a set amount of time. Adjust the delay (1–10 seconds) so you don't have to press the "Send" button every time.

**Clear** before Send — if enabled, each new text replaces the previous one (only the last phrase is kept). If disabled, text accumulates, and new phrases are added on a new line.

---

## 📡 How does sending to the server work

The application connects to the server via a TCP socket, generates JSON with the text: `{"text": "recognized text", "timestamp": 1234567890, "device": "Android"}`, sends the data length (4 bytes) and the data itself. The server receives and processes the text. To work, the server must listen to the TCP port (default 5000) and receive data using the [length] protocol. + [JSON] and process the received JSON.

---

## 🎯 Who is this app for

**For regular users** — fast voice-to-text input, sending notes to a server, voice control of devices.

**For developers** — testing voice systems, creating voice interfaces, integration with other projects.

**For business** — data entry automation, voice-controlled warehouse management, voice ordering systems.

**For education** — dictation of lectures and notes, pronunciation practice, subtitle creation.

---

## 🚀 How to Use

Setup before use: specify the server — enter the IP address and port of the server, select the mode — single or continuous, configure the settings — delay, auto send, and clear.

Workflow: press "Speak" or "Continuous", start speaking, the text appears on the screen, the app sends the text to the server, repeat until you're done.

## Saving settings: 
all settings are saved automatically — server address, pause delay, auto-send mode, and clear mode. The app will remember your settings the next time you launch it.
## 📊 Advantages

Speed — speak faster than you type. Accuracy — Google-powered recognition. Flexibility — two operating modes. Versatility — send to any TCP server. Reliability — settings are saved. 
Clarity — built-in help in two languages.
## 🛠️ System Requirements

For the app to work, you need: Android 6.0+ (operating system), microphone (for voice recording), internet (for speech recognition and sending), server (for receiving text, optional).






<a name="english"></a>
## English

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

<img width="312" height="503" alt="screen2" src="https://github.com/user-attachments/assets/bb2eef5b-23ac-4828-86a3-97030f0b41a8" />

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
*  Русский
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
### Требования:

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
* Contact	Sergey Chekryzhov
* Email	sergeyh510@gmail.com
* GitHub	sergeyh510-alt
* Project	Voice2Text-Pro
* LinkedIn: www.linkedin.com/in/sergey-chekryzhov-a38778217
* Telegram: @SergeyChekryzhov


