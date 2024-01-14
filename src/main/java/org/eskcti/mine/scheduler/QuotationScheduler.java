package org.eskcti.mine.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eskcti.mine.messages.KafkaEvents;
import org.eskcti.mine.services.QuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QuotationScheduler {
    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Inject
    QuotationService quotationService;

    @Transactional
    @Scheduled(every = "35s", identity = "task-job")
    void schedule() throws JsonProcessingException {
        LOG.info("-------- Executando scheduler ------");
        quotationService.getCurrencyPrice();
    }

}
