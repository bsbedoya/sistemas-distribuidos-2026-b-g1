package com.odontosys.odontosys_api.application.patient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.odontosys.odontosys_api.application.patient.command.UpdatePatientCommand;
import com.odontosys.odontosys_api.domain.exception.PatientAlreadyExistsException;
import com.odontosys.odontosys_api.domain.exception.PatientNotFoundException;
import com.odontosys.odontosys_api.domain.model.Patient;
import com.odontosys.odontosys_api.domain.port.out.PatientRepositoryPort;

@ExtendWith(MockitoExtension.class)
class PatientQueryAndMutationServicesTest {
  @Mock PatientRepositoryPort repository;
  private Patient patient(UUID id) { return Patient.reconstitute(id, "Ana", "Díaz", "CC", "123", "1", "a@test.com", LocalDate.of(1990,1,1), "x", true, java.time.Instant.now(), java.time.Instant.now()); }
  @Test void getById_mapsPatient() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.of(patient(id))); assertEquals("Ana", new GetPatientByIdService(repository).execute(id).firstName()); }
  @Test void getById_throwsWhenMissing() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.empty()); assertThrows(PatientNotFoundException.class, () -> new GetPatientByIdService(repository).execute(id)); }
  @Test void list_mapsPatients() { when(repository.findAll()).thenReturn(List.of(patient(UUID.randomUUID()))); assertEquals(1, new ListPatientsService(repository).execute().size()); }
  @Test void delete_deactivatesAndSaves() { UUID id=UUID.randomUUID(); Patient p=patient(id); when(repository.findById(id)).thenReturn(Optional.of(p)); new DeletePatientService(repository).execute(id); assertFalse(p.isActive()); verify(repository).save(p); }
  @Test void update_rejectsDuplicateDocument() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.of(patient(id))); when(repository.existsByDocumentNumber("999")).thenReturn(true); UpdatePatientCommand c=new UpdatePatientCommand("Ana","Díaz","CC","999","1","a@test.com",LocalDate.of(1990,1,1),"x"); assertThrows(PatientAlreadyExistsException.class, () -> new UpdatePatientService(repository).execute(id,c)); }
}
