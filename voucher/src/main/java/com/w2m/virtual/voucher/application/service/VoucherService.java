package com.w2m.virtual.voucher.application.service;

import com.w2m.virtual.voucher.application.port.input.IssueVoucherInputPort;
import com.w2m.virtual.voucher.application.port.output.EmailSenderOutputPort;
import com.w2m.virtual.voucher.application.port.output.PdfRendererOutputPort;
import com.w2m.virtual.voucher.application.port.output.VoucherRepositoryOutputPort;
import com.w2m.virtual.voucher.domain.IssueVoucherCommand;
import com.w2m.virtual.voucher.domain.Voucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de emisión de vouchers — orquesta render PDF + envío email + persistencia.
 *
 * <p>El envío de email es best-effort: si falla, el voucher queda persistido con estado
 * {@link Voucher#STATUS_FAILED_DELIVERY} para poder ser descargado posteriormente.</p>
 */
@Service
public class VoucherService implements IssueVoucherInputPort {

    private static final Logger log = LoggerFactory.getLogger(VoucherService.class);

    private final VoucherRepositoryOutputPort repository;
    private final PdfRendererOutputPort pdfRenderer;
    private final EmailSenderOutputPort emailSender;

    public VoucherService(VoucherRepositoryOutputPort repository,
                          PdfRendererOutputPort pdfRenderer,
                          EmailSenderOutputPort emailSender) {
        this.repository = repository;
        this.pdfRenderer = pdfRenderer;
        this.emailSender = emailSender;
    }

    @Override
    public Voucher issue(IssueVoucherCommand cmd) {
        Voucher v = new Voucher(
                UUID.randomUUID(),
                cmd.bookingId(),
                cmd.localizador(),
                cmd.holderName(),
                cmd.holderEmail(),
                cmd.hotelName(),
                cmd.city(),
                cmd.checkInDate(),
                cmd.checkOutDate(),
                cmd.guests(),
                cmd.totalAmount(),
                cmd.currency(),
                Voucher.STATUS_SENT,
                Instant.now(),
                null
        );

        // 1. Render PDF
        byte[] pdf = pdfRenderer.render(v);
        Voucher withPdf = new Voucher(
                v.voucherId(), v.bookingId(), v.localizador(), v.holderName(), v.holderEmail(),
                v.hotelName(), v.city(), v.checkInDate(), v.checkOutDate(), v.guests(),
                v.totalAmount(), v.currency(), v.status(), v.issuedAt(), pdf);

        // 2. Send email (best-effort)
        Voucher finalVoucher;
        try {
            emailSender.send(withPdf);
            finalVoucher = withPdf;
        } catch (RuntimeException ex) {
            log.warn("Envío de email falló para voucher {} ({}): {}", withPdf.voucherId(),
                    withPdf.holderEmail(), ex.getMessage());
            finalVoucher = withPdf.withStatus(Voucher.STATUS_FAILED_DELIVERY);
        }

        // 3. Persist
        return repository.save(finalVoucher);
    }

    public Optional<Voucher> get(UUID voucherId) {
        return repository.findById(voucherId);
    }

    public Optional<Voucher> getByBookingId(UUID bookingId) {
        return repository.findByBookingId(bookingId);
    }
}
