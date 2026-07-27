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

## 📦 Quick Start (Local or Server) — Nginx Reverse Proxy

This setup uses a single `docker-compose.yml` + `nginx.conf` that works **both locally and on a remote server** (AWS EC2 or any VPS) without touching any code or rebuilding images. Nginx sits in front of the frontend and backend, so everything is served under one origin — no CORS issues, no hardcoded `localhost` URLs baked into the frontend build.

### 1. Files you need

Create a folder with these 3 files:

```
secret-key/
├── docker-compose.yml
├── nginx.conf
└── .env          ← do NOT commit this file
```

**`docker-compose.yml`**

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
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATA_MONGODB_URI: mongodb://mongo:mongo@mongodb:27017/secretkey?authSource=admin
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: 86400000
      FRONTEND_URL: ${FRONTEND_URL}
    depends_on:
      mongodb:
        condition: service_healthy
    networks:
      - secretkey-network

  secret-key-frontend:
    image: luisorihuela92/secret-key-frontend:latest
    container_name: secretkey-app-frontend
    restart: unless-stopped
    environment:
      - NODE_ENV=production
    depends_on:
      - secret-key-backend
    networks:
      - secretkey-network
      
  nginx:
    image: nginx:alpine
    container_name: secretkey-nginx
    restart: unless-stopped
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
    depends_on:
      - secret-key-backend
      - secret-key-frontend
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
**`nginx.conf`**
```nginx
server {
    listen 80;
    server_name _;
 
    location /api/ {
        proxy_pass http://secret-key-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
 
    location /v1/ {
        proxy_pass http://secret-key-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
 
    location / {
        proxy_pass http://secret-key-frontend:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**`.env`** (create this yourself, don't commit it)
```
JWT_SECRET=DHoEyF2VTNrYGafkeIP9LipcGfVkOt8SeBC9SjViYR8=
FRONTEND_URL=http://localhost
```

| Variable | Description | Local example | Server example |
|----------|-------------|----------------|-----------------|
| `JWT_SECRET` | JWT signing key (BASE64) | `DHoEyF2VTNrYGafkeIP9LipcGfVkOt8SeBC9SjViYR8=` | use your own generated secret |
| `FRONTEND_URL` | Public origin allowed by CORS | `http://localhost` | `http://your-server-ip-or-domain` |

And access the link
```bash
   http://localhost:3000
   ```

### 2. Run it locally

```bash
docker compose up -d
```

Access the app at:
```
http://localhost
```
(note: port 80 now, not `localhost:3000` — everything is routed through Nginx)

### 3. Run it on a server (AWS EC2 or any VPS)

**a) Install Docker on the instance:**
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
```

**b) Open only port 80 (and 443 if you add HTTPS later)** in your Security Group / firewall. Do **not** expose `8080`, `3000`, or `27017` publicly — they only need to be reachable inside the Docker network.

**c) Copy the 3 files to the server:**
```bash
scp docker-compose.yml nginx.conf usuario@your-server-ip:~/secret-key/
```

**d) Create the `.env` on the server** with the server's public IP/domain:
```
JWT_SECRET=<generate-a-new-one-for-production>
FRONTEND_URL=http://your-server-ip-or-domain
```

**e) Pull and run:**
```bash
cd ~/secret-key
docker compose pull
docker compose up -d
```

**f) Access:**
```
http://your-server-ip-or-domain
```

### Why this works the same in both places

- **Frontend ↔ backend calls** go through relative paths (`/api/...`, `/v1/...`), so no `NEXT_PUBLIC_API_URL` needs to be baked into the image at build time.
- **Nginx** is the only container that needs a published port — it routes `/api/` and `/v1/` to the backend and everything else to the frontend, so the browser only ever talks to one origin.
- **Mongo's connection string (`mongodb:27017`)** and its healthcheck (`localhost:27017`) are internal Docker-network references — they never change between environments.
- The only thing that changes between local and server is the **`FRONTEND_URL`** value in `.env`.

## 👨‍💻 Author

<div align="center">

**Luis Orihuela** - *FullStack Developer*



🌐 **Portfolio:** [luisorihuela.me](https://luisorihuela.me)  
💼 **GitHub:** [@LuisOrihuela08](https://github.com/LuisOrihuela08)  

---

<sub>Made with ❤️ in Peru 🇵🇪 | © 2025</sub>

</div>
