package com.w2m.virtual.voucher.application.port.output;

import com.w2m.virtual.voucher.domain.Voucher;

/** Envía el voucher por email (puede fallar). */
public interface EmailSenderOutputPort {
    void send(Voucher voucher);
}
