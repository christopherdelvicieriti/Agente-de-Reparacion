<div align="center">
  <img src="https://github.com/christopherdelvicieriti/Agente-de-Reparacion/blob/master/server/src/public/logo.png?raw=true" alt="DHC Logo" width="120" height="138">
  <h1>DHC</h1>
  <p>
    <strong>Sistema de Gestión y Registro de Ingreso de Equipos Informáticos.</strong>
  </p>
</div>

---

<details>
  <summary><strong>Tabla de Contenidos</strong></summary>
  <ol>
    <li><a href="#-sobre-el-proyecto">Sobre el Proyecto</a></li>
    <li><a href="#%EF%B8%8F-stack-tecnológico">Stack Tecnológico</a></li>
    <li><a href="#-estructura-del-proyecto">Estructura del Proyecto</a></li>
    <li>
      <a href="#-guía-de-ejecución">Guía de Ejecución</a>
      <ul>
        <li><a href="#1-backend-nestjs--pm2">Backend (NestJS)</a></li>
        <li><a href="#2-frontend-android--generar-apk">Frontend (Android)</a></li>
      </ul>
    </li>
    <li><a href="#-autor">Autor</a></li>
  </ol>
</details>

---

## 📄 Sobre el Proyecto

**DHC** es una solución tecnológica desarrollada como **Trabajo de Titulación (Caso Práctico)** para optar por el título de *Tecnólogo Superior en Desarrollo de Aplicaciones Web* en el **Instituto Superior Tecnológico Internacional (ITI)**.

### 🎯 Objetivo
Proveer una herramienta móvil y un servidor centralizado que permita a los técnicos:
1.  **Digitalizar el ingreso** de equipos con trazabilidad completa.
2.  **Capturar evidencia fotográfica** para evitar reclamos injustificados.
3.  **Gestionar la ubicación física** de los equipos mediante un sistema de espacios visuales (colores y fotos).

---

## 🛠️ Stack Tecnológico

La solución implementa una arquitectura cliente-servidor desconectada de servicios externos (Local Network) para garantizar la privacidad.

### 📱 Frontend (Aplicación Móvil)
* **Lenguaje:** Kotlin
* **Framework UI:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel)
* **Networking:** Retrofit
* **Hardware:** Integración con CameraX (Escaneo QR y Fotos)

### 🖥️ Backend (API RESTful)
* **Runtime:** Node.js
* **Framework:** NestJS
* **Base de Datos:** SQLite (Persistencia local eficiente)
* **ORM:** TypeORM
* **Gestor de Procesos:** PM2

---

## 📂 Estructura del Proyecto

```text
delvicier-dhc/
├── android/                 # Proyecto Android Nativo (Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/com/delvicier/fixagent/
│   │   │   ├── data/        # Repositorios y Modelos
│   │   │   ├── network/     # Configuración API (Retrofit)
│   │   │   ├── ui/          # Pantallas y ViewModels (Compose)
│   │   │   └── utils/       # Utilidades (QR Analyzer, Modifiers)
│   └── build.gradle.kts
├── server/                  # API Backend (NestJS)
│   ├── src/
│   │   ├── auth/            # Módulo de Autenticación y Setup
│   │   ├── clients/         # Gestión de Clientes
│   │   ├── orders/          # Gestión de Órdenes de Trabajo
│   │   ├── machines/        # Gestión de Equipos y Evidencia
│   │   ├── spaces/          # Gestión de Ubicaciones Físicas
│   │   └── images/          # Controlador de Archivos Estáticos
│   ├── package.json
│   └── ecosystem.config.js  # Configuración para PM2
└── README.md
```

---

## 🚀 Guía de Ejecución

### 1. Backend (NestJS + PM2)

El servidor debe estar ejecutándose para que la aplicación móvil pueda realizar la configuración inicial (Setup) y la sincronización de datos, ya que el sistema de autenticación depende de la generación de un token inicial.

**Prerrequisitos:**
* **Node.js** (v20 o superior)
* **PM2** instalado globalmente (`npm install pm2 -g`)

**Pasos:**

1.  Navega al directorio del servidor:
    ```bash
    cd server
    ```
2.  Instala las dependencias:
    ```bash
    npm install
    ```
3.  Compila el proyecto (NestJS build):
    ```bash
    npm run build
    ```
4.  **Ejecutar con PM2:**
    Asegúrate de tener el archivo `ecosystem.config.js` en la raíz de `server/`. Ejecuta:
    ```bash
    pm2 start ecosystem.config.js
    ```
    *Esto iniciará la API en segundo plano, asegurando que se reinicie automáticamente si ocurre un error.*

5.  **Verificar logs (Importante):**
    En la primera ejecución, el servidor imprimirá en la consola el **Código QR de Setup** necesario para crear la cuenta de administrador.
    ```bash
    pm2 log
    ```

---

### 2. Frontend (Android - Generar APK)

Para instalar la aplicación en los dispositivos de los técnicos, debes generar el archivo `.apk`. El proyecto utiliza Gradle para la gestión del empaquetado.

**Prerrequisitos:**
* Java Development Kit (JDK) 19 o superior.
* Android SDK configurado.

**Pasos desde Terminal:**

1.  Navega al directorio de Android:
    ```bash
    cd android
    ```
2.  Dar permisos de ejecución al gradlew (Linux/Mac):
    ```bash
    chmod +x gradlew
    ```
3.  **Generar el APK (Debug):**
    Para pruebas en el emulador o dispositivo físico sin firma oficial.
    ```bash
    ./gradlew assembleDebug
    ```
    * El APK se generará en: `android/app/build/outputs/apk/debug/app-debug.apk`

---

## 👤 Autor

**Christopher Joel Delvicier Palacios**
* Carrera de Desarrollo de Aplicaciones Web
* **Instituto Superior Tecnológico Internacional (ITI)**