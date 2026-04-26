package com.parkin.mike.config;

import com.parkin.mike.repository.NanasScheduleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class NanasScheduleResetRunner {

    @Bean
    public ApplicationRunner clearNanasScheduleOnStartup(
            NanasScheduleRepository repository,
            @Value("${app.clear-schedule-on-startup:false}") boolean clearScheduleOnStartup
    ) {
        return args -> clearIfEnabled(repository, clearScheduleOnStartup);
    }

    @Transactional
    void clearIfEnabled(NanasScheduleRepository repository, boolean clearScheduleOnStartup) {
        if (!clearScheduleOnStartup) {
            return;
        }
        repository.deleteAllInBatch();
    }
}
