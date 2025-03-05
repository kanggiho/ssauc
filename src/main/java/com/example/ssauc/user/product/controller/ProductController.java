package com.example.ssauc.user.product.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.dto.ProductInsertDto;
import com.example.ssauc.user.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    // GET: 상품 등록 페이지
    @GetMapping("/insert")
    public String insertPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = productService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "product/insert";
    }

    // POST: 상품 등록 처리 (AJAX로 호출)
    @PostMapping("/insert")
    @ResponseBody
    public ResponseEntity<String> insertProduct(@RequestBody ProductInsertDto productInsertDto, HttpSession session) {
        // 세션에서 판매자 정보 획득 (세션 키가 "user")
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        Users latestUser = productService.getCurrentUser(user.getUserId());
        productService.insertProduct(productInsertDto, latestUser);
        return ResponseEntity.ok("상품 등록 성공!");
    }

    // ↓ 새 엔드포인트 추가 (다중 파일 업로드) ↓
    @PostMapping("/uploadMultiple")
    @ResponseBody
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        // 최대 10장 제한 검증
        if (files.length > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최대 5장의 파일만 업로드 가능합니다.");
        }
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            // 파일 크기 3MB 이하 검증
            if (file.getSize() > 3 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 크기는 3MB를 초과할 수 없습니다.");
            }
            try {
                // 고유 파일명 생성 (현재시간 접두사)
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 변경됨: 고유 파일명 사용
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType()); // 변경됨: content type 설정
                // S3에 파일 업로드
                amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
                // S3 URL 생성
                String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
                fileUrls.add(fileUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
            }
        }
        // 여러 URL들을 콤마(,)로 구분하여 하나의 문자열로 결합
        String joinedUrls = String.join(",", fileUrls);
        Map<String, String> response = new HashMap<>();
        response.put("urls", joinedUrls);
        return ResponseEntity.ok(response);
    }

}
