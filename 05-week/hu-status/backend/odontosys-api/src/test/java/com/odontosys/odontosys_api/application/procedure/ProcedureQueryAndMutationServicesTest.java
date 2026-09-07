package com.odontosys.odontosys_api.application.procedure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.odontosys.odontosys_api.application.procedure.command.UpdateProcedureCommand;
import com.odontosys.odontosys_api.domain.exception.ProcedureAlreadyExistsException;
import com.odontosys.odontosys_api.domain.exception.ProcedureNotFoundException;
import com.odontosys.odontosys_api.domain.model.Procedure;
import com.odontosys.odontosys_api.domain.port.out.ProcedureRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ProcedureQueryAndMutationServicesTest {
  @Mock ProcedureRepositoryPort repository;
  private Procedure procedure(UUID id) { return Procedure.reconstitute(id,"Limpieza","d",BigDecimal.TEN,30,true,Instant.now(),Instant.now()); }
  @Test void getById_mapsProcedure() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.of(procedure(id))); assertEquals("Limpieza",new GetProcedureByIdService(repository).execute(id).name()); }
  @Test void getById_throwsWhenMissing() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.empty()); assertThrows(ProcedureNotFoundException.class,()->new GetProcedureByIdService(repository).execute(id)); }
  @Test void list_mapsProcedures() { when(repository.findAll()).thenReturn(List.of(procedure(UUID.randomUUID()))); assertEquals(1,new ListProceduresService(repository).execute().size()); }
  @Test void delete_deactivatesAndSaves() { UUID id=UUID.randomUUID(); Procedure p=procedure(id); when(repository.findById(id)).thenReturn(Optional.of(p)); new DeleteProcedureService(repository).execute(id); assertFalse(p.isActive()); verify(repository).save(p); }
  @Test void update_rejectsDuplicateName() { UUID id=UUID.randomUUID(); when(repository.findById(id)).thenReturn(Optional.of(procedure(id))); when(repository.existsByName("Otro")).thenReturn(true); assertThrows(ProcedureAlreadyExistsException.class,()->new UpdateProcedureService(repository).execute(id,new UpdateProcedureCommand("Otro","d",BigDecimal.ONE,10))); }
}
