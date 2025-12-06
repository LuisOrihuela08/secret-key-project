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
<img width="1353" height="838" alt="Image" src="https://github.com/user-attachments/assets/5a1ca289-2754-4d24-90bb-2fdd1f91172a" />



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
