package com.payment.payment.product;


import com.payment.payment.dto.CreateOrderRequest;
import com.payment.payment.dto.CreateOrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") @RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private Long uid(HttpServletRequest req) {
        return Long.valueOf(req.getHeader("X-UID"));
    }

    // 소비자: 구매(결제)
    @PostMapping("/orders")
    public CreateOrderResponse buy(@RequestBody CreateOrderRequest reqBody, HttpServletRequest req) {
        return orderService.createAndPay(reqBody.token(), uid(req));
    }
}
