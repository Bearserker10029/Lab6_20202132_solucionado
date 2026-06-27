# 💼 Sistema de Facturación Electrónica

> Aplicación web desarrollada con Spring Boot y Thymeleaf para emitir comprobantes electrónicos (facturas y boletas) como parte del Laboratorio 6 del curso GTICS — PUCP, ciclo 2026-1.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción-del-proyecto)
- [Tecnologías](#-tecnologías-usadas)
- [Estructura](#-estructura-principal)
- [Flujo Funcional](#-flujo-funcional-implementado)
- [Reglas de Negocio](#-reglas-de-negocio)
- [Ejecución](#-cómo-ejecutar)

---

## 📝 Descripción del Proyecto

Sistema de facturación electrónica que permite gestionar clientes, productos y comprobantes de venta (facturas / boletas), respetando las reglas tributarias básicas del Perú:

- Registrar clientes con DNI (8 dígitos) o RUC (11 dígitos)  
- Registrar productos con precio y stock  
- Emitir comprobantes electrónicos (Factura o Boleta)  
- Validar que la Factura solo se emita a clientes con RUC y la Boleta solo a clientes con DNI  
- Descontar automáticamente el stock al emitir un comprobante  
- Listar, editar y eliminar clientes, productos y comprobantes  
- Impedir eliminar clientes / productos con comprobantes asociados  

## 💻 Tecnologías Usadas

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 17 | Lenguaje base |
| Spring Boot | 4.0.6 | Framework MVC |
| Spring Data JPA | Incluido | Acceso a datos |
| Spring MVC | Incluido | Controladores HTTP |
| Thymeleaf | Starter | Motor de plantillas |
| Maven | Wrapper | Gestor de dependencias |
| Lombok | Latest | Reducción de boilerplate |
| MySQL | 8.0+ | Base de datos (`lab6`) |
| Bootstrap | 5.x | Framework CSS (vía static) |

## 📂 Estructura Principal

```
facturacion-electronica/
├── src/
│   ├── main/
│   │   ├── java/org/example/facturacion/
│   │   │   ├── Lab520202132Application.java
│   │   │   ├── controller/
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   └── InvoiceController.java
│   │   │   ├── dto/
│   │   │   │   ├── InvoiceDto.java
│   │   │   │   └── InvoiceDetailDTO.java
│   │   │   ├── model/
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Invoice.java
│   │   │   │   └── InvoiceDetail.java
│   │   │   └── repository/
│   │   │       ├── CustomerRepository.java
│   │   │       ├── ProductRepository.java
│   │   │       ├── InvoiceRepository.java
│   │   │       └── InvoiceDetailRepository.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── message.properties
│   │       ├── db/lab6.sql
│   │       ├── static/
│   │       │   ├── css/bootstrap.min.css
│   │       │   └── js/bootstrap.bundle.min.js
│   │       └── templates/
│   │           ├── title.html
│   │           ├── fragments/navbar.html
│   │           ├── customer/{list,form}.html
│   │           ├── product/{list,form}.html
│   │           └── invoice/{list,form}.html
│   └── test/
│       └── java/org/example/facturacion/FacturacionApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🔄 Flujo Funcional Implementado

```
┌──────────────────────────────────────────────────────────────┐
│             FLUJO DE FACTURACIÓN ELECTRÓNICA                 │
├──────────────────────────────────────────────────────────────┤
│  1. GET  /                                                  │
│     → Página de inicio (title.html)                          │
│                                                              │
│  2. GET  /cliente                                            │
│     → Lista todos los clientes                               │
│                                                              │
│  3. GET/POST  /new  ·  /save  ·  /edit/{id}  ·  /delete     │
│     → ABM de clientes (validación DNI 8 / RUC 11)            │
│                                                              │
│  4. GET  /producto                                           │
│     → Lista todos los productos                              │
│                                                              │
│  5. GET/POST  /producto/new · /producto/save · ...           │
│     → ABM de productos (precio > 0, stock ≥ 0)               │
│                                                              │
│  6. GET  /comprobante                                        │
│     → Lista de comprobantes emitidos                         │
│                                                              │
│  7. GET  /comprobante/new                                    │
│     → Formulario: tipo + cliente + productos[] + cantidades  │
│                                                              │
│  8. POST /comprobante/save                                   │
│     → Valida reglas de negocio, descuenta stock              │
│                                                              │
│  9. GET  /comprobante/delete?id={id}                         │
│     → Elimina comprobante + detalles (cascade)               │
└──────────────────────────────────────────────────────────────┘
```

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/` | GET | Página de inicio |
| `/cliente` | GET | Lista todos los clientes |
| `/new` | GET | Formulario de nuevo cliente |
| `/save` | POST | Crea / actualiza cliente |
| `/edit/{id}` | GET | Carga cliente en formulario |
| `/delete` | GET | Elimina cliente (params: `id`) |
| `/producto` | GET | Lista todos los productos |
| `/producto/new` | GET | Formulario de nuevo producto |
| `/producto/save` | POST | Crea / actualiza producto |
| `/producto/edit/{id}` | GET | Carga producto en formulario |
| `/producto/delete` | GET | Elimina producto (params: `id`) |
| `/comprobante` | GET | Lista comprobantes emitidos |
| `/comprobante/new` | GET | Formulario de nuevo comprobante |
| `/comprobante/save` | POST | Registra comprobante y descuenta stock |
| `/comprobante/delete` | GET | Elimina comprobante (params: `id`) |

## 🧾 Reglas de Negocio

### Cliente
- `name` — obligatorio, máximo 100 caracteres.
- `document` — obligatorio, **único**.
  - Si `documentType = DNI` → debe tener exactamente **8** dígitos.
  - Si `documentType = RUC` → debe tener exactamente **11** dígitos.
- `documentType` — obligatorio (`DNI` o `RUC`).

### Producto
- `name` — obligatorio y **único**, máximo 100 caracteres.
- `price` — obligatorio, **mayor a 0**.
- `stock` — obligatorio, **no negativo**.
- No se puede eliminar si está usado en algún comprobante.

### Comprobante
- `type` — `FACTURA` o `BOLETA` (obligatorio).
- `customer` — obligatorio.
  - **FACTURA solo permite clientes con RUC.**
  - **BOLETA solo permite clientes con DNI.**
- `date` — obligatoria, **no puede ser futura.**
- Detalle:
  - Al menos **un** producto con cantidad > 0.
  - **No se puede repetir** el mismo producto en un mismo comprobante.
  - La cantidad **no puede superar el stock** disponible.
  - Al guardar, el stock se **descuenta automáticamente** del producto.

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Java 17+
- Maven 3.6+ (incluido como wrapper `mvnw`)
- MySQL 8.0+ corriendo en `localhost:3306`

### Configurar Base de Datos

Crea la base de datos `lab6` con el script incluido en el proyecto:

```bash
# Usando el script provisto
mysql -u root -p < src/main/resources/db/lab6.sql
```

O manualmente:

```sql
CREATE DATABASE IF NOT EXISTS lab6;
```

> El esquema completo (tablas `customer`, `product`, `invoice`, `invoice_detail`) está en [`src/main/resources/db/lab6.sql`](src/main/resources/db/lab6.sql).

### Configurar Credenciales

Edita `src/main/resources/application.properties` si tu MySQL usa credenciales distintas:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lab6
spring.datasource.username=root
spring.datasource.password=root
```

### Ejecutar la Aplicación

**En Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**En Linux/macOS:**

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### Acceder a la Aplicación

Una vez iniciada, abre tu navegador en:

```
http://localhost:8080
```

**Puerto por defecto:** `8080`

---

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf Guide](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [SUNAT — Comprobantes de Pago Electrónicos](https://www.sunat.gob.pe/legislacion/superinendencia/legislacion.html)

---

## 📄 Licencia

Este proyecto es de uso académico y educativo como parte del Laboratorio 6 del curso **GTICS — PUCP**, ciclo 2026-1.