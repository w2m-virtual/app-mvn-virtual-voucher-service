package com.w2m.virtual.voucher.infrastructure.adapter.output.inmemory;

import com.w2m.virtual.voucher.application.port.output.VoucherRepositoryOutputPort;
import com.w2m.virtual.voucher.domain.Voucher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio in-memory: ConcurrentHashMap por voucherId + índice por bookingId. */
@Component
public class InMemoryVoucherRepository implements VoucherRepositoryOutputPort {

    private final Map<UUID, Voucher> byVoucherId = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> bookingIdToVoucherId = new ConcurrentHashMap<>();

    @Override
    public Voucher save(Voucher voucher) {
        byVoucherId.put(voucher.voucherId(), voucher);
        if (voucher.bookingId() != null) {
            bookingIdToVoucherId.put(voucher.bookingId(), voucher.voucherId());
        }
        return voucher;
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(byVoucherId.get(voucherId));
    }

    @Override
    public Optional<Voucher> findByBookingId(UUID bookingId) {
        UUID id = bookingIdToVoucherId.get(bookingId);
        return id == null ? Optional.empty() : findById(id);
    }
}
