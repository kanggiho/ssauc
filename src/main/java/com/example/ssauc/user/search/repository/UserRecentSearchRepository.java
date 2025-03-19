package com.example.ssauc.user.search.repository;


import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.search.entity.UserRecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRecentSearchRepository extends JpaRepository<UserRecentSearch, Long> {
  List<UserRecentSearch> findTop10ByUserOrderBySearchedAtDesc(Users user);
  List<UserRecentSearch> findTop10BySessionIdOrderBySearchedAtDesc(String sessionId);
  Optional<UserRecentSearch> findByUserAndKeyword(Users user, String keyword);
  Optional<UserRecentSearch> findBySessionIdAndKeyword(String sessionId, String keyword);
}