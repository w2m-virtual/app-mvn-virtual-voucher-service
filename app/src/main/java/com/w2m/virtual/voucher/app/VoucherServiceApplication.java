package com.w2m.virtual.voucher.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.w2m.virtual.voucher")
public class VoucherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoucherServiceApplication.class, args);
    }
}
