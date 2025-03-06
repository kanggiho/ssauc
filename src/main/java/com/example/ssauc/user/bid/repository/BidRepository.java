package com.example.ssauc.user.bid.repository;

import com.example.ssauc.user.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("SELECT b.user.userId FROM Bid b WHERE b.bidPrice = (SELECT MAX(b2.bidPrice) FROM Bid b2)")
    Long findUserIdWithHighestBidPrice();


}