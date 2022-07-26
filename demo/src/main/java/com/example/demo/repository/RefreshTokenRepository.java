package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Token;

import io.lettuce.core.dynamic.annotation.Param;

public interface RefreshTokenRepository extends JpaRepository<Token, Long>{
    
    Optional<Token> findByTokenId(Long tokenId);
    Optional<Token> findByUsername(String username);
    
    @Transactional
    void deleteByUsername(String username);
  
}
