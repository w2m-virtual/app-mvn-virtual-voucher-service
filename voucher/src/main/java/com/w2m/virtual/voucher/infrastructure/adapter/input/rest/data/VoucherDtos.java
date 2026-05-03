package com.w2m.virtual.voucher.infrastructure.adapter.input.rest.data;

import com.w2m.virtual.voucher.domain.IssueVoucherCommand;
import com.w2m.virtual.voucher.domain.Voucher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** DTOs del subdominio voucher — endpoints {@code /api/vouchers/*}. */
public final class VoucherDtos {

    private VoucherDtos() {}

    public record IssueRequest(
            @NotNull UUID bookingId,
            @NotBlank String localizador,
            @NotBlank String holderName,
            @NotBlank @Email String holderEmail,
            @NotBlank String hotelName,
            @NotBlank String city,
            @NotNull LocalDate checkInDate,
            @NotNull LocalDate checkOutDate,
            @Min(1) int guests,
            @NotNull BigDecimal totalAmount,
            @NotBlank String currency
    ) {
        public IssueVoucherCommand toCommand() {
            return new IssueVoucherCommand(bookingId, localizador, holderName, holderEmail,
                    hotelName, city, checkInDate, checkOutDate, guests, totalAmount, currency);
        }
    }

    public record IssueResponse(
            UUID voucherId,
            UUID bookingId,
            String status,
            Instant issuedAt,
            String deliveredTo
    ) {
        public static IssueResponse from(Voucher v) {
            return new IssueResponse(v.voucherId(), v.bookingId(), v.status(), v.issuedAt(),
                    v.holderEmail());
        }
    }

    public record VoucherResponse(
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
            Instant issuedAt
    ) {
        public static VoucherResponse from(Voucher v) {
            return new VoucherResponse(v.voucherId(), v.bookingId(), v.localizador(),
                    v.holderName(), v.holderEmail(), v.hotelName(), v.city(),
                    v.checkInDate(), v.checkOutDate(), v.guests(), v.totalAmount(),
                    v.currency(), v.status(), v.issuedAt());
        }
    }
}
