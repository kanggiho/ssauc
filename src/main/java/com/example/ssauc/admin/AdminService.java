package com.example.ssauc.admin;


import com.example.ssauc.admin.entity.Admin;
import com.example.ssauc.admin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin checkAddress(String email, String password) {
        return adminRepository.findByEmailAndPassword(email, password).orElse(null);
    }

}
