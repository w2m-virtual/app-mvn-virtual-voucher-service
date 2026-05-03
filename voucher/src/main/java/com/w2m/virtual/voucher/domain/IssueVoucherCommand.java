package com.w2m.virtual.voucher.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Datos necesarios para emitir un voucher. */
public record IssueVoucherCommand(
        UUID bookingId,
        String localizador,
        String holderName,
        String holderEmail,
        String hotelName,
        String city,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guests,
        BigDecimal totalAmount,
        String currency
) {}
