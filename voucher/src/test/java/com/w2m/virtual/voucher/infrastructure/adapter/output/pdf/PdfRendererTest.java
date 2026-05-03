package com.w2m.virtual.voucher.infrastructure.adapter.output.pdf;

import com.w2m.virtual.voucher.domain.Voucher;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PdfRendererTest {

    @Test
    void generates_valid_pdf_bytes() {
        OpenPdfRenderer renderer = new OpenPdfRenderer();
        Voucher v = new Voucher(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ABCD1234",
                "Juan Pérez",
                "juan@example.com",
                "Hotel Test",
                "Madrid",
                LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 6, 18),
                2,
                new BigDecimal("540.00"),
                "EUR",
                Voucher.STATUS_SENT,
                Instant.now(),
                null
        );

        byte[] bytes = renderer.render(v);

        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isGreaterThan(1000);
        // %PDF magic header
        assertThat(new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]})
                .isEqualTo("%PDF".getBytes());
    }
}
