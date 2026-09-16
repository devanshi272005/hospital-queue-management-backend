\# 🏥 Hospital Queue Management System — Backend



A Spring Boot REST API backend for managing hospital appointments, patients, doctors, and consultation queues.



The backend provides APIs that allow the hospital dashboard to manage patient queues, call patients for consultation, update consultation status, and maintain completed patient records.



\## ✨ Features



\* 👨‍⚕️ Doctor management

\* 🧑‍🤝‍🧑 Patient management

\* 📅 Appointment management

\* 🎫 Automatic queue token generation

\* 🔔 Call Next Patient functionality

\* 🩺 Current consultation tracking

\* ✅ Patient completion tracking

\* 📋 Completed queue history

\* 🔒 Duplicate appointment protection

\* 📊 Doctor-wise queue management

\* 🔗 REST API integration

\* 🗄️ MySQL database integration



\## 🛠️ Tech Stack



\* \*\*Language:\*\* Java

\* \*\*Framework:\*\* Spring Boot

\* \*\*Build Tool:\*\* Maven

\* \*\*Database:\*\* MySQL

\* \*\*ORM:\*\* Hibernate / JPA

\* \*\*API:\*\* REST API

\* \*\*Server:\*\* Apache Tomcat

\* \*\*Testing:\*\* Postman

\* \*\*JDK:\*\* Java 21



\## 🏗️ Architecture



```text

React Frontend

&#x20;      │

&#x20;      │ REST API

&#x20;      ▼

Spring Boot Backend

&#x20;      │

&#x20;      ├── Controllers

&#x20;      │

&#x20;      ├── DTOs

&#x20;      │

&#x20;      ├── Repositories

&#x20;      │

&#x20;      └── Entities

&#x20;      │

&#x20;      ▼

&#x20;    MySQL

```



\## 📂 Project Structure



```text

hospital\_queue\_management/

│

├── src/

│   ├── main/

│   │   └── java/

│   │       └── com/

│   │           └── hospital/

│   │               └── hospital\_queue\_management/

│   │                   │

│   │                   ├── appointmentcontroller/

│   │                   │   └── appointmentcontroller.java

│   │                   │

│   │                   ├── dto/

│   │                   │   └── QueueResponse.java

│   │                   │

│   │                   ├── entity/

│   │                   │   ├── appointment.java

│   │                   │   ├── doctor.java

│   │                   │   ├── patient.java

│   │                   │   └── queue.java

│   │                   │

│   │                   ├── queuecontroller/

│   │                   │   └── queuecontroller.java

│   │                   │

│   │                   ├── repository/

│   │                   │   ├── appointmentrepository.java

│   │                   │   ├── patientrepository.java

│   │                   │   └── queuerepository.java

│   │                   │

│   │                   └── HospitalQueueManagementApplication.java

│   │

│   └── test/

│

├── .gitignore

├── pom.xml

├── mvnw

├── mvnw.cmd

└── README.md

```



\## 🔗 REST API Endpoints



\### Queue APIs



| Method | Endpoint                                    | Purpose                     |

| ------ | ------------------------------------------- | --------------------------- |

| GET    | `/api/queues`                               | Get all queues              |

| GET    | `/api/queues/{id}`                          | Get queue by ID             |

| GET    | `/api/queues/doctor/{doctorId}/date/{date}` | Get doctor queue for a date |

| GET    | `/api/queues/doctor/{doctorId}/today`       | Get today's doctor queue    |

| GET    | `/api/queues/doctor/{doctorId}/waiting`     | Get waiting patients        |

| GET    | `/api/queues/doctor/{doctorId}/current`     | Get current consultation    |

| GET    | `/api/queues/doctor/{doctorId}/next`        | Get next patient            |

| GET    | `/api/queues/doctor/{doctorId}/completed`   | Get completed patients      |

| POST   | `/api/queues`                               | Add patient to queu         |



