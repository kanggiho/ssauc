package com.example.ssauc.user.bid.service;

import com.example.ssauc.user.bid.dto.CarouselImage;
import com.example.ssauc.user.bid.dto.ProductInformDto;
import com.example.ssauc.user.bid.dto.ReportDto;
import com.example.ssauc.user.bid.entity.ProductReport;
import com.example.ssauc.user.bid.repository.BidRepository;
import com.example.ssauc.user.bid.repository.PdpRepository;
import com.example.ssauc.user.bid.repository.ProductReportRepository;
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

    public void insertReportData(ReportDto dto){
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
}
