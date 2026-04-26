package com.parkin.mike.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class NanasModelsTest {

    @Test
    void nanasRequestAccessorsWork() {
        NanasRequest request = new NanasRequest();
        LocalDate date = LocalDate.of(2026, 4, 26);

        request.setDate(date);
        request.setOn(true);

        assertThat(request.getDate()).isEqualTo(date);
        assertThat(request.isOn()).isTrue();
    }

    @Test
    void nanasScheduleAccessorsWork() {
        LocalDate date = LocalDate.of(2026, 5, 3);
        NanasSchedule schedule = new NanasSchedule(date, true);

        schedule.setAttending(false);

        assertThat(schedule.getDate()).isEqualTo(date);
        assertThat(schedule.isAttending()).isFalse();
    }

    @Test
    void nanasScheduleProtectedConstructorIsAccessibleForJpa() throws Exception {
        Constructor<NanasSchedule> constructor = NanasSchedule.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        NanasSchedule schedule = constructor.newInstance();

        assertThat(schedule).isNotNull();
        assertThat(schedule.getDate()).isNull();
        assertThat(schedule.isAttending()).isFalse();
    }

    @Test
    void nanasScheduleEntryRecordStoresValues() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        NanasScheduleEntry entry = new NanasScheduleEntry(date, true);

        assertThat(entry.date()).isEqualTo(date);
        assertThat(entry.on()).isTrue();
    }
}
