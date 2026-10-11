# Informe de cierre — HU-APT-001

**Disponibilidad y agendamiento de citas odontológicas**  
**Proyecto:** `code-corhuila/dlc-appointments-portal`  
**Módulo:** Portal frontend de citas · Angular / Native Federation  
**Fecha de verificación:** 9 de octubre de 2026  
**Rama verificada:** `develop` · **HEAD después de integrar A11:** `c958362`  
**Estado:** **Implementación frontend planificada terminada y validada localmente. Integración extremo a extremo pendiente.**

> **Alcance del cierre.** Este informe cierra el plan de trabajo frontend A1–A12 de HU-APT-001. No certifica la aceptación funcional de la historia en un entorno integrado, puesto que siguen existiendo dependencias externas sin verificar.

## 1. Resumen ejecutivo

Se completó la implementación frontend prevista para consultar y administrar la disponibilidad de odontólogos y agendar citas odontológicas. El portal dispone de búsqueda de pacientes activos, selección de odontólogo, fecha y horario, validación de intervalos, persistencia de disponibilidad y envío de solicitudes de creación de citas mediante los contratos API previstos.

Después de fusionar el PR A11 en `develop`, se ejecutó la batería completa de pruebas y la compilación de producción. **Las 169 pruebas automatizadas pasaron en 30 archivos**, la compilación fue exitosa y `git status --short` devolvió un árbol de trabajo limpio. Las pruebas de interfaz y servicios utilizan dobles de prueba; **no sustituyen las pruebas con los microservicios reales**.

## 2. Indicadores de validación

| Indicador | Resultado | Evidencia |
|---|---|---|
| Pruebas automatizadas | **169/169 aprobadas** | `npm test -- --watch=false` en `develop` |
| Archivos de pruebas | **30/30 aprobados** | Salida final de Vitest |
| Compilación de producción | **Exitosa** | `npm run build` / `ng build` |
| Árbol de trabajo Git | **Limpio** | `git status --short` sin salida |
| Integración de A11 | **Confirmada** | `git pull --ff-only origin develop`: `436c7f8..c958362` |
| Advertencias de compilación | **2 advertencias no bloqueantes** | Presupuestos CSS excedidos; ver §7 |

## 3. Funcionalidades implementadas y verificadas localmente

| Funcionalidad | Resultado frontend | Alcance de la evidencia |
|---|---|---|
| Consultar disponibilidad odontológica | Implementada | Servicio HTTP y tratamiento de respuesta simulada |
| Crear y editar turnos de disponibilidad | Implementada | Formularios, guardado y pruebas con API simulada |
| Validar horarios y solapamientos | Implementada | Validaciones de intervalos y pruebas de regresión |
| Excluir intervalos bloqueados al calcular horarios | Implementada | Lógica de generación de franjas y pruebas locales |
| Trabajar con zona horaria de la clínica | Implementada | Configuración e interpretación de fechas y horarios |
| Buscar pacientes activos | Implementada | `status=ACTIVE`; respuesta simulada en pruebas |
| Seleccionar paciente, odontólogo, fecha y horario | Implementada | Interacción y estado del formulario con pruebas locales |
| Evitar selecciones de paciente obsoletas | Implementada | Prueba nueva de A11: limpieza al iniciar otra búsqueda |
| Mostrar odontólogo seleccionado en el resumen | Implementada | Prueba nueva de A11 con directorio inyectado |
| Crear citas con idempotencia | Implementada | Cliente `POST`, cabecera y pruebas con servicio simulado |
| Gestionar errores de agendamiento | Implementada | Casos de conflicto, validación, permisos, indisponibilidad y error genérico |
| Ejecutar pruebas de regresión | Aprobadas | 169/169 pruebas |

**Interpretación:** «Implementada» significa que la funcionalidad se encuentra en el frontend y ha sido ejercitada localmente. No significa que el backend real haya respondido con éxito ni que el flujo esté habilitado en un ambiente integrado.

## 4. Contratos API examinados

Los siguientes métodos están definidos en los servicios Angular revisados:

| Método | Ruta relativa | Finalidad | Servicio |
|---|---|---|---|
| `GET` | `/api/v1/dentists/{id}/availability` | Consultar la disponibilidad del odontólogo | `AppointmentsApiService` |
| `PUT` | `/api/v1/dentists/{id}/availability` | Actualizar intervalos y bloqueos de disponibilidad | `AppointmentsApiService` |
| `POST` | `/api/v1/appointments` | Crear una cita odontológica | `AppointmentsApiService` |
| `GET` | `/api/v1/patients` | Buscar pacientes mediante `search`, `status`, `page`, `limit` | `PatientsLookupService` |
| `GET` | `/api/v1/patients/{id}` | Obtener un paciente por su identificador | `PatientsLookupService` |

