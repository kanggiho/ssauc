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
import com.example.ssauc.user.main.repository.ProductLikeRepository;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private ProductLikeRepository productLikeRepository;




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
        Product bidProduct = getProduct(bidRequestDto.getProductId());
        Users bidUser = getUser(Long.valueOf(bidRequestDto.getUserId()));
        long bidAmount = (long) bidRequestDto.getBidAmount();

        // 일반 입찰이 유효한지 검증 및 현재가 업데이트
        int updatedRows = pdpRepository.updateProductField(bidAmount, bidRequestDto.getProductId());
        if (updatedRows == 0) {
            // 입찰 조건이 맞지 않아 업데이트가 이루어지지 않았을 경우 false 반환
            return false;
        }

        // Bid 객체 만들고 저장
        Bid bid = Bid.builder()
                .product(bidProduct)
                .user(bidUser)
                .bidPrice(bidAmount)
                .bidTime(LocalDateTime.now())
                .build();
        bidRepository.save(bid);

        return true;
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
        //processAutoBidding(productId);

        return true;
    }

    private void processAutoBidding(Long productId) {
        Product product = getProduct(productId);
        Long currentPrice = product.getTempPrice(); // 현재가
        Long minIncrement = (long)product.getMinIncrement();

        // 1) 해당 상품의 active=true인 자동입찰 목록 조회
        List<AutoBid> autoBidders = autoBidRepository.findByProductAndActiveIsTrue(product);
        if (autoBidders.isEmpty()) {
            return; // 자동입찰자가 없으면 로직 종료
        }

        // 2) maxBidAmount 기준 내림차순 정렬
        autoBidders.sort((a, b) -> b.getMaxBidAmount().compareTo(a.getMaxBidAmount()));

        // 3) 1등(최대금액) / 2등(두 번째로 높은 금액) 찾기
        AutoBid topBidder = autoBidders.get(0);
        Long topMax = topBidder.getMaxBidAmount();


        Bid lastBid = bidRepository.findTopByProductOrderByBidTimeDesc(product).orElse(null);
        if (lastBid != null && lastBid.getUser().getUserId().equals(topBidder.getUser().getUserId())) {
            // 이미 최고입찰자인 경우 자동입찰 진행하지 않음
//            System.out.println("==============================================");
//            System.out.println("1번 실행됨");
//            System.out.println("==============================================");
            return;
        }

        Long secondMax;
        if (autoBidders.size() >= 2) {
            secondMax = Math.max(autoBidders.get(1).getMaxBidAmount(), pdpRepository.findById(productId).orElseThrow().getTempPrice());
        } else {
            // 자동입찰자가 1명뿐이면 2등이 없으니, '현재가' 정도로 처리
            secondMax = currentPrice;
        }

        // 4) 새 가격 계산:
        //    "2등 + minIncrement"가 1등의 maxBid를 넘으면 안 되므로
        Long desiredPrice = secondMax + minIncrement; // 2등보다 500원 더 높게
        if (desiredPrice > topMax) {
            desiredPrice = topMax;  // 1등이 지불할 최대치까지만
        }

        // 5) desiredPrice가 현재가보다 높다면 그만큼만 올린다.
        //    (경쟁자가 없을 때는 currentPrice가 이미 secondMax, 결국 + 500 해서 올림 or 그냥 유지)
        if (desiredPrice > currentPrice) {
            // DB에 반영
            pdpRepository.updateProductField(desiredPrice, productId);

            // Bid 테이블에도 기록
            Bid newBid = Bid.builder()
                    .product(product)
                    .user(topBidder.getUser())
                    .bidPrice(desiredPrice)
                    .bidTime(LocalDateTime.now())
                    .build();
            bidRepository.save(newBid);
        }
    }


    // 스케줄러 메서드: 매 분 0초마다 자동입찰 로직 실행
    @Scheduled(cron = "0 * * * * *")  // 초, 분, 시, 일, 월, 요일 순 (매 분 0초 실행)
    public void scheduledAutoBidding() {


        System.out.println("실행됨");
        System.out.println(pdpRepository.findById(56L).orElseThrow().getTempPrice());


        // active 상태인 자동입찰 데이터들에서 상품 목록 추출

        List<AutoBid> activeAutoBids = autoBidRepository.findAll().stream()
                .filter(AutoBid::isActive)
                .toList();

        Set<Product> productSet = activeAutoBids.stream()
                .map(AutoBid::getProduct)
                .collect(Collectors.toSet());

        // 각 상품에 대해 자동입찰 로직 실행
        for (Product product : productSet) {
            processAutoBidding(product.getProductId());
        }
    }





    public String getHighestBidUser() {
        Long tempUserId = bidRepository.findUserIdWithHighestBidPrice();

        if(tempUserId == null) {
            return "입찰자 없음";
        }else{
            Users user = usersRepository.findById(tempUserId).orElseThrow();
            return user.getUserName();
        }

    }

    public boolean isProductLike(Long productId, Long userId){
        return productLikeRepository.countByProductIdAndUserId(productId, userId) > 0;

    }


}
