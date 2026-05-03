package com.w2m.virtual.voucher.application.service;

import com.w2m.virtual.voucher.application.port.output.EmailSenderOutputPort;
import com.w2m.virtual.voucher.application.port.output.PdfRendererOutputPort;
import com.w2m.virtual.voucher.domain.IssueVoucherCommand;
import com.w2m.virtual.voucher.domain.Voucher;
import com.w2m.virtual.voucher.infrastructure.adapter.output.inmemory.InMemoryVoucherRepository;
import com.w2m.virtual.voucher.infrastructure.adapter.output.pdf.OpenPdfRenderer;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class VoucherServiceTest {

    private final PdfRendererOutputPort renderer = new OpenPdfRenderer();
    private final InMemoryVoucherRepository repo = new InMemoryVoucherRepository();

    private static IssueVoucherCommand sampleCmd() {
        return new IssueVoucherCommand(
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
                "EUR"
        );
    }

    @Test
    void issue_happy_path_marks_sent() {
        EmailSenderOutputPort emailSender = mock(EmailSenderOutputPort.class);
        VoucherService service = new VoucherService(repo, renderer, emailSender);

        Voucher result = service.issue(sampleCmd());

        assertThat(result.status()).isEqualTo(Voucher.STATUS_SENT);
        assertThat(result.voucherId()).isNotNull();
        assertThat(result.pdfBytes()).isNotNull().isNotEmpty();
        verify(emailSender, times(1)).send(any(Voucher.class));
        // Persistido y recuperable por bookingId
        assertThat(service.getByBookingId(result.bookingId())).isPresent();
    }

    @Test
    void issue_when_mail_fails_marks_failed_delivery() {
        EmailSenderOutputPort emailSender = mock(EmailSenderOutputPort.class);
        doThrow(new MailSendException("smtp down")).when(emailSender).send(any(Voucher.class));
        VoucherService service = new VoucherService(repo, renderer, emailSender);

        Voucher result = service.issue(sampleCmd());

        assertThat(result.status()).isEqualTo(Voucher.STATUS_FAILED_DELIVERY);
        assertThat(result.voucherId()).isNotNull();
        assertThat(result.pdfBytes()).isNotNull().isNotEmpty();
        // Voucher fue persistido aunque el email falló
        assertThat(service.get(result.voucherId())).isPresent();
    }

    @Test
    void getByBookingId_unknown_returnsEmpty() {
        EmailSenderOutputPort emailSender = mock(EmailSenderOutputPort.class);
        VoucherService service = new VoucherService(repo, renderer, emailSender);
        assertThat(service.getByBookingId(UUID.randomUUID())).isEmpty();
        verify(emailSender, never()).send(any(Voucher.class));
    }
}
