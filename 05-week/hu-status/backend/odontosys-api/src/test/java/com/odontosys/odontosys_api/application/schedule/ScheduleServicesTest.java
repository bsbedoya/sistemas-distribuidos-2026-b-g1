package com.odontosys.odontosys_api.application.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.odontosys.odontosys_api.domain.model.AvailabilitySlot;
import com.odontosys.odontosys_api.domain.model.DentistSchedule;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.AvailabilitySlotRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.DentistScheduleRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ScheduleServicesTest {
    @Mock private DentistScheduleRepositoryPort scheduleRepository;
    @Mock private AvailabilitySlotRepositoryPort slotRepository;
    @Mock private UserRepositoryPort userRepository;
    @Captor private ArgumentCaptor<List<DentistSchedule>> schedulesCaptor;
    @Captor private ArgumentCaptor<List<AvailabilitySlot>> slotsCaptor;

    @Test
    void copySchedule_toAllDentists_replacesEachTargetSchedule() {
        UUID sourceId = UUID.randomUUID();
        UUID firstTargetId = UUID.randomUUID();
        UUID secondTargetId = UUID.randomUUID();
        DentistSchedule sourceSchedule = DentistSchedule.create(sourceId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), 30, false, null, null);
        when(scheduleRepository.findByDentistId(sourceId)).thenReturn(List.of(sourceSchedule));
        when(userRepository.findAll()).thenReturn(List.of(user(sourceId, "source@test.com"), user(firstTargetId, "first@test.com"), user(secondTargetId, "second@test.com")));

        var response = new CopyDentistScheduleService(scheduleRepository, userRepository).copySchedule(sourceId, null, true);

        assertEquals(1, response.size());
        verify(scheduleRepository).deleteByDentistId(firstTargetId);
        verify(scheduleRepository).deleteByDentistId(secondTargetId);
        verify(scheduleRepository, never()).deleteByDentistId(sourceId);
        verify(scheduleRepository, org.mockito.Mockito.times(2)).saveAll(schedulesCaptor.capture());
        assertEquals(Set.of(firstTargetId, secondTargetId), schedulesCaptor.getAllValues().stream().map(s -> s.getFirst().getDentistId()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void generateSlots_skipsExistingAndBreakSlots() {
        UUID dentistId = UUID.randomUUID();
        LocalDate monday = LocalDate.of(2026, 9, 7);
        DentistSchedule schedule = DentistSchedule.create(dentistId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true, LocalTime.of(10, 0), LocalTime.of(11, 0));
        AvailabilitySlot existing = AvailabilitySlot.create(dentistId, monday, LocalTime.of(9, 0), LocalTime.of(9, 30));
        when(scheduleRepository.findByDentistId(dentistId)).thenReturn(List.of(schedule));
        when(slotRepository.existsByDentistIdAndDateAndStartTime(dentistId, monday, LocalTime.of(9, 0))).thenReturn(true);
        when(slotRepository.findByDentistIdAndDateBetween(dentistId, monday, monday)).thenReturn(List.of(existing));
        when(slotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = new GenerateSlotsService(scheduleRepository, slotRepository).execute(dentistId, monday, monday);

        assertEquals(1, response.size());
        verify(slotRepository).saveAll(slotsCaptor.capture());
        assertEquals(List.of(LocalTime.of(9, 30), LocalTime.of(11, 0), LocalTime.of(11, 30)), slotsCaptor.getValue().stream().map(AvailabilitySlot::getStartTime).toList());
    }

    @Test
    void getAvailableSlots_mapsRepositoryResults() {
        UUID dentistId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 7);
        AvailabilitySlot slot = AvailabilitySlot.create(dentistId, date, LocalTime.of(9, 0), LocalTime.of(9, 30));
        when(slotRepository.findByDentistIdAndDateBetweenAndStatus(eq(dentistId), eq(date), eq(date), eq(com.odontosys.odontosys_api.domain.model.SlotStatus.AVAILABLE))).thenReturn(List.of(slot));

        var response = new GetAvailableSlotsService(slotRepository).execute(dentistId, date, date);

        assertEquals(1, response.size());
        assertEquals(LocalTime.of(9, 0), response.getFirst().startTime());
    }

    @Test
    void copySchedule_withoutSourceSchedule_doesNotModifyTargets() {
        UUID sourceId = UUID.randomUUID();
        when(scheduleRepository.findByDentistId(sourceId)).thenReturn(List.of());

        var response = new CopyDentistScheduleService(scheduleRepository, userRepository).copySchedule(sourceId, UUID.randomUUID(), false);

        assertTrue(response.isEmpty());
        verify(scheduleRepository, never()).deleteByDentistId(org.mockito.ArgumentMatchers.any());
    }

    private User user(UUID id, String email) {
        User created = User.create(email, "hash", "Test", "Dentist", "1", id.toString(), Set.of(Role.DENTIST));
        return User.reconstitute(id, created.getEmail(), created.getPasswordHash(), created.getFirstName(), created.getLastName(), created.getPhone(), created.getDocumentNumber(), true, created.getRoles(), created.getCreatedAt(), created.getUpdatedAt(), null);
    }
}
