package com.odontosys.odontosys_api.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.odontosys.odontosys_api.application.auth.*;
import com.odontosys.odontosys_api.application.auth.command.RefreshTokenCommand;
import com.odontosys.odontosys_api.application.invoice.*;
import com.odontosys.odontosys_api.application.medicalrecord.*;
import com.odontosys.odontosys_api.application.schedule.*;
import com.odontosys.odontosys_api.domain.exception.*;
import com.odontosys.odontosys_api.domain.model.InvoiceStatus;
import com.odontosys.odontosys_api.domain.port.in.invoice.SendInvoiceEmailUseCase;
import com.odontosys.odontosys_api.domain.port.out.*;

class RemainingApplicationServicesTest {
  @Test void getCurrentUser_throwsWhenUserDoesNotExist() {
    UserRepositoryPort users=mock(UserRepositoryPort.class); UUID id=UUID.randomUUID(); when(users.findById(id)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class,()->new GetCurrentUserService(users).execute(id));
  }
  @Test void logout_revokesAllRefreshTokens() {
    RefreshTokenRepositoryPort tokens=mock(RefreshTokenRepositoryPort.class); UUID id=UUID.randomUUID(); new LogoutService(tokens).execute(id); verify(tokens).revokeAllByUserId(id);
  }
  @Test void refreshToken_rejectsUnknownToken() {
    RefreshTokenRepositoryPort refresh=mock(RefreshTokenRepositoryPort.class); TokenProviderPort provider=mock(TokenProviderPort.class); when(provider.hashRefreshToken("raw")).thenReturn("hash"); when(refresh.findByTokenHash("hash")).thenReturn(Optional.empty());
    assertThrows(InvalidTokenException.class,()->new RefreshTokenService(refresh,mock(UserRepositoryPort.class),provider,1L).execute(new RefreshTokenCommand("raw")));
  }
  @Test void getInvoice_throwsWhenMissing() {
    InvoiceRepositoryPort invoices=mock(InvoiceRepositoryPort.class); UUID id=UUID.randomUUID(); when(invoices.findById(id)).thenReturn(Optional.empty());
    assertThrows(InvoiceNotFoundException.class,()->new GetInvoiceByIdService(invoices,mock(PaymentRepositoryPort.class)).execute(id));
  }
  @Test void listInvoices_returnsEmptyWhenRepositoryIsEmpty() {
    InvoiceRepositoryPort invoices=mock(InvoiceRepositoryPort.class); when(invoices.findByFilters(null,null)).thenReturn(List.of());
    assertTrue(new ListInvoicesService(invoices,mock(PaymentRepositoryPort.class)).execute(null,null).isEmpty());
  }
  @Test void sendInvoiceEmail_throwsWhenInvoiceMissing() {
    InvoiceRepositoryPort invoices=mock(InvoiceRepositoryPort.class); UUID id=UUID.randomUUID(); when(invoices.findById(id)).thenReturn(Optional.empty());
    assertThrows(InvoiceNotFoundException.class,()->new SendInvoiceEmailService(invoices,mock(PatientRepositoryPort.class),mock(PaymentRepositoryPort.class),mock(EmailSenderPort.class)).execute(id));
  }
  @Test void updateInvoice_throwsWhenMissing() {
    InvoiceRepositoryPort invoices=mock(InvoiceRepositoryPort.class); UUID id=UUID.randomUUID(); when(invoices.findById(id)).thenReturn(Optional.empty());
    assertThrows(InvoiceNotFoundException.class,()->new UpdateInvoiceService(invoices,mock(PaymentRepositoryPort.class),mock(SendInvoiceEmailUseCase.class)).execute(id,InvoiceStatus.PAID,BigDecimal.ZERO));
  }
  @Test void getMedicalRecord_throwsWhenMissing() {
    MedicalRecordRepositoryPort records=mock(MedicalRecordRepositoryPort.class); UUID id=UUID.randomUUID(); when(records.findById(id)).thenReturn(Optional.empty());
    assertThrows(MedicalRecordNotFoundException.class,()->new GetMedicalRecordByIdService(records).execute(id));
  }
  @Test void patientHistory_throwsWhenPatientMissing() {
    PatientRepositoryPort patients=mock(PatientRepositoryPort.class); UUID id=UUID.randomUUID(); when(patients.findById(id)).thenReturn(Optional.empty());
    assertThrows(PatientNotFoundException.class,()->new GetPatientMedicalHistoryService(mock(MedicalRecordRepositoryPort.class),patients).execute(id));
  }
  @Test void updateMedicalRecord_throwsWhenMissing() {
    MedicalRecordRepositoryPort records=mock(MedicalRecordRepositoryPort.class); UUID id=UUID.randomUUID(); when(records.findById(id)).thenReturn(Optional.empty());
    assertThrows(MedicalRecordNotFoundException.class,()->new UpdateMedicalRecordService(records,mock(ProcedureRepositoryPort.class)).execute(id,null));
  }
  @Test void setSchedule_throwsWhenDentistMissing() {
    UserRepositoryPort users=mock(UserRepositoryPort.class); UUID id=UUID.randomUUID(); when(users.findById(id)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class,()->new SetDentistScheduleService(mock(DentistScheduleRepositoryPort.class),users).execute(id,List.of()));
  }
  @Test void getSchedule_returnsEmptyWhenNoneConfigured() {
    DentistScheduleRepositoryPort schedules=mock(DentistScheduleRepositoryPort.class); UUID id=UUID.randomUUID(); when(schedules.findByDentistId(id)).thenReturn(List.of());
    assertTrue(new GetDentistScheduleService(schedules).execute(id).isEmpty());
  }
  @Test void getAvailableSlots_returnsEmptyWhenRepositoryHasNone() {
    AvailabilitySlotRepositoryPort slots=mock(AvailabilitySlotRepositoryPort.class); UUID id=UUID.randomUUID(); LocalDate day=LocalDate.now(); when(slots.findByDentistIdAndDateBetweenAndStatus(any(),any(),any(),any())).thenReturn(List.of());
    assertTrue(new GetAvailableSlotsService(slots).execute(id,day,day).isEmpty());
  }
  @Test void generateSlots_returnsEmptyWithoutSchedule() {
    DentistScheduleRepositoryPort schedules=mock(DentistScheduleRepositoryPort.class); UUID id=UUID.randomUUID(); when(schedules.findByDentistId(id)).thenReturn(List.of());
    assertTrue(new GenerateSlotsService(schedules,mock(AvailabilitySlotRepositoryPort.class)).execute(id,LocalDate.now(),LocalDate.now()).isEmpty());
  }
  @Test void copySchedule_returnsEmptyWithoutSourceConfiguration() {
    DentistScheduleRepositoryPort schedules=mock(DentistScheduleRepositoryPort.class); UUID id=UUID.randomUUID(); when(schedules.findByDentistId(id)).thenReturn(List.of());
    assertTrue(new CopyDentistScheduleService(schedules,mock(UserRepositoryPort.class)).copySchedule(id,UUID.randomUUID(),false).isEmpty());
  }
}
