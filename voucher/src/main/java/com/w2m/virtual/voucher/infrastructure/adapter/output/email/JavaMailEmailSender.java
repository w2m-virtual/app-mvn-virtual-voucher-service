package com.w2m.virtual.voucher.infrastructure.adapter.output.email;

import com.w2m.virtual.voucher.application.port.output.EmailSenderOutputPort;
import com.w2m.virtual.voucher.domain.Voucher;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Adapter de envío de email vía {@link JavaMailSender} (SMTP — mailpit en local).
 * Adjunta el PDF del voucher.
 */
@Component
public class JavaMailEmailSender implements EmailSenderOutputPort {

    private final JavaMailSender mailSender;
    private final String from;

    public JavaMailEmailSender(JavaMailSender mailSender,
                               @Value("${voucher.from:noreply@w2m-virtual.local}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(Voucher v) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(v.holderEmail());
            helper.setSubject(String.format("Tu reserva en %s — localizador %s",
                    v.hotelName(), v.localizador()));
            helper.setText(buildBody(v), false);
            helper.addAttachment("voucher-" + v.localizador() + ".pdf",
                    new ByteArrayResource(v.pdfBytes()));
            mailSender.send(msg);
        } catch (MessagingException | MailException ex) {
            throw new EmailDeliveryException("No se pudo enviar email a " + v.holderEmail(), ex);
        }
    }

    private static String buildBody(Voucher v) {
        return new StringBuilder()
                .append("Hola ").append(v.holderName()).append(",\n\n")
                .append("Tu reserva en ").append(v.hotelName())
                .append(" (").append(v.city()).append(") ha sido confirmada.\n\n")
                .append("Localizador: ").append(v.localizador()).append('\n')
                .append("Check-in:    ").append(v.checkInDate()).append('\n')
                .append("Check-out:   ").append(v.checkOutDate()).append('\n')
                .append("Huéspedes:   ").append(v.guests()).append('\n')
                .append("Total:       ").append(v.totalAmount().toPlainString())
                .append(' ').append(v.currency()).append('\n')
                .append('\n')
                .append("Adjuntamos tu voucher en PDF. Consérvalo para el check-in.\n\n")
                .append("World to Meet Virtual\n")
                .toString();
    }

    /** Excepción específica para que el service la diferencie. */
    public static class EmailDeliveryException extends RuntimeException {
        public EmailDeliveryException(String msg, Throwable cause) { super(msg, cause); }
    }
}
