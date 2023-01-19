package com.ssafy.core.repository;

import com.ssafy.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * author @김정은
 * author @서재건
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 유저 이메일로 유저 반환
     *
     * @param email
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 유저 테이블 내 이메일 중복 체크
     *
     * @param email
     * @return
     * true 존재
     * false 없음
     */
    boolean existsByEmail(String email);

}
