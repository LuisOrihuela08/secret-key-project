<h1 align="center"> 🔐 Secret Key Project - Credential Manager </h1>

API REST desarrollada con Java 17 y Spring Boot para la gestión segura de credenciales de forma local y generación de reportes en PDF y Excel. 

## ✨ Carateristicas
- ✅ CRUD completo de credenciales
- ✅ Generación de reportes PDF y Excel
- ✅ Filtro por nombre de plataforma
- ✅ API RESTful documentada
- ✅ Validación de datos
- ✅ Manejo centralizado de excepciones
- ✅ Arquitectura en capas (Controller, Service, Repository)
- ✅ Logging completo con SLF4J para monitoreo y debugging en tiempo real

## 🛠️ Tecnologías

- Java 17
- Spring Boot 4
- Mongo DB
- OpenPDF (Generación de PDF)
- POI (Generación de Excel)
- Maven (Gestión de dependencias)
- Lombok
- Docker
- Swagger/OpenAPI (Documentación)

## 🚀 Instalación
Clonar el repositorio del proyecto
```bash
   git clone https://github.com/LuisOrihuela08/secret-key-project.git
   cd secret-key-project
   ```
Crear un archivo .env para alojar tus credenciales de MongoDB:
| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `MONGO_USERNAME` | Usuario de MongoDB | `admin` |
| `MONGO_PASSWORD` | Contraseña de MongoDB | `miPasswordSeguro123` |
| `MONGO_DATABASE` | Nombre de la base de datos | `credentials_db` |

Y ejecutar el proyecto en local:
```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 📸 Capturas
1️⃣ Swagger

La API cuenta con documentación generada automáticamente con Swagger. Puedes acceder a la interfaz de los endpoints desde la siguiente URL local:
```bash
   http://localhost:8080/swagger-ui-custom.html
   ```
<img width="1090" height="1020" alt="Image" src="https://github.com/user-attachments/assets/a7d64656-caf9-4db8-b5c6-4ba05f10a296" />

Desde alli, podemos probar todos los endpoints disponibles en la API. Pero, primero tenemos que crear una cuenta o logearnos si es necesario.
Una vez autenticados, podemos gestionar nuestras credenciales y generar reportes.

<img width="1080" height="436" alt="Image" src="https://github.com/user-attachments/assets/b83c35b7-1a4f-484b-ab46-3d8ed4b112db" />
<img width="1094" height="1039" alt="Image" src="https://github.com/user-attachments/assets/c34ded6d-c4a7-46a8-8655-034f228b96b4" />
<img width="1096" height="1106" alt="Image" src="https://github.com/user-attachments/assets/18fbc726-5636-4887-8532-15e544863a76" />

Y listo, podemos realizar todas las operaciones CRUD sobre nuestras credenciales.
<img width="1091" height="893" alt="Image" src="https://github.com/user-attachments/assets/310e25a5-9fc5-45ce-866c-7895978adb86" />


2️⃣ Reportes
<img width="907" height="592" alt="Image" src="https://github.com/user-attachments/assets/c7059af1-4b5a-4f83-bd79-627975d13270" />
<img width="700" height="327" alt="Image" src="https://github.com/user-attachments/assets/5f81855a-ae63-4d46-b65c-a12c213b3687" />

## 👨‍💻 Autor

<div align="center">

**Luis Orihuela** - *FullStack Developer*



🌐 **Portfolio:** [luisorihuela.me](https://luisorihuela.me)  
💼 **GitHub:** [@LuisOrihuela08](https://github.com/LuisOrihuela08)  

---

⭐ *Si este proyecto te resultó útil, considera darle una estrella en GitHub*

<sub>Made with ❤️ in Peru 🇵🇪 | © 2025</sub>

</div>
