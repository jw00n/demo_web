package com.example.demo.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

//엔티티 user에 매핑될 레포지터리
//JpaRepository를 extends 하면 findAll, save 등의 메소드를 기본적으로 사용가능
public interface UserRepository extends JpaRepository<User, Long>{
	@EntityGraph(attributePaths = "authorities")
	//쿼리가 수행될때 lazy조회가 아니고 Eager조회로 authorites정보를 같이 가져오게 됨.
	
	
	Optional<User> findOneWithAuthoritiesByUsername(String username);
	//username을 기준으로 User정보를 가져올때 권한정보도 같이 가져옴
	Optional<User> findOneWithAuthoritiesByPlatformAndUsername(String username,Integer platform);
	
	Optional<User> findByPlatformAndUsername(Integer platform,String username);
	Optional<User> findByUserId(Long userId);
	Optional<User> findByUsername(String username);	

}
