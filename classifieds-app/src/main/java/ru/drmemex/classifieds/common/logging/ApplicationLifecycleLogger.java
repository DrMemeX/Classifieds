package ru.drmemex.classifieds.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationLifecycleLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStarted() {
        log.info("Classifieds application started");
    }

    @EventListener(ContextClosedEvent.class)
    public void logApplicationStopped() {
        log.info("Classifieds application stopped");
    }
}
