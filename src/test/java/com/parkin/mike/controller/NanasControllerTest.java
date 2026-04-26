package com.parkin.mike.controller;

import com.parkin.mike.model.NanasRequest;
import com.parkin.mike.model.NanasScheduleEntry;
import com.parkin.mike.service.NanasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NanasControllerTest {

    @Mock
    private NanasService nanasService;

    @InjectMocks
    private NanasController controller;

    @Test
    void isNanasOnThisWeekReturnsYes() {
        when(nanasService.isNanasOnThisWeek()).thenReturn(true);

        var response = controller.isNanasOnThisWeek();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isEqualTo("Yes");
    }

    @Test
    void isNanasOnThisWeekReturnsNo() {
        when(nanasService.isNanasOnThisWeek()).thenReturn(false);

        var response = controller.isNanasOnThisWeek();

        assertThat(response.getBody()).isEqualTo("No");
    }

    @Test
    void setNanasDateDelegatesToService() {
        NanasRequest request = new NanasRequest();
        request.setDate(LocalDate.of(2026, 4, 26));
        request.setOn(true);

        var response = controller.setNanasDate(request);

        verify(nanasService).setNanasDate(LocalDate.of(2026, 4, 26), true);
        assertThat(response.getBody()).isEqualTo("Saved");
    }

    @Test
    void getScheduleReturnsServiceResponse() {
        List<NanasScheduleEntry> entries = List.of(new NanasScheduleEntry(LocalDate.of(2026, 4, 26), true));
        when(nanasService.getSchedule()).thenReturn(entries);

        var response = controller.getSchedule();

        assertThat(response.getBody()).isEqualTo(entries);
    }
}
