package org.eskcti.mine.messages;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eskcti.mine.dto.QuotationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvents {
    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotationRequestEmmiter;

    public void sendNewKafkaEvent(QuotationDTO quotation) {
        LOG.info("-- Enviando Cotação para Tópico Kafka --");
        quotationRequestEmmiter.send(quotation).toCompletableFuture().join();
    }
}
