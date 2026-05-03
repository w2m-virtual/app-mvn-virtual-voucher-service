package com.w2m.virtual.voucher.infrastructure.adapter.input.rest;

import com.w2m.virtual.voucher.application.service.VoucherService;
import com.w2m.virtual.voucher.domain.Voucher;
import com.w2m.virtual.voucher.infrastructure.adapter.input.rest.data.VoucherDtos.IssueRequest;
import com.w2m.virtual.voucher.infrastructure.adapter.input.rest.data.VoucherDtos.IssueResponse;
import com.w2m.virtual.voucher.infrastructure.adapter.input.rest.data.VoucherDtos.VoucherResponse;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Endpoints {@code /api/vouchers/*}. */
@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestAdapter {

    private final VoucherService service;

    public VoucherRestAdapter(VoucherService service) {
        this.service = service;
    }

    @PostMapping("/issue")
    public ResponseEntity<IssueResponse> issue(@Valid @RequestBody IssueRequest req) {
        Voucher v = service.issue(req.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(IssueResponse.from(v));
    }

    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> get(@PathVariable UUID voucherId) {
        return service.get(voucherId)
                .map(VoucherResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{voucherId}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable UUID voucherId) {
        return service.get(voucherId)
                .map(v -> {
                    HttpHeaders h = new HttpHeaders();
                    h.setContentType(MediaType.APPLICATION_PDF);
                    h.setContentDisposition(ContentDisposition.inline()
                            .filename("voucher-" + v.localizador() + ".pdf").build());
                    h.setContentLength(v.pdfBytes() == null ? 0 : v.pdfBytes().length);
                    return new ResponseEntity<>(v.pdfBytes(), h, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<VoucherResponse> getByBooking(@PathVariable UUID bookingId) {
        return service.getByBookingId(bookingId)
                .map(VoucherResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
