package com.example.ssauc.user.main.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.main.entity.Notification;
import com.example.ssauc.user.main.service.NotificationResponseService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalNotificationAdvice {

    private final NotificationResponseService notificationService;
    private final UsersRepository userRepository;

    public GlobalNotificationAdvice(NotificationResponseService notificationService, UsersRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addNotificationsToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else if (principal instanceof Users) {
                // principal이 Users 객체라면, getUserName() 메소드를 사용하도록 함
                username = ((Users) principal).getUserName();
            } else {
                username = principal.toString();
            }

            // 디버깅 로그 추가 (필요시)
            System.out.println("GlobalNotificationAdvice - 추출된 username: " + username);

            Optional<Users> userOpt = userRepository.findByUserName(username);
            if (!userOpt.isPresent()) {
                userOpt = userRepository.findByEmail(username);
            }

            Users user = userOpt.orElseThrow(() ->
                    new UsernameNotFoundException("User not found with username or email: " + username));

            List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user.getUserId());
            for (Notification notification : unreadNotifications) {
                System.out.println(notification.getMessage());
            }
            System.out.println(unreadNotifications.size() + " 사이즈");
            model.addAttribute("notifications", unreadNotifications);
        }
    }
}
