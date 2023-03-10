package com.ssafy.core.repository.review;

import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.Reservation;
import com.ssafy.core.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/***
 * review repo
 * @author 신민철
 * @author 서재건
 */
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    List<Review> findAllByPhotographer(Photographer photographer);

    List<Review> findAllByPhotographerId(Long photographerId);

    Optional<Review> findByReservation(Reservation reservation);

}
