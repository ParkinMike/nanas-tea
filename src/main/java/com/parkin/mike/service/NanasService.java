package com.parkin.mike.service;

import com.parkin.mike.model.NanasSchedule;
import com.parkin.mike.model.NanasScheduleEntry;
import com.parkin.mike.repository.NanasScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Service
public class NanasService {

    private final NanasScheduleRepository repository;

    public NanasService(NanasScheduleRepository repository) {
        this.repository = repository;
    }

    public void setNanasDate(LocalDate date, boolean isOn) {
        if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Date must be a Sunday, got: " + date.getDayOfWeek());
        }
        NanasSchedule entry = repository.findById(date).orElse(new NanasSchedule(date, isOn));
        entry.setAttending(isOn);
        repository.save(entry);
    }

    public boolean isNanasOnThisWeek() {
        LocalDate thisSunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return repository.findById(thisSunday).map(NanasSchedule::isAttending).orElse(false);
    }

    public List<NanasScheduleEntry> getSchedule() {
        return repository.findAll().stream()
                .map(e -> new NanasScheduleEntry(e.getDate(), e.isAttending()))
                .sorted(Comparator.comparing(NanasScheduleEntry::date))
                .toList();
    }
}
