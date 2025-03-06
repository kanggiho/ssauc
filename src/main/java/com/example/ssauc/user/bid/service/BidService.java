package com.example.ssauc.user.bid.service;

import com.example.ssauc.user.bid.dto.*;
import com.example.ssauc.user.bid.entity.AutoBid;
import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.bid.entity.ProductReport;
import com.example.ssauc.user.bid.repository.AutoBidRepository;
import com.example.ssauc.user.bid.repository.BidRepository;
import com.example.ssauc.user.bid.repository.PdpRepository;
import com.example.ssauc.user.bid.repository.ProductReportRepository;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BidService {
    @Autowired
    private PdpRepository pdpRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ProductReportRepository productReportRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AutoBidRepository autoBidRepository;




    public ProductInformDto getBidInform(Long productId) {
        // pdp에 들어갈 정보 가져오기
        ProductInformDto informDto = pdpRepository.getPdpInform(productId);

        // 전체 시간 구하기
        Long totalTime = Duration.between(LocalDateTime.now(), informDto.getEndAt()).getSeconds();
        informDto.setTotalTime(totalTime);
        return informDto;
    }

    public List<CarouselImage> getCarouselImages(Long productId) {
        // pdp에 들어갈 정보 가져오기
        ProductInformDto informDto = pdpRepository.getPdpInform(productId);

        List<CarouselImage> CarouselImages = new ArrayList<>();
        String[] urls = informDto.getImageUrl().split(",");
        int i = 1;
        for (String url : urls) {
            CarouselImage image = new CarouselImage();
            image.setUrl(url);
            image.setAlt("Slide " + i);
            i++;
            CarouselImages.add(image);
        }
        return CarouselImages;
    }

    public void insertReportData(ReportDto dto) {
        Long reportedUserId = pdpRepository.findById(dto.getProductId()).get().getSeller().getUserId();
        dto.setReportedUserId(reportedUserId);

        ProductReport productReport = ProductReport.builder()
                .product(pdpRepository.findById(dto.getProductId()).orElseThrow())
                .reporter(usersRepository.findById(dto.getReporterId()).orElseThrow())
                .reportedUser(usersRepository.findById(dto.getReportedUserId()).orElseThrow())
                .reportReason(dto.getReportReason())
                .status("처리전")
                .details(dto.getDetails())
                .reportDate(LocalDateTime.now())
                .processedAt(null)
                .build();

        productReportRepository.save(productReport);
    }

    public Product getProduct(Long productId) {
        return pdpRepository.findById(productId).orElseThrow();
    }

    public Users getUser(Long userId) {
        return usersRepository.findById(userId).orElseThrow();
    }


    // 일반 입찰 기능 구현
    public boolean placeBid(BidRequestDto bidRequestDto) {
        // 예시: 제품 ID로 상품 조회 후 입찰 금액 검증 및 입찰 수 증가 처리 등 로직 구현

        Product bidProduct = getProduct(bidRequestDto.getProductId());
        Users bidUser = getUser(Long.valueOf(bidRequestDto.getUserId()));


        if (pdpRepository.updateProductField((long) bidRequestDto.getBidAmount(), bidRequestDto.getProductId()) != 0) {
            // Bid 객체 만들고 저장
            Bid bid = Bid.builder()
                    .product(bidProduct)
                    .user(bidUser)
                    .bidPrice((long) bidRequestDto.getBidAmount())
                    .bidTime(LocalDateTime.now())
                    .build();
            bidRepository.save(bid);
            return true;
        }


        return false;

    }

    // 자동 입찰 기능 구현
    public boolean autoBid(AutoBidRequestDto autoBidRequestDto) {
        Long productId = autoBidRequestDto.getProductId();
        String userIdStr = autoBidRequestDto.getUserId();
        Long maxBidAmount = (long)autoBidRequestDto.getMaxBidAmount();

        Product product = getProduct(productId);
        Users user = getUser(Long.valueOf(userIdStr));

        // 이미 같은 (user, product)에 대한 자동입찰이 있는지 확인
        AutoBid existingAuto = autoBidRepository
                .findByProductAndActiveIsTrue(product)
                .stream()
                .filter(ab -> ab.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);

        if (existingAuto != null) {
            // 기존에 등록한 자동입찰이 있을 경우 maxBidAmount 갱신
            existingAuto.setMaxBidAmount(maxBidAmount);
            autoBidRepository.save(existingAuto);
        } else {
            // 없으면 새로 생성
            AutoBid newAutoBidding = AutoBid.builder()
                    .product(product)
                    .user(user)
                    .maxBidAmount(maxBidAmount)
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build();
            autoBidRepository.save(newAutoBidding);
        }

        // 이제 등록만 하는 것이 아니라,
        // 혹시 새로 등록된/갱신된 maxBidAmount가 현재가보다 높다면 자동으로 입찰 동작을 실행한다.
        processAutoBidding(productId);

        return true;
    }

    // 자동입찰자들이 현재가를 상회할 수 있다면 계속 입찰하도록 처리
    private void processAutoBidding(Long productId) {
        Product product = getProduct(productId);
        Long currentPrice = product.getTempPrice(); // Product 테이블의 현재가(tempPrice) 사용

        // (1) 이 상품에 대해 active=true인 자동입찰 목록 조회
        List<AutoBid> autoBidders = autoBidRepository.findByProductAndActiveIsTrue(product);

        // (2) 우선순위 정하기
        //   예: "먼저 등록한 순" → createdAt ASC
        //   예: "최대입찰금액이 높은 순" → maxBidAmount DESC
        //   여기는 createdAt ASC + maxBidAmount DESC 복합으로 정렬 예시:
        autoBidders.sort((a, b) -> {
            // 먼저 maxBidAmount 큰 순
            int cmp = b.getMaxBidAmount().compareTo(a.getMaxBidAmount());
            if(cmp != 0) return cmp;
            // 같다면 createdAt 오름차순
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        });

        // (3) 최소 입찰 단위 (상품마다 있을 수 있음)
        Long minIncrement = (long)product.getMinIncrement();

        // (4) while문 or for문으로 “더 이상 가격 상승이 없을 때까지” 반복
        boolean updated = true;
        while (updated) {
            updated = false;

            // 자동입찰 목록 순회
            for (AutoBid ab : autoBidders) {
                // 1) 이미 비활성화되었는지 여부 체크
                if(!ab.isActive()) continue;

                // 2) 이 사람이 낼 수 있는 "다음 입찰금"은 currentPrice + minIncrement
                Long nextBid = currentPrice + minIncrement;
                // 3) nextBid가 ab.getMaxBidAmount() 이하라면 입찰 가능
                if(nextBid <= ab.getMaxBidAmount()) {
                    // 입찰 처리
                    currentPrice = nextBid;  // 현재가 갱신
                    pdpRepository.updateProductField(currentPrice, productId);

                    // Bid 테이블에 기록 (autoBidMax에, 이 사람의 최대자동금액 기재)
                    Bid autoBid = Bid.builder()
                            .product(product)
                            .user(ab.getUser())
                            .bidPrice(currentPrice)
                            .bidTime(LocalDateTime.now())
                            .build();
                    bidRepository.save(autoBid);

                    updated = true;
                } else {
                    // 다음 입찰 불가 → 이 사용자의 자동입찰 비활성화할지 여부는 정책에 따라 결정
                    // ab.setActive(false);
                    // autoBiddingRepository.save(ab);
                }
            }
        }
    }


}
