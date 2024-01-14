package org.eskcti.mine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eskcti.mine.client.CurrencyPriceClient;
import org.eskcti.mine.dto.CurrencyPriceDTO;
import org.eskcti.mine.dto.QuotationDTO;
import org.eskcti.mine.entities.QuotationEntity;
import org.eskcti.mine.messages.KafkaEvents;
import org.eskcti.mine.repositories.QuotationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice() throws JsonProcessingException {
        String json = currencyPriceClient.getPriceByPair("USD-BRL");
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyPriceDTO currencyPriceInfo = objectMapper.readValue(json, CurrencyPriceDTO.class);
        if (currencyPriceInfo == null) {
            System.out.println("Moeda nula!");
            return;
        }
        if (updateCurrentInfoPrice(currencyPriceInfo)) {
            kafkaEvents.sendNewKafkaEvent(
                    QuotationDTO
                            .builder()
                            .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                            .date(new Date())
                            .build()
            );
        }
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {
        if (currencyPriceInfo.getUSDBRL() == null) {
            System.out.println("Pair nula!");
            return false;
        }
        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if (quotationList.isEmpty()) {
            saveQuotation(currencyPriceInfo);
            updatePrice = true;
        } else {
            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size() - 1);

            BigDecimal currentPriceRounded = currentPrice.setScale(2, RoundingMode.HALF_UP);
            BigDecimal lastDollarPriceRounded = lastDollarPrice.getCurrencyPrice().setScale(2, RoundingMode.HALF_UP);

            if (currentPriceRounded.compareTo(lastDollarPriceRounded) > 0) {
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }
        
        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyPriceInfo) {
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyPriceInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }

}
