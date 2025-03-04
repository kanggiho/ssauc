package com.example.ssauc.user.pay.service;


import com.example.ssauc.user.bid.dto.OrderRequestDto;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentService {

    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;


    public boolean processPayment(OrderRequestDto dto){
        Product product = productRepository.findById(dto.getProductId()).orElse(null);
        Users buyer = usersRepository.findById(dto.getBuyerId()).orElse(null);
        Users seller = usersRepository.findById(dto.getSellerId()).orElse(null);







        return true;
    }
}
