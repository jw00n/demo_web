package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.demo.dto.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token extends BaseTimeEntity{
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String token_type;

    public Token(String username, String refreshToken, String token_type) {
    this.username=username;
    this.refreshToken=refreshToken;
    this.token_type=token_type;
    }

}
