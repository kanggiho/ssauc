package com.example.ssauc.user.login.repository;

import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
  Optional<Users> findByEmail(String email);


  @Transactional
  @Modifying
  @Query("update Users u set u.cash = u.cash + ?1 where u.userId = ?2")
  void addCash(Long cash, Long userId);

  @Transactional
  @Modifying
  @Query("update Users u set u.cash = u.cash - ?1 where u.userId = ?2")
  void minusCash(Long cash, Long userId);

  @Transactional
  @Modifying
  @Query("UPDATE Users u SET u.warningCount = u.warningCount + :warningCount WHERE u.userId = :userId")
  int updateUserByWarningCount(@Param("warningCount") int warningCount, @Param("userId") Long userId);



  Optional<Users> findByUserNameAndPhone(String userName, String phone);
  boolean existsByEmail(String email);
  boolean existsByUserName(String userName);
  boolean existsByPhone(String phone);



}