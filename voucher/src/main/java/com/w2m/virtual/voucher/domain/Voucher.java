package com.w2m.virtual.voucher.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Voucher de reserva — agregado emitido tras booking + payment capture (S9 VOUCHER_DELIVERED).
 *
 * <p>Estados:</p>
 * <ul>
 *   <li>{@code SENT} — PDF generado y email entregado al SMTP correctamente.</li>
 *   <li>{@code FAILED_DELIVERY} — PDF generado pero el envío de email falló (se conserva para descarga).</li>
 * </ul>
 */
public record Voucher(
        UUID voucherId,
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
        String currency,
        String status,
        Instant issuedAt,
        byte[] pdfBytes
) {
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED_DELIVERY = "FAILED_DELIVERY";

    public Voucher withStatus(String newStatus) {
        return new Voucher(voucherId, bookingId, localizador, holderName, holderEmail,
                hotelName, city, checkInDate, checkOutDate, guests, totalAmount,
                currency, newStatus, issuedAt, pdfBytes);
    }
}
