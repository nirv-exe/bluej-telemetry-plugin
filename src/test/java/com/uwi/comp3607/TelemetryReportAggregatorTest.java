package com.uwi.comp3607;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TelemetryReportAggregatorTest {
    @Test
void shouldCalculateCompilationStatistics() {

    List<TelemetryEvent> events = List.of(
        CompilationEvent.successfulCompilation(),

        CompilationEvent.failedCompilation(
            "ERR_SYNTAX", "Missing semicolon", 10
        ),

        CompilationEvent.failedCompilation(
            "COMPILE_ATTEMPT_FAILED",
            "Compilation finished with errors", -1
        )
    );

    TelemetryReportAggregator report =
        new TelemetryReportAggregator(events);

    assertEquals(2, report.getTotalCompilations());
    assertEquals(1, report.getSuccessfulCompilations());
    assertEquals(1, report.getCompilationErrors());
    assertEquals(50.0, report.getCompileSuccessRate(), 0.001);
}
}
