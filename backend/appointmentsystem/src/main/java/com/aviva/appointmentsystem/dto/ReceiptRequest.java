package com.aviva.appointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;

public record ReceiptRequest(
    @JsonAlias({"paymentId", "payment_id"})
    Long paymentId,
    @JsonAlias({"receiptNumber", "receipt_number"})
    String receiptNumber,
    BigDecimal amount,
    BigDecimal tax,
    String description
) {
}

