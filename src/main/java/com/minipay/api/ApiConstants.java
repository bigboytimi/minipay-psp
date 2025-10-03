package com.minipay.api;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

@CrossOrigin
public abstract class ApiConstants {
    public static final String BASE_PATH = "/api";
    public static final String REGISTER = "/admin/auth/register";
    public static final String LOGIN = "/auth/login";
    public static final String REFRESH = "/auth/refresh";
    public static final String MERCHANTS = "/merchants";
    public static final String MERCHANTS_APPROVAL = "/merchants/approve/{id}";
    public static final String MERCHANTS_WITH_ID = "/merchants/{merchantId}";
    public static final String MERCHANT_CHARGE_SETTINGS = "/merchants/{merchantId}/charge-settings";
    public static final String PAYMENTS = "/payments";

    public static final String PAYMENTS_APPROVE = "/payments/{paymentRef}";

    public static final String PAYMENT_WITH_REFERENCE = "/payments/{paymentRef}";
    public static final String PROCESSOR_CALLBACK = "/simulate/processor-callback";
    public static final String SETTLEMENTS_GENERATE = "/settlements/generate";
    public static final String SETTLEMENTS ="/settlements";
    public static final String SETTLEMENTS_WITH_SETTLEMENT_REF ="/settlements/{settlementRef}";
    public static final String REPORTS_TRANSACTIONS ="/reports/transactions";
    public static final String REPORTS_SETTLEMENT ="/reports/settlement";
}
