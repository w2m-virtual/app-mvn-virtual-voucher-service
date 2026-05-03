package com.w2m.virtual.voucher.application.port.output;

import com.w2m.virtual.voucher.domain.Voucher;

import java.util.Optional;
import java.util.UUID;

/** Persistencia in-memory del voucher (incluyendo bytes PDF). */
public interface VoucherRepositoryOutputPort {
    Voucher save(Voucher voucher);
    Optional<Voucher> findById(UUID voucherId);
    Optional<Voucher> findByBookingId(UUID bookingId);
}
