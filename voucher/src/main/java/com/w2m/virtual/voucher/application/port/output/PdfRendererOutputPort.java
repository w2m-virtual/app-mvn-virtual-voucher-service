package com.w2m.virtual.voucher.application.port.output;

import com.w2m.virtual.voucher.domain.Voucher;

/** Genera el PDF a partir del voucher. */
public interface PdfRendererOutputPort {
    byte[] render(Voucher voucher);
}
