package org.eskcti.mine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.Date;

@Jacksonized
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationDTO {
    private Date date;

    private BigDecimal currencyPrice;
}
