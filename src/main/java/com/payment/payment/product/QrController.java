package com.payment.payment.product;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payment.payment.dto.ResolveQrResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.Duration;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class QrController {
    private final QrService qrService;

    private Long uid(HttpServletRequest req) {
        return Long.valueOf(req.getHeader("X-UID")); // 데모용. 실서비스는 JWT 사용.
    }

    @PostMapping("/products/{id}/qr")
    public ResponseEntity<byte[]> issueProductQr(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Long sellerId = uid(req);

        var qr = qrService.issueProductQr(sellerId, id, Duration.ofMinutes(10));
        String checkoutUrl = "http://localhost:3000/checkout?token=" + qr.getToken();

        // ✅ QR코드 이미지 생성
        QRCodeWriter qrWriter = new QRCodeWriter();
        var bitMatrix = qrWriter.encode(checkoutUrl, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(pngData);
    }

    // 소비자: QR 토큰 해석 → 상품 정보 가져오기
    @GetMapping("/qr/resolve")
    public ResolveQrResponse resolve(@RequestParam String token) {
        return qrService.resolve(token);
    }
}

