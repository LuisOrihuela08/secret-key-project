<h1 align="center"> 🔐 Secret Key Project - Credential Manager </h1>

<div align="center">
  <img width="633" height="633" alt="Image" src="https://github.com/user-attachments/assets/9288d393-fc92-421d-a9ec-1e159a1433ce" />
  <h2>This project allows you to manage your credentials securely</h2>
</div>

REST API developed with Java 17 and Spring Boot for secure local credential management and PDF and Excel report generation

## ✨ Features
- ✅ CRUD completo de credenciales
- ✅ Generación de reportes PDF y Excel
- ✅ Filtro por nombre de plataforma
- ✅ API RESTful documentada
- ✅ Validación de datos
- ✅ Manejo centralizado de excepciones
- ✅ Arquitectura en capas (Controller, Service, Repository)
- ✅ Logging completo con SLF4J para monitoreo y debugging en tiempo real

## 🛠️ Technologies

- Java 17
- Spring Boot 4
- Mongo DB
- OpenPDF (Generate PDF)
- POI (Generate Excel)
- Maven
- Lombok
- Docker
- Swagger/OpenAPI (Documentation)

## 🚀 Installation
Clone the project repository
```bash
   git clone https://github.com/LuisOrihuela08/secret-key-project.git
   cd secret-key-project
   ```
You can leave the credentials as default to launch the project with Docker Compose and build the project locally. The URI would not undergo any modifications.
```bash
   mongodb://mongo:mongo@mongodb:27017/secretkey?authSource=admin
   ```


Configure and launch a MongoDB container using Docker:
You can leave the jwt, user, password, and database name as default to launch the docker-compose.yml.

| Variable | Description                 | Example |
|----------|----------------------------|--|
| `MONGO_INITDB_ROOT_USERNAME` | MongoDB user        | `mongo` |
| `MONGO_INITDB_ROOT_PASSWORD` | MongoDB password      | `mongo` |
| `MONGO_INITDB_DATABASE` | Database name | `secretkey` |
| `JWT_SECRET` | jwt (BASE64)               |  |

If you want to generate a new JWT_SECRET in BASE64, you can use the following command:
```bash
   node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
   python -c "import secrets, base64; print(base64.b64encode(secrets.token_bytes(32)).decode())"
   ```

Then run the following commands to launch the project in development and production mode:

```bash
   docker compose -f docker-compose.dev.yml up -d
   docker compose -f docker-compose.prod.yml up -d
   ```


Create an .env file to store your MongoDB credentials and jwt if you want to customize them:

| Variable | Description | Example |
|----------|-------------|---------|
| `MONGO_USERNAME` | MongoDB user | `admin` |
| `MONGO_PASSWORD` | MongoDB password | `miPasswordSeguro123` |
| `MONGO_DATABASE` | Database name | `credentials_db` |
| `JWT_SECRET` | jwt (BASE64)               |  |

And run the project locally:
```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 📸 Screenshots
1️⃣ Swagger

The API has documentation generated automatically with Swagger. You can access the endpoint interface from the following local URL:
```bash
   http://localhost:8080/swagger-ui-custom.html
   ```
<img width="1090" height="1020" alt="Image" src="https://github.com/user-attachments/assets/a7d64656-caf9-4db8-b5c6-4ba05f10a296" />

From there, we can test all the endpoints available in the API. But first, we have to create an account or log in if necessary.
Once authenticated, we can manage our credentials and generate reports.

<img width="1080" height="436" alt="Image" src="https://github.com/user-attachments/assets/b83c35b7-1a4f-484b-ab46-3d8ed4b112db" />
<img width="1094" height="1039" alt="Image" src="https://github.com/user-attachments/assets/c34ded6d-c4a7-46a8-8655-034f228b96b4" />
<img width="1096" height="1106" alt="Image" src="https://github.com/user-attachments/assets/18fbc726-5636-4887-8532-15e544863a76" />

And that's it, we can now perform all CRUD operations on our credentials.
<img width="1091" height="893" alt="Image" src="https://github.com/user-attachments/assets/310e25a5-9fc5-45ce-866c-7895978adb86" />


2️⃣ Reports
- PDF
  
![Image](https://github.com/user-attachments/assets/cfd50acf-0590-490c-ada9-a8b059f90b1e)

- Excel
  
![Image](https://github.com/user-attachments/assets/a297050d-80b4-4c26-a828-fd520889ca83)


## 🌐 Frontend: 
```bash
   https://github.com/LuisOrihuela08/secret-key-project-frontend.git
   ```

To get the web application up and running quickly, it is best to run the following docker-compose: 
```bash
services:
  mongodb:
    image: mongo:7.0
    container_name: secretkey-mongodb
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: mongo
      MONGO_INITDB_ROOT_PASSWORD: mongo
      MONGO_INITDB_DATABASE: secretkey
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
      - mongodb_config:/data/configdb
    networks:
      - secretkey-network
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/secretkey --quiet
      interval: 10s
      timeout: 5s
      retries: 5

  secret-key-backend:
    image: luisorihuela92/secret-key-backend:latest
    container_name: secretkey-app-backend
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATA_MONGODB_URI: mongodb://mongo:mongo@mongodb:27017/secretkey?authSource=admin
      JWT_SECRET: DHoEyF2VTNrYGafkeIP9LipcGfVkOt8SeBC9SjViYR8=
      JWT_EXPIRATION: 86400000
    depends_on:
      mongodb:
        condition: service_healthy
    networks:
      - secretkey-network

volumes:
  mongodb_data:
    name: secretkey-mongodb-data
  mongodb_config:
    name: secretkey-mongodb-config

networks:
  secretkey-network:
   ```

And access the link
```bash
   http://localhost:3000
   ```

## 👨‍💻 Author

<div align="center">

**Luis Orihuela** - *FullStack Developer*



🌐 **Portfolio:** [luisorihuela.me](https://luisorihuela.me)  
💼 **GitHub:** [@LuisOrihuela08](https://github.com/LuisOrihuela08)  

---

<sub>Made with ❤️ in Peru 🇵🇪 | © 2025</sub>

</div>