La creación de citas utiliza la cabecera **`Idempotency-Key`**. Los clientes emplean rutas relativas `/api/v1` e inyectan `HttpClient`; estos archivos no definen hosts absolutos ni gestionan tokens o sesiones. La implementación del servicio no prueba por sí sola que los endpoints estén desplegados o disponibles.

### Alcance de otras historias

El servicio Angular también declara operaciones de confirmación, cancelación, reprogramación, atención, inasistencia, finalización y asignaciones clínicas. **Su presencia en el cliente no implica que se hayan desarrollado o aceptado las pantallas correspondientes como parte de HU-APT-001.** Esas responsabilidades pertenecen a historias independientes (HU-APT-002, HU-APT-003 y HU-APT-004, según aplique).

## 5. Evidencia de cierre técnico

1. **Integración a `develop`:** después de la fusión del PR A11, la actualización local avanzó de `436c7f8` a `c958362` por *fast-forward*.
2. **Pruebas:** `npm test -- --watch=false` concluyó con `Test Files 30 passed (30)` y `Tests 169 passed (169)`.
3. **Compilación:** `npm run build` completó la generación del *bundle* y produjo artefactos en `dist/dlc-appointments-portal`.
4. **Git:** `git status --short` no imprimió cambios pendientes.
5. **Trazabilidad de A11:** commit `9672e02` — `fix(scheduling): keep patient and dentist selections consistent`; PR integrado antes de la verificación de A12.

### Cobertura y límites

Las pruebas automatizadas incluyen simulaciones de peticiones y respuestas, entre ellas errores y conflictos. **No se ha presentado evidencia de una ejecución extremo a extremo** con IAM, pacientes, agenda/citas y gateway reales. Tampoco se ha verificado en este cierre la publicación de un evento de dominio por el backend.

## 6. Dependencias externas y riesgos abiertos

| Dependencia o riesgo | Situación observada | Consecuencia | Criterio para resolverlo |
|---|---|---|---|
| Directorio autorizado de odontólogos (IAM) | `DENTIST_DIRECTORY` utiliza un proveedor predeterminado que devuelve `[]` y señala integración IAM pendiente | Sin proveedor real, el selector puede permanecer sin odontólogos | Conectar un directorio IAM autorizado y probar permisos/selección con datos reales de prueba |
| Servicio de pacientes | Existe `PatientsLookupService`, con filtro `ACTIVE`, pero la prueba de integración real no se ha aportado | No está confirmada la consulta real de pacientes activos | Verificar respuesta, paginación, permisos y contrato en ambiente integrado |
| Servicio de citas y disponibilidad | Los métodos `GET`, `PUT` y `POST` están definidos; las pruebas locales usan simulaciones | No se ha confirmado persistencia, no solapamiento ni conflictos simultáneos reales | Realizar pruebas con backend, incluida concurrencia y respuesta a conflictos |
| Autenticación/autorización y gateway | No se ha verificado el flujo completo en un ambiente integrado | La autorización efectiva no está demostrada | Probar acceso según rol y direccionamiento de rutas relativas |
| Evento de dominio de creación de cita | Responsabilidad del backend; no hay evidencia de ejecución en este cierre | Su emisión no puede certificarse desde el frontend | Comprobar publicación/consumo mediante evidencias del backend, cuando corresponda |

Estos puntos se **documentan como pendientes de integración**, no como funcionalidades frontend adicionales que deban improvisarse dentro de A12.

## 7. Observaciones no bloqueantes

La compilación final reportó advertencias de presupuesto CSS:

| Archivo | Límite configurado | Tamaño informado | Exceso |
|---|---:|---:|---:|
| `calendar-page.component.css` | 4,00 kB | 5,74 kB | 1,74 kB |
| `scheduling-page.component.css` | 4,00 kB | 5,37 kB | 1,37 kB |

Estas advertencias **no impidieron el build**. No se realizaron cambios de estilos durante A12.

## 8. Conclusión y decisión de cierre

**Decisión:** dar por **finalizado el plan frontend A1–A12 de HU-APT-001 y su verificación local**. El código integrado en `develop` supera las 169 pruebas automatizadas, compila correctamente y no presenta cambios Git sin confirmar.

**Condición de aceptación:** mantener el estado de **integración funcional pendiente** hasta disponer del directorio IAM autorizado y de pruebas reales de pacientes, disponibilidad, creación de citas, permisos y conflictos. No corresponde afirmar que HU-APT-001 está validada extremo a extremo en esta etapa.

**Siguiente paso organizativo:** trasladar las dependencias a los equipos responsables y conservar este informe como evidencia de cierre frontend. Las demás historias de citas se abordan por separado, sin extender artificialmente A12.

---

**Fuentes de verificación:** comandos y salidas de CMD suministrados durante la revisión de A12; contenido revisado de `scheduling-page.component.ts`, `scheduling-page.component.html`, `dentist-directory.ts`, `appointments-api.service.ts` y `patients-lookup.service.ts`.  
**Naturaleza del documento:** informe de estado y validación local; no acta de aceptación de integración extremo a extremo.
