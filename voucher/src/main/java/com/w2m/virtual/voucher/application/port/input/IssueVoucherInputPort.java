package com.w2m.virtual.voucher.application.port.input;

import com.w2m.virtual.voucher.domain.IssueVoucherCommand;
import com.w2m.virtual.voucher.domain.Voucher;

/** Caso de uso S9: emitir voucher (PDF + email). */
public interface IssueVoucherInputPort {
    Voucher issue(IssueVoucherCommand cmd);
}
