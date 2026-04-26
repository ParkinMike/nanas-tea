package com.parkin.mike.repository;

import com.parkin.mike.model.NanasSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface NanasScheduleRepository extends JpaRepository<NanasSchedule, LocalDate> {}
