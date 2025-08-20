package com.payment.payment.product;

import com.payment.auth.user.UserRepository;
import com.payment.payment.dto.CreateOrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final QrLinkRepository qrRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    @Transactional
    public CreateOrderResponse createAndPay(String token, Long buyerId) {
        var q = qrRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid token"));
        if (q.getExpiresAt().isBefore(Instant.now()) || q.getUsedCount() >= q.getMaxUse())
            throw new ResponseStatusException(HttpStatus.GONE, "expired/used");

        var p = productRepo.findById(q.getProductId()).orElseThrow();
        if (!p.isActive()) throw new ResponseStatusException(HttpStatus.GONE, "inactive product");
        if (buyerId.equals(q.getSellerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "self purchase not allowed");

        // 주문 생성
        var order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(q.getSellerId());
        order.equals(p.getId());
        order.setAmount(p.getPrice());
        order.setStatus("CREATED");
        orderRepo.save(order);

        // 결제(가상머니 이동)
        var buyer  = userRepo.findById(buyerId).orElseThrow();
        var seller = userRepo.findById(q.getSellerId()).orElseThrow();
        if (buyer.getBalance() < p.getPrice())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_FUNDS");

        buyer.setBalance(buyer.getBalance() - p.getPrice());
        seller.setBalance(seller.getBalance() + p.getPrice());
        order.setStatus("PAID");

        // 토큰 소진(단회용)
        q.setUsedCount(q.getUsedCount() + 1);
        if (q.getUsedCount() >= q.getMaxUse()) q.setUsed(true);

        return new CreateOrderResponse(order.getId(), order.getStatus());
    }
}
