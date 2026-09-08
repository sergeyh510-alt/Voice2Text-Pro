

# 🎙️ Voice2Text Pro

[![🇬🇧 English](https://img.shields.io/badge/🇬🇧_English-README-blue?style=for-the-badge&logo=markdown&logoColor=white)](./README.md)
[![🇷🇺 Русский](https://img.shields.io/badge/🇷🇺_Русский-README-red?style=for-the-badge&logo=markdown&logoColor=white)](./README.ru.md)

---

### Speech Recognition App with Server Sending

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0-blue.svg)](https://github.com/sergeyh510-alt/Voice2Text-Pro)
[![Android](https://img.shields.io/badge/Android-6.0+-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org)
[![Status](https://img.shields.io/badge/status-stable-brightgreen.svg)](https://github.com/sergeyh510-alt/Voice2Text-Pro)

Speech recognition app with server sending capabilities
Android | Kotlin | Google Speech Recognizer | TCP Sockets



## 📖 Table of Contents

  *  [📱 About the Project](#About-the-Project])

  *  [✨ Features](#Features)

  *  [📸 Interface](#Interface)

  *  [🏗️ Architecture](#Architecture)

  *  [📡 Communication Protocol](#Communication-Protocol)

  *  [🚀 Installation](#Installatio)

  *  [📖 User Guide](#User-Guide)

  *  [🧪 Testing](#Testing)

  *  [📁 Project Structure](#roject-Structure)

  * [🛠️ Technologies](#Technologie)

  *  [📝 License](#License)

  *  [📞 Contacts](#Contacts)



## 📱 
## About the Project

Voice2Text Pro is an Android mobile application that converts speech to text and sends it to a server via TCP sockets. 
The app is designed for fast voice-based text input into any systems that support the TCP protocol.

## 🎯 
## Who Is This App For
|Category|	Application|
|---------------------|--------------------------|
Regular Users	|Fast voice-to-text input, sending notes to server, voice control of devices
Developers|	Testing voice systems, creating voice interfaces, integration with other projects
Business	|Data entry automation, voice-controlled warehouse management, order systems
Education	|Lecture dictation, pronunciation practice, subtitle creation
People with Disabilities|	Convenient text input without using a keyboard
#### 💡 Recommendation

The app has been tested in conjunction with the Windows application DeepSeek_Voice_Bridge. 
This setup allows voice input from the mobile device to be transmitted directly to the Windows environment, 
enabling voice-controlled interaction with local applications and services.


## ✨ 
## Features
|Feature|	Description|
|-----------------------------------|------------------------------------------|
🎤 Single Recognition	|Press a button and say one phrase — perfect for commands
🔄 Continuous Mode|	App listens constantly — ideal for long dictations
📤 Server Sending|	Send text to any server via TCP with JSON format
⏱️ Auto-Send on Pause|	Automatic sending after a configurable silence period
🧹 Clear Before Send|	Replace or accumulate text with each recognition
💾 Settings Persistence	|All settings saved via SharedPreferences
📖 Built-in Help|	Quick and full help in both Russian and English
🌐 Multi-language UI|	Supports Russian and English languages

## 📸 
## Interface
#### Main Application Screen

<img width="312" height="503" alt="screen2" src="https://github.com/user-attachments/assets/97e73b33-8e5f-4bf7-8813-3abe01338f79" />


```bash
┌──────────────────────────────────────────────────────┐
│  ┌─────────────┐  ┌─────────────────────────────┐  │
│  │ 🎤 SPEAK    │  │ 🔄 CONTINUOUS              │  │
│  │ (single)    │  │ (continuous mode)          │  │
│  └─────────────┘  └─────────────────────────────┘  │
├──────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────┐ │
│  │              📤 SEND                          │ │
│  │         (send to server)                     │ │
│  └────────────────────────────────────────────────┘ │
├──────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────────────────────┐  │
│  │ 🗑️ CLEAR   │  │ ❓ HELP                    │  │
│  │ (clear)     │  │ (quick help)               │  │
│  └─────────────┘  └─────────────────────────────┘  │
├──────────────────────────────────────────────────────┤
│  ⏱️ Delay (sec): [ 2 ]   🤖 Auto Send [ ● ON  ]   │
│  (pause delay)          (auto-send)                │
├──────────────────────────────────────────────────────┤
│  🧹 Clear before Send [ ● ON  ]  (only last text)  │
│  (clear before sending)                            │
├──────────────────────────────────────────────────────┤
│  🌐 Server: [ http://192.168.1.100:5000 ]          │
│  (server address)                                  │
├──────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────────────────────┐  │
│  │ 💾 SAVE     │  │ 📖 FULL HELP               │  │
│  │ SETTINGS    │  │ (full help)                 │  │
│  └─────────────┘  └─────────────────────────────┘  │
├──────────────────────────────────────────────────────┤
│  📌 Press Speak to start recognition               │
│  (status line)                                     │
├──────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────┐ │
│  │ Recognized text will appear here...           │ │
│  │                                               │ │
│  │ (recognized text output area)                 │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```
## Interface Elements Description


|№	|Element	|Purpose|
|----------------|--------------|------------------|
1|	🎤 SPEAK	|Start single speech recognition
2|	🔄 CONTINUOUS	Toggle continuous recognition mode
3|	📤 SEND	|Send current text to server
4|	🗑️ CLEAR|	Clear text field
5|	❓ HELP	|Display quick help
6|	⏱️ Delay|	Delay before auto-send (seconds)
7|	🤖 Auto Send|	Automatic sending after pause
8|	🧹 Clear before Send|	Clear text before sending
9|	🌐 Server	|Server URL for sending
10|	💾 SAVE SETTINGS|	Save current settings
11|	📖 FULL HELP|	Display full help
12|	Status line|	Display current state
13|	Text area	|Display recognized text

## 🏗️ 
## Architecture
#### The application follows a clean and simple architecture:
```bash
MainActivity
├── UI Elements
│   ├── Buttons (Speak, Continuous, Send, Clear, Help)
│   ├── Text Fields (Result, Status)
│   ├── Input Fields (Delay, Server URL)
│   └── Switches (Auto Send, Clear before Send)
├── SpeechRecognizer (Google Voice API)
│   ├── Single Recognition
│   └── Continuous Recognition
├── SharedPreferences (Settings Storage)
│   ├── Server URL
│   ├── Pause Delay
│   ├── Auto Send
│   └── Clear before Send
├── TCP Client (Coroutines)
│   ├── JSON Formatting
│   ├── Socket Sending
│   └── Error Handling
├── Pause Timer (Handler + Runnable)
│   ├── Start on speech end
│   ├── Cancel on speech start
│   └── Auto-send on timer
└── Help System (AlertDialog)
    ├── Quick Help
    └── Full Help
```

## Data Flow

<img width="405" height="892" alt="image" src="https://github.com/user-attachments/assets/305e4f86-63be-49a7-9ec3-7d4d8f5545ff" />


## 📡 
## Communication Protocol
#### TCP Socket Protocol
|Bytes|Description|
|-----------------|-----------------|
0–3	|JSON data length (big-endian, 4 bytes)
4–N	|JSON data (UTF-8 encoded)

### JSON Format
```bash
{
  "text": "recognized text",
  "timestamp": 1234567890,
  "device": "Android"
}
```
### Example Packet
[00 00 00 3A] [{"text":"Hello world","timestamp":1712345678,"device":"Android"}]

### Example Test Server (Python)
```bash
import socket
import socket

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(('0.0.0.0', 5000))
server.listen(1)

print("Waiting for connection...")

while True:
    conn, addr = server.accept()
    print(f"Connected: {addr}")
    
    # Read length (4 bytes)
    length_bytes = conn.recv(4)
    if not length_bytes:
        break
    
    length = int.from_bytes(length_bytes, 'big')
    print(f"Data length: {length} bytes")
    
    # Read data
    data = conn.recv(length)
    json_text = data.decode('utf-8')
    print(f"Received: {json_text}")
    
    conn.close()
```
## 🚀 
## Installation
#### System Requirements

|Component|	Minimum Version|
|-------------------|-------------------|
Operating System|	Android 6.0+
Android Studio|	2022.3.1+
Android SDK|	23+
Gradle|	8.0+
Kotlin|	1.9.0+

## Step-by-Step Installation
### 1️⃣ 
### Clone the Repository
```bash
git clone https://github.com/sergeyh510-alt/Voice2Text-Pro.git
cd Voice2Text-Pro
```
### 2️⃣ 
### Open in Android Studio
```bash
File → Open → select the project folder
```
### 3️⃣ 
### Build and Run

Click the Run button (▶️) or use the shortcut Shift + F10

### 4️⃣ 
### Grant Permissions

On first launch, the app will request microphone permission — you must grant it.

## 📖 
## User Guide

### Quick Start

* Configure the server — enter IP address and port (e.g., http://192.168.1.100:5000)

* Select mode — single (SPEAK) or continuous (CONTINUOUS)

* Configure parameters — delay, auto-send, clear before send

* Press SPEAK and start speaking

* Text appears on screen → automatically or manually sent to server

### Detailed Mode Descriptions
#### 🎤 
#### Single Mode (SPEAK)

  *  Press the SPEAK button

  *  Say one phrase

  *  The app recognizes and displays the text

  *  Suitable for short commands and individual phrases

#### Example: You say "Turn on the light" → text appears on screen

### 🔄 
### Continuous Mode (CONTINUOUS)

   * Press the CONTINUOUS button to start

   * The app listens constantly

   * Text appears as it's recognized

   * Press CONTINUOUS again to stop

   * Ideal for lectures, dictations, monologues

#### Example: You're giving a lecture → the app transcribes everything you say

## Settings
|Parameter|	Description|
|-----------------|--------------------|
⏱️ Delay|	Delay in seconds before auto-send after speech ends
🤖 Auto Send	|Enable/disable automatic sending
🧹 Clear before Send|	ON: each new text replaces the previous one. OFF: texts accumulate with new lines
🌐 Server	|Server address for sending data

### Settings Persistence

#### All settings are saved automatically when:

  *  Pressing the SAVE SETTINGS button

  *  Exiting the application

  *  Input fields lose focus

## 🧪 
## Testing
### Manual Testing
|Test Case|	Expected Result|
|-------------|----------------------|
Single Recognition|	Text appears in result field
Continuous Mode|	Text accumulates/replaces according to settings
Auto-Send	|Text sent after pause
SEND Button|	Manual send works
Settings Persistence	|Settings restored after restart

### Server Testing

To test server communication, use the simple TCP server example in Python (provided above).

### Automated Testing
```bash
./gradlew connectedAndroidTest
```
### 📁 
### Project Structure
```bash
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/my_speek/
│   │   │   └── MainActivity.kt          # Main logic
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    # UI layout
│   │   │   ├── values/
│   │   │   │   ├── colors.xml           # Colors
│   │   │   │   └── strings.xml          # Strings
│   │   │   └── drawable/               # Image resources
│   │   └── AndroidManifest.xml          # Permissions and metadata
│   └── test/                           # Unit tests
├── build.gradle.kts                     # Build configuration
├── gradle.properties                    # Gradle settings
├── settings.gradle.kts                  # Project settings
└── README.md                           # Documentation
```

### 🛠️ 
### Technologies
|Technology	|Purpose|
|-----------------|----------------------|
Kotlin	|Primary programming language
Android SDK|	UI and system integration
SpeechRecognizer|Speech recognition (Google Voice)
Coroutines|	Asynchronous TCP sending
TCP Sockets|	Server communication
SharedPreferences	|Settings storage
Handler + Runnable|	Pause timer management

### 📝 
### License

This project is distributed under the MIT License — see the LICENSE file for details.

### 📞 
### Contacts
* Contact: Sergey Chekryzhov
* Email: sergeyh510@gmail.com
* GitHub: sergeyh510-alt
* Project: Voice2Text-Pro
* LinkedIn: www.linkedin.com/in/sergey-chekryzhov-a38778217
* Telegram: @SergeyChekryzhov
