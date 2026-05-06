# 🏥 Aviva Appointment System

Aplicativo web de gestión de citas médicas para la Clínica Aviva.

---

## 📋 Descripción

Aplicación web que permite gestionar citas médicas, pagss, pacientes, 
doctores, atención médica y especialidades de la Clínica Aviva, 
mediante un portal para uso del personal interno (médico, 
administrador y recepcionista) así como un portal paciente donde
podra autogestionar sus citas y su pago. 
El sistema contara con notificaciones sobre cambios en las citas además
de auditorías de las mismas

---

## 🚀 Tecnologías utilizadas

- Java 21
- Spring Boot 4.0.6
- PostgreSQL
- HTML5, CSS3, JavaScript (para el segundo avance)
- Maven

---

## 📁 Estructura del proyecto

```
AvivaAppointmentSystem/
├── appointmentsystem/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aviva/appointmentsystem/
│   │   │   │   ├── controllers/
│   │   │   │   ├── dto/
│   │   │   │   ├── models/
│   │   │   │   ├── repositories/
│   │   │   │   ├── services/
│   │   │   │   └── AppointmentsystemApplication.java
│   │   │   └── resources/
│   │   │       ├── static/
│   │   │       │   ├── css/
│   │   │       │   ├── js/
│   │   │       │   └── img/
│   │   │       ├── templates/
│   │   │       └── application.properties
│   │   └── test/
│   ├── .gitignore
│   ├── mvnw
│   └── pom.xml
└── README.md
```


---

## 👥 Funcionalidades

- 📅 Gestión de citas médicas
- 👤 Gestión de pacientes
- 🩺 Atención médica
- 🏷️ Gestión de especialidades
- 🔐 Login por roles (Admin, Doctor, Recepcionista, Paciente)
- 🔐 Auditorías de citas

---

