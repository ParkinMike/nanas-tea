package com.parkin.mike.controller;

import com.parkin.mike.model.NanasRequest;
import com.parkin.mike.model.NanasScheduleEntry;
import jakarta.validation.Valid;
import com.parkin.mike.service.NanasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nanas")
public class NanasController {

    private final NanasService nanasService;

    public NanasController(NanasService nanasService) {
        this.nanasService = nanasService;
    }

    @GetMapping
    public ResponseEntity<String> isNanasOnThisWeek() {
        return ResponseEntity.ok(nanasService.isNanasOnThisWeek() ? "Yes" : "No");
    }

    @PostMapping
    public ResponseEntity<String> setNanasDate(@Valid @RequestBody NanasRequest request) {
        nanasService.setNanasDate(request.getDate(), request.isOn());
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<NanasScheduleEntry>> getSchedule() {
        return ResponseEntity.ok(nanasService.getSchedule());
    }
}
