package com.parkin.mike.service;

import com.parkin.mike.model.NanasSchedule;
import com.parkin.mike.model.NanasScheduleEntry;
import com.parkin.mike.repository.NanasScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NanasServiceTest {

    @Mock
    private NanasScheduleRepository repository;

    @InjectMocks
    private NanasService service;

    @Test
    void setNanasDateRejectsNonSunday() {
        assertThatThrownBy(() -> service.setNanasDate(LocalDate.of(2026, 4, 27), true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date must be a Sunday");

        verifyNoInteractions(repository);
    }

    @Test
    void setNanasDateUpdatesExistingSundayEntry() {
        LocalDate sunday = LocalDate.of(2026, 4, 26);
        NanasSchedule existing = new NanasSchedule(sunday, false);
        when(repository.findById(sunday)).thenReturn(Optional.of(existing));

        service.setNanasDate(sunday, true);

        assertThat(existing.isAttending()).isTrue();
        verify(repository).save(existing);
    }

    @Test
    void setNanasDateCreatesNewSundayEntry() {
        LocalDate sunday = LocalDate.of(2026, 5, 3);
        when(repository.findById(sunday)).thenReturn(Optional.empty());

        service.setNanasDate(sunday, false);

        ArgumentCaptor<NanasSchedule> captor = ArgumentCaptor.forClass(NanasSchedule.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDate()).isEqualTo(sunday);
        assertThat(captor.getValue().isAttending()).isFalse();
    }

    @Test
    void isNanasOnThisWeekReturnsTrueWhenScheduled() {
        LocalDate thisSunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        when(repository.findById(thisSunday)).thenReturn(Optional.of(new NanasSchedule(thisSunday, true)));

        assertThat(service.isNanasOnThisWeek()).isTrue();
    }

    @Test
    void isNanasOnThisWeekReturnsFalseWhenNotScheduled() {
        LocalDate thisSunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        when(repository.findById(thisSunday)).thenReturn(Optional.empty());

        assertThat(service.isNanasOnThisWeek()).isFalse();
    }

    @Test
    void getScheduleReturnsSortedEntries() {
        when(repository.findAll()).thenReturn(List.of(
                new NanasSchedule(LocalDate.of(2026, 5, 10), true),
                new NanasSchedule(LocalDate.of(2026, 4, 26), false),
                new NanasSchedule(LocalDate.of(2026, 5, 3), true)
        ));

        List<NanasScheduleEntry> schedule = service.getSchedule();

        assertThat(schedule).containsExactly(
                new NanasScheduleEntry(LocalDate.of(2026, 4, 26), false),
                new NanasScheduleEntry(LocalDate.of(2026, 5, 3), true),
                new NanasScheduleEntry(LocalDate.of(2026, 5, 10), true)
        );
        verify(repository).findAll();
    }
}
