package com.example.ssauc.user.list.Service;

import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.list.dto.TempDto;
import com.example.ssauc.user.list.dto.WithLikeDto;
import com.example.ssauc.user.list.repository.ListRepository;
import java.time.Duration;

import jakarta.servlet.http.HttpSession;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ListService {
    @Autowired
    private ListRepository listRepository;

    public Page<TempDto> findAll(Pageable pageable, HttpSession session) {

        Page<ListDto> list = listRepository.getProductList(pageable);

        // ✅ Stream을 사용하여 ListDto → TempDto 변환
        List<TempDto> tempList = list.getContent().stream().map(listDto -> {
            Duration duration = Duration.between(listDto.getCreatedAt(), listDto.getEndAt());

            int days = (int) duration.toDays();
            int hours = (int) duration.toHours() % 24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());
            String inform = "⏳ %d일 %d시간".formatted(days, hours);
            String like = addCommas(String.valueOf(listDto.getLikeCount()));
            String price = addCommas(listDto.getPrice().toString());
            String[] mainImage = listDto.getImageUrl().split(",");

            return TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(mainImage[0])
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .build();
            }).toList();

        // ✅ Page<TempDto>로 변환하여 반환
        return new PageImpl<>(tempList, pageable, list.getTotalElements());
    }

    public Page<TempDto> list(Pageable pageable, HttpSession session) {
        Page<WithLikeDto> list = listRepository.getWithLikeProductList((Long) session.getAttribute("userId"), pageable);

        List<TempDto> tempList = list.getContent().stream().map(listDto -> {
            Duration duration = Duration.between(listDto.getCreatedAt(), listDto.getEndAt());

            int days = (int) duration.toDays();
            int hours = (int) duration.toHours() % 24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());
            String inform = "⏳ %d일 %d시간".formatted(days, hours);
            String like = addCommas(String.valueOf(listDto.getLikeCount()));
            String price = addCommas(listDto.getPrice().toString());
            String[] mainImage = listDto.getImageUrl().split(",");

            return TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(mainImage[0])
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .liked(listDto.isLiked())
                    .build();
        }).toList();

        return new PageImpl<>(tempList, pageable, list.getTotalElements());
    }


    public static String addCommas(String number) {
        try {
            double value = Double.parseDouble(number); // 실수도 처리 가능
            return String.format("%,.0f", value); // 천 단위마다 콤마 추가
        } catch (NumberFormatException e) {
            return "Invalid number format";
        }
    }

    public Page<TempDto> likelist(Pageable pageable, HttpSession session) {
        Page<WithLikeDto> list =  listRepository.getLikeList((Long) session.getAttribute("userId"), pageable);

        List<TempDto> tempList = list.getContent().stream().map(listDto -> {
            Duration duration = Duration.between(listDto.getCreatedAt(), listDto.getEndAt());

            int days = (int) duration.toDays();
            int hours = (int) duration.toHours() % 24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());
            String inform = "⏳ %d일 %d시간".formatted(days, hours);
            String like = addCommas(String.valueOf(listDto.getLikeCount()));
            String price = addCommas(listDto.getPrice().toString());
            String[] mainImage = listDto.getImageUrl().split(",");

            return TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(mainImage[0])
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .liked(listDto.isLiked())
                    .build();
        }).toList();

        return new PageImpl<>(tempList, pageable, list.getTotalElements());
    }

    public Page<TempDto> categoryList(Pageable pageable, HttpSession session, Long categoryId) {
        Long id = (Long)session.getAttribute("userId");
        Page<WithLikeDto> list = listRepository.getCategoryList(id, categoryId, pageable);

        List<TempDto> tempList = list.getContent().stream().map(listDto -> {
            Duration duration = Duration.between(listDto.getCreatedAt(), listDto.getEndAt());

            int days = (int) duration.toDays();
            int hours = (int) duration.toHours() % 24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());
            String inform = "⏳ %d일 %d시간".formatted(days, hours);
            String like = addCommas(String.valueOf(listDto.getLikeCount()));
            String price = addCommas(listDto.getPrice().toString());
            String[] mainImage = listDto.getImageUrl().split(",");

            return TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(mainImage[0])
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .liked(listDto.isLiked())
                    .build();
        }).toList();

        return new PageImpl<>(tempList, pageable, list.getTotalElements());
    }

    public Page<TempDto> getProductsByPrice(Pageable pageable, HttpSession session, int minPrice, int maxPrice) {
        Long userId = (Long) session.getAttribute("userId");
        Page<WithLikeDto> list = listRepository.findByPriceRange(userId, minPrice, maxPrice, pageable);

        List<TempDto> tempList = list.getContent().stream().map(listDto -> {
            Duration duration = Duration.between(listDto.getCreatedAt(), listDto.getEndAt());

            int days = (int) duration.toDays();
            int hours = (int) duration.toHours() % 24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());
            String inform = "⏳ %d일 %d시간".formatted(days, hours);
            String like = addCommas(String.valueOf(listDto.getLikeCount()));
            String price = addCommas(listDto.getPrice().toString());
            String[] mainImage = listDto.getImageUrl().split(",");

            return TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(mainImage[0])
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .liked(listDto.isLiked())
                    .build();
        }).toList();

        return new PageImpl<>(tempList, pageable, list.getTotalElements());
    }
}