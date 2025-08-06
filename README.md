# 🛫 FlyCheck — App de Listas de Verificación para Aviación y Simulación Aérea.

**FlyCheck** es una aplicación Android nativa desarrollada con **Jetpack Compose** y **Kotlin**, diseñada para facilitar la creación y gestión de *checklists* (listas de verificación) específicas para aviones y aerolíneas.

Pensada para entornos aeronáuticos (pilotos virtuales, simuladores o incluso formación real), permite documentar procedimientos técnicos con imágenes, instrucciones y secciones personalizadas.

> 🎯 Proyecto desarrollado como parte de mi porfolio como desarrollador Android, con vistas a su futura publicación en Google Play.

---

## ✨ Características principales

- 🎛️ EDITOR COMPLETO: Secciones, Subsecciones, Ítems y contenido multimedia
- 🖼️ Soporte de imágenes por ítem (galería o cámara): cada item puede tener asociada una imagen para su localización o simplemente anotaciones importantes.
- 🧠 Campos informativos con descripciones técnicas: Campo de información técnica o remarcable sobre algún Ítem en particular.
- 💾 Exportación como archivo `.zip`: Pensado en el desarrollo del "Displayer /Reproductor" de las checklist y poder compartir con la comunidad o entre usuarios, los archivos .ZIP incluyen:
  - Un archivo `.json` estructurado
  - Una carpeta `/images` con los recursos multimedia

- 📤 Compartir directamente desde la app o guardar localmente
- 📲 Compatible con Android 7.0 hasta Android 14+
- 📱 Splash screen animado con branding personalizado
- 🔔 Notificaciones con acciones rápidas al exportar

---

## 🛠️ Tecnologías y librerías usadas

- **Jetpack Compose** — UI declarativa moderna
- **Kotlin Coroutines & Flow** — Gestión reactiva del estado
- **Hilt (DI)** — Inyección de dependencias
- **Navigation Compose** — Navegación declarativa
- **MediaStore API** — Gestión moderna de archivos
- **FileProvider** — Compatibilidad de archivos en Android 7+
- **Material 3** — Sistema de diseño moderno (Material You)
- **Notificaciones** — Integración con NotificationManager
- **JSON Serialization** — Serialización con kotlinx.serialization

---

## 📦 Estructura de exportación

Cuando se exporta una plantilla, se genera un `.zip` con esta estructura:

📁 checklist_Boeing738.zip
├── checklist.json
└── images/
├── item_1.jpg
├── item_2.jpg
└── ...

---

## 📸 Capturas de pantalla

<div style="display: flex; overflow-x: auto; gap: 10px; padding: 10px 0;">
  <img src="screenshots/home.png" alt="Pantalla de inicio" width="200"/>
  <img src="screenshots/preeditor.png" alt="Pre-editor" width="200"/>
  <img src="screenshots/editor1.png" alt="Editor 1" width="200"/>
  <img src="screenshots/editor2_imagenitem.png" alt="Editor con imagen" width="200"/>
  <img src="screenshots/editor2_imagenitemdemo.png" alt="Editor con info extendida" width="200"/>
  <img src="screenshots/exportdemo.png" alt="Exportación ZIP" width="200"/>
  <img src="screenshots/exportnotification.png" alt="Notificación de exportación" width="200"/>
</div>
---

## 🚀 Proyección a futuro

FlyCheck está diseñado para evolucionar hacia una solución más amplia con:

- ☁️ Sincronización en la nube (Firebase o propio backend)
- 🌐 Repositorio comunitario de plantillas (compartición online)
- 🛫 Base de datos offline integrada
- 📲 Publicación oficial en **Google Play**
- 🧩 Soporte para nuevos tipos de bloques (checkboxes dinámicos, temporizadores, etc.)

---

## 👨‍💻 Sobre el desarrollador

Desarrollado por **Sergio M.**  
📍 España  
🎓 Titulación como Técnico Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).  
📱 Apasionado del desarrollo Android, Jetpack Compose y UI/UX. (Además de la simulación aérea 😜)

---

🔒 Código fuente con derechos reservados

El contenido de este repositorio está protegido por derechos de autor y es propiedad exclusiva de Sergio M. No se permite el uso, modificación, redistribución ni comercialización del código sin autorización previa por escrito.

Este proyecto está destinado únicamente a fines demostrativos en el contexto de un porfolio profesional.

---

## 🤝 ¿Quieres colaborar?

- ¿Te gusta el proyecto? ¡Dale una estrella en GitHub!
- ¿Tienes ideas o sugerencias? Abre un *issue* o *pull request*.
- ¿Eres piloto o estudiante? ¡Contáctame para probar futuras versiones beta!

---

> 💬 *FlyCheck es más que una app, es una herramienta diseñada con pasión por la aviación y el desarrollo móvil.*
