package com.payment.payment.dto;

public record IssueQrResponse(String token, String checkoutUrl, String exp) {}
