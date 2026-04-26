package com.parkin.mike.config;

import com.parkin.mike.repository.NanasScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NanasScheduleResetRunnerTest {

    @Mock
    private NanasScheduleRepository repository;

    private final NanasScheduleResetRunner runnerConfig = new NanasScheduleResetRunner();

    @Test
    void clearIfEnabledDoesNothingWhenFlagIsFalse() {
        runnerConfig.clearIfEnabled(repository, false);

        verify(repository, never()).deleteAllInBatch();
    }

    @Test
    void clearIfEnabledDeletesAllRowsWhenFlagIsTrue() {
        runnerConfig.clearIfEnabled(repository, true);

        verify(repository).deleteAllInBatch();
    }

    @Test
    void applicationRunnerDelegatesToClearMethod() throws Exception {
        var runner = runnerConfig.clearNanasScheduleOnStartup(repository, true);

        runner.run(null);

        verify(repository).deleteAllInBatch();
    }
}
