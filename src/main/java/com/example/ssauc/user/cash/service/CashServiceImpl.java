package com.example.ssauc.user.cash.service;

import com.example.ssauc.exception.PortoneVerificationException;
import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.cash.repository.ChargeRepository;
import com.example.ssauc.user.cash.repository.WithdrawRepository;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.pay.entity.Payment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CashServiceImpl implements CashService {

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Value("${portone.secret.api}")
    private String portoneApiSecret;

    // 만약 필요하다면 웹훅 관련 키도 주입
    // @Value("${portone.secret.webhook}")
    // private String portoneWebhookSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // ===================== 충전 내역 =====================
    @Override
    public Page<ChargeDto> getChargesByUser(Users user, Pageable pageable) {
        Page<Charge> chargePage = chargeRepository.findByUser(user, pageable);
        return chargePage.map(ch -> ChargeDto.builder()
                .chargeId(ch.getChargeId())
                .chargeType(ch.getChargeType())
                .amount(ch.getAmount())
                .status(ch.getStatus())
                .createdAt(ch.getCreatedAt())
                .receiptUrl(ch.getReceiptUrl())
                .build());
    }

    @Override
    public Page<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Charge> chargePage = chargeRepository.findByUserAndCreatedAtBetween(user, startDate, endDate, pageable);
        return chargePage.map(ch -> ChargeDto.builder()
                .chargeId(ch.getChargeId())
                .chargeType(ch.getChargeType())
                .amount(ch.getAmount())
                .status(ch.getStatus())
                .createdAt(ch.getCreatedAt())
                .receiptUrl(ch.getReceiptUrl())
                .build());
    }

    // ===================== 환급 내역 =====================
    @Override
    public Page<WithdrawDto> getWithdrawsByUser(Users user, Pageable pageable) {
        // ※ WithdrawRepository에 Page<Withdraw> findByUser(Users user, Pageable pageable) 메서드가 필요합니다.
        Page<Withdraw> withdrawPage = withdrawRepository.findByUser(user, pageable);
        return withdrawPage.map(w -> {
            String status = (w.getWithdrawAt() != null) ? "완료" : "처리중";
            long netAmount = (w.getAmount() != null && w.getCommission() != null) ? w.getAmount() - w.getCommission() : 0;
            return WithdrawDto.builder()
                    .withdrawId(w.getWithdrawId())
                    .bank(w.getBank())
                    .account(w.getAccount())
                    .netAmount(netAmount)
                    .withdrawAt(w.getWithdrawAt())
                    .requestStatus(status)
                    .build();
        });
    }

    @Override
    public Page<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Withdraw> withdrawPage = withdrawRepository.findByUserAndWithdrawAtBetween(user, startDate, endDate, pageable);
        return withdrawPage.map(w -> {
            String status = (w.getWithdrawAt() != null) ? "완료" : "처리중";
            long netAmount = (w.getAmount() != null && w.getCommission() != null) ? w.getAmount() - w.getCommission() : 0;
            return WithdrawDto.builder()
                    .withdrawId(w.getWithdrawId())
                    .bank(w.getBank())
                    .account(w.getAccount())
                    .netAmount(netAmount)
                    .withdrawAt(w.getWithdrawAt())
                    .requestStatus(status)
                    .build();
        });
    }

    // ===================== 결제/정산 내역 =====================
    @Override
    public Page<CalculateDto> getCalculateByUser(Users user, Pageable pageable) {
        // ※ OrdersRepository에 Page<Orders> findBySellerOrBuyer(Users seller, Users buyer, Pageable pageable) 메서드 필요
        Page<Orders> ordersPage = ordersRepository.findBySellerOrBuyer(user, user, pageable);
        return ordersPage.map(order -> {
            boolean isSeller = order.getSeller().getUserId().equals(user.getUserId());
            Payment payment = (order.getPayments() != null && !order.getPayments().isEmpty())
                    ? order.getPayments().get(0) : null;
            String paymentAmount = isSeller ? ("+" + order.getTotalPrice()) : ("-" + order.getTotalPrice());
            LocalDateTime paymentTime = isSeller ? order.getCompletedDate() :
                    (payment != null ? payment.getPaymentDate() : null);
            String productName = order.getProduct().getName();
            return CalculateDto.builder()
                    .orderId(order.getOrderId())
                    .paymentAmount(paymentAmount)
                    .productName(productName)
                    .paymentTime(paymentTime)
                    .orderStatus(order.getOrderStatus())
                    .build();
        });
    }

    @Override
    public Page<CalculateDto> getCalculateByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Orders> ordersPage = ordersRepository.findBySellerOrBuyerAndPaymentTimeBetween(user, user, startDate, endDate, pageable);
        return ordersPage.map(order -> {
            boolean isSeller = order.getSeller().getUserId().equals(user.getUserId());
            Payment payment = (order.getPayments() != null && !order.getPayments().isEmpty())
                    ? order.getPayments().get(0) : null;
            String paymentAmount = isSeller ? ("+" + order.getTotalPrice()) : ("-" + order.getTotalPrice());
            LocalDateTime paymentTime = isSeller ? order.getCompletedDate() :
                    (payment != null ? payment.getPaymentDate() : null);
            String productName = order.getProduct().getName();
            return CalculateDto.builder()
                    .orderId(order.getOrderId())
                    .paymentAmount(paymentAmount)
                    .productName(productName)
                    .paymentTime(paymentTime)
                    .orderStatus(order.getOrderStatus())
                    .build();
        });
    }


    // ===================== 결제(Portone) 관련 기능 =====================
    @Override
    public Charge verifyAndCompletePayment(String paymentId, Long providedAmount, Users user) throws PortoneVerificationException {
        // 1. 결제 정보 사전 등록(pre‑register) API 호출 (POST)
        String preRegisterUrl = "https://api.portone.io/payments/" + paymentId + "/pre-register";
        // RestTemplate에 필요한 헤더(Authorization 등)를 추가하는 방법은
        // 예를 들어 HttpHeaders와 HttpEntity를 이용해서 구성할 수 있습니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "PortOne " + portoneApiSecret);

        // 사전 등록 요청 바디 구성 (필요한 값만 전달)
        // storeId는 생략하면 토큰에 담긴 상점 아이디 사용, taxFreeAmount는 0으로 설정할 수 있습니다.
        Map<String, Object> preRegisterRequest = new HashMap<>();
        preRegisterRequest.put("totalAmount", providedAmount);
        preRegisterRequest.put("taxFreeAmount", 0);
        preRegisterRequest.put("currency", "KRW");

        HttpEntity<Map<String, Object>> preRegisterEntity = new HttpEntity<>(preRegisterRequest, headers);
        ResponseEntity<String> preRegisterResponse = null;
        try {
            preRegisterResponse = restTemplate.exchange(preRegisterUrl, HttpMethod.POST, preRegisterEntity, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                // 409 Conflict: 이미 사전 등록된 결제 건이 있으므로, 계속 진행
                // Optionally, log a warning here.
            } else {
                throw new PortoneVerificationException("Pre-register API 호출 중 오류 발생", e);
            }
        }

        // If preRegisterResponse exists and is not successful, but it's not conflict, throw an error.
        if (preRegisterResponse != null && !preRegisterResponse.getStatusCode().is2xxSuccessful() &&
                preRegisterResponse.getStatusCode() != HttpStatus.CONFLICT) {
            throw new PortoneVerificationException("Pre-register API 호출 실패: " + preRegisterResponse.getStatusCode());
        }


        // 2. GET /payments/{paymentId}로 결제 상세 정보를 조회
        String getUrl = "https://api.portone.io/payments/" + paymentId;
        HttpEntity<String> getEntity = new HttpEntity<>(null, headers);  // GET 요청이므로 body는 null로 설정
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(getUrl, HttpMethod.GET, getEntity, String.class);
        } catch (Exception e) {
            throw new PortoneVerificationException("PortOne GET API 호출 중 오류 발생", e);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new PortoneVerificationException("PortOne GET API 호출 실패: " + responseEntity.getStatusCode());
        }

        // 3. JSON 응답 파싱
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap;
        try {
            responseMap = mapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new PortoneVerificationException("응답 파싱 실패", e);
        }

        // 4. PortOne 응답에서 필요한 필드 추출
        // (필드 이름은 PortOne API v2 문서를 참고) --> 로그에 찍힌 JSON 응답 기반
        String impUid = (String) responseMap.get("id"); // PortOne 고유 아이디

        // 결제 수단 처리
        String payMethod = null;
        Object methodObj = responseMap.get("method");
        if (methodObj instanceof Map) {
            Map<String, Object> methodMap = (Map<String, Object>) methodObj;
            String methodType = (String) methodMap.get("type");
            if ("PaymentMethodEasyPay".equals(methodType)) {
                // 간편 결제인 경우, provider 값을 이용하여 "간편결제(공급자)" 형태로 만듭니다.
                String provider = (String) methodMap.get("provider");
                payMethod = provider != null ? "간편결제(" + provider + ")" : "간편결제";
            } else if ("PaymentMethodCard".equals(methodType)) {
                // 카드 결제인 경우, card 객체 내 issuer 값을 이용하여 "카드 결제(발급사)" 형태로 만듭니다.
                Object cardObj = methodMap.get("card");
                if (cardObj instanceof Map) {
                    Map<String, Object> cardMap = (Map<String, Object>) cardObj;
                    String issuer = (String) cardMap.get("issuer");
                    payMethod = issuer != null ? "카드 결제(" + issuer + ")" : "카드 결제";
                } else {
                    payMethod = "카드 결제";
                }
            } else {
                // 그 외의 경우에는 기본적으로 type과 provider를 이어서 사용
                String provider = (String) methodMap.get("provider");
                if (methodType != null && provider != null) {
                    payMethod = methodType + " - " + provider;
                } else if (methodType != null) {
                    payMethod = methodType;
                } else if (provider != null) {
                    payMethod = provider;
                }
            }
        }

        // amountObj가 null이면 클라이언트에서 제공한 금액 사용
        // amount 객체 내에서 총 결제 금액 추출
        long parsedAmount;
        Object amountObj = responseMap.get("amount");
        if (amountObj instanceof Map) {
            Map<String, Object> amountMap = (Map<String, Object>) amountObj;
            if (amountMap.get("total") != null) {
                try {
                    parsedAmount = Long.parseLong(amountMap.get("total").toString());
                } catch (Exception e) {
                    throw new PortoneVerificationException("amount 파싱 실패", e);
                }
            } else {
                parsedAmount = providedAmount;
            }
        } else {
            // fallback: 클라이언트에서 전달한 값 사용
            parsedAmount = providedAmount;
        }
        // 결제 금액이 다르면 오류 발생 (클라이언트에서 조작했을 가능성 있음)
        if (parsedAmount != providedAmount) {
            throw new PortoneVerificationException("결제 금액 불일치: 제공된 금액(" + providedAmount + ") vs 실제 결제 금액(" + parsedAmount + ")");
        }

        // 결제 상태 (예: "PAID")
        String status = (String) responseMap.get("status");
        // 결제 실패 사유 (없을 수 있으므로 null 허용)
        String failReason = (String) responseMap.get("fail_reason");
        // 영수증 URL
        String receiptUrl = (String) responseMap.get("receiptUrl");


        // paid_at 처리 (ISO‑8601 문자열을 한국 시간으로 변환)
        String paidAtStr = (String) responseMap.get("paidAt");
        LocalDateTime paidAt;
        if (paidAtStr == null || paidAtStr.trim().isEmpty()) {
            paidAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        } else {
            try {
                ZonedDateTime zdt = ZonedDateTime.parse(paidAtStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                ZonedDateTime koreaZdt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                paidAt = koreaZdt.toLocalDateTime();
            } catch (Exception e) {
                paidAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            }
        }

        // 5. 새 Charge 객체 생성
        Charge charge = Charge.builder()
                .user(user)
                .impUid(impUid)
                .chargeType(payMethod)
                .amount(parsedAmount)
                .status(status)
                .details(failReason)
                .receiptUrl(receiptUrl)
                .createdAt(paidAt)
                .build();

        // 6. DB에 Charge 저장
        chargeRepository.save(charge);

        // 7. 결제 성공 시 사용자 잔액 업데이트 (결과를 DB에 반영)
        if ("PAID".equalsIgnoreCase(status)) {
            // 예시: 사용자 객체의 캐시 값을 업데이트하고, 세션에 다시 저장
            user.setCash(user.getCash() + parsedAmount);
            usersRepository.save(user);
        }

        // 응답 json 확인용
        String responseBody = responseEntity.getBody();
        System.out.println("Raw PortOne API Response: " + responseBody);

        return charge;
    }


//    @Override
//    public void handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature)
//            throws PortoneVerificationException {
//        // 웹훅 검증 및 처리 로직 구현
//        // 예시: webhook 서명 검증 후, body에서 paymentId 추출하여 verifyAndCompletePayment() 호출
//    }
}
