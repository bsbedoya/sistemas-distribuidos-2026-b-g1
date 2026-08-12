# OdontoSys — Arquitectura y Microservicios

**Responsable:** Brayan Smith Bedoya

## Introducción

Este documento presenta la estructura arquitectónica de **OdontoSys** y la distribución de sus principales microservicios. El objetivo es mostrar cómo se dividirá el sistema de acuerdo con las responsabilidades del negocio, qué tecnología utilizará cada servicio y cómo se manejará la persistencia independiente.

La arquitectura busca mantener los servicios desacoplados, permitiendo que cada uno pueda desarrollarse, desplegarse y evolucionar de manera independiente.

---

## 1. Arquitectura general

La solución estará compuesta por cuatro microservicios principales de negocio, un servicio de autenticación y un componente de analítica.

```text
                              ODONTOSYS
                                  │
                                  ▼
                           ┌──────────────┐
                           │ API GATEWAY  │
                           └──────┬───────┘
                                  │
                       ┌──────────▼──────────┐
                       │   AUTH SERVICE      │
                       │    Spring Boot      │
                       └──────────┬──────────┘
                                  │
                              JWT / RBAC
                                  │
          ┌───────────────────────┼────────────────────────┐
          │                       │                        │
          ▼                       ▼                        ▼
   ┌─────────────┐         ┌─────────────┐          ┌─────────────┐
   │  PACIENTES  │         │    CITAS    │          │   CLÍNICO   │
   │ Spring Boot │         │ Spring Boot │          │     Go      │
   └──────┬──────┘         └──────┬──────┘          └──────┬──────┘
          │                       │                        │
          ▼                       ▼                        ▼
      PostgreSQL              PostgreSQL                MongoDB

                         ┌─────────────────┐
                         │   FACTURACIÓN   │
                         │   Spring Boot   │
                         └────────┬────────┘
                                  │
                                  ▼
                              PostgreSQL

                         ┌─────────────────┐
                         │    RabbitMQ     │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │    ANALÍTICA    │
                         │  Go/Python/Java │
                         └─────────────────┘
```

Los cuatro microservicios principales serán:

1. Pacientes.
2. Citas.
3. Clínico.
4. Facturación.

De manera transversal se contará con:

- Servicio de autenticación.
- Componente de analítica.
- RabbitMQ como mecanismo de mensajería.

## 2. Microservicio de Pacientes

**Tecnología:** Spring Boot  
**Base de datos:** PostgreSQL  
**Responsable:** Daniel Perez Lozada

Gestionará la información general y administrativa de los pacientes.

Funciones principales:

- Registrar pacientes.
- Actualizar información.
- Consultar pacientes.
- Gestionar información de contacto.
- Gestionar contactos de emergencia.

## 3. Microservicio de Citas

**Tecnología:** Spring Boot  
**Base de datos:** PostgreSQL  
**Responsable:** Brayan Smith Bedoya

Gestionará la agenda y las citas odontológicas.

Funciones principales:

- Crear citas.
- Consultar disponibilidad.
- Reprogramar citas.
- Cancelar citas.
- Confirmar asistencia.
- Cambiar estados de las citas.
- Consultar agenda de los odontólogos.

Estados posibles:

```text
PROGRAMADA
CONFIRMADA
EN_ATENCION
FINALIZADA
CANCELADA
NO_ASISTIO
```

## 4. Microservicio Clínico

**Tecnología:** Go  
**Base de datos:** MongoDB  
**Responsable:** Luis Ignacio Bonilla

Gestionará la información relacionada con la atención odontológica.

Funciones principales:

- Historia clínica.
- Odontograma.
- Diagnósticos.
- Tratamientos.
- Procedimientos.
- Evolución de la atención.
- Registro de consultas odontológicas.

MongoDB permitirá manejar estructuras clínicas que pueden variar dependiendo del paciente, diagnóstico o tratamiento.

## 5. Microservicio de Facturación

**Tecnología:** Spring Boot  
**Base de datos:** PostgreSQL  
**Responsable:** Juan Diego Mora

Gestionará los procesos económicos relacionados con la atención odontológica.

Funciones principales:

- Generación de facturas.
- Consulta de facturas.
- Registro de pagos.
- Consulta de saldos.
- Métodos de pago.
- Historial de pagos.

Los pagos serán inicialmente simulados.

## 6. Persistencia

Cada microservicio tendrá control exclusivo sobre su propia base de datos.

```text
Pacientes      → PostgreSQL
Citas          → PostgreSQL
Clínico        → MongoDB
Facturación    → PostgreSQL
```

No se permitirá que un microservicio acceda directamente a la base de datos de otro.
