package com.example.ssauc.user.list.Service;

import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.list.dto.TempDto;
import com.example.ssauc.user.list.repository.ListRepository;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListService {
    @Autowired
    private ListRepository listRepository;

    public List<TempDto> getSecondHandAuctionList() {
        List<ListDto> list = listRepository.getProductList();

        List<TempDto> temp = new ArrayList<>();

        for(ListDto listDto : list){

            Duration duration = Duration.between(listDto.getEndAt(), listDto.getCreatedAt());

            int days = (int)duration.toDays();
            int hours = (int)duration.toHours()%24;
            String bidCount = "입찰 %d회".formatted(listDto.getBidCount());

            String inform = "⏳ %d일 %d시간".formatted(days, hours);

            String like = "❤️"+" %s".formatted(addCommas(String.valueOf(listDto.getLikeCount())));
            String price = addCommas(listDto.getPrice().toString());


            TempDto tempDto = TempDto.builder()
                    .productId(listDto.getProductId())
                    .imageUrl(listDto.getImageUrl())
                    .name(listDto.getName())
                    .price(price)
                    .bidCount(bidCount)
                    .gap(inform)
                    .location(listDto.getLocation())
                    .likeCount(like)
                    .build();

            temp.add(tempDto);
        }

        return temp;
    }

    public static String addCommas(String number) {
        try {
            double value = Double.parseDouble(number); // 실수도 처리 가능
            return String.format("%,.0f", value); // 천 단위마다 콤마 추가
        } catch (NumberFormatException e) {
            return "Invalid number format";
        }
    }

}
