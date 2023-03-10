package com.ssafy.api.service;

import com.ssafy.api.dto.Photographer.PlacesForListDto;
import com.ssafy.api.dto.Reservation.CategoriesInfoResDto;
import com.ssafy.api.dto.Reservation.CategoryDetailDto;
import com.ssafy.api.dto.Reservation.NotificationDTO;
import com.ssafy.api.dto.Reservation.PhotographerReservationDto;
import com.ssafy.api.dto.Reservation.ReservationListDto;
import com.ssafy.api.dto.Reservation.ReservationReqDto;
import com.ssafy.api.dto.Reservation.ReservationResDto;
import com.ssafy.api.dto.Reservation.ReservationStatusDto;
import com.ssafy.api.dto.Reservation.ReviewDetailDto;
import com.ssafy.api.dto.Reservation.ReviewPostDto;
import com.ssafy.api.dto.Reservation.ReviewResDto;
import com.ssafy.core.code.ReservationStatus;
import com.ssafy.core.code.Role;
import com.ssafy.core.dto.CategoriesQdslDto;
import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.Places;
import com.ssafy.core.entity.Reservation;
import com.ssafy.core.entity.Review;
import com.ssafy.core.entity.User;
import com.ssafy.core.exception.CustomException;
import com.ssafy.core.exception.ErrorCode;
import com.ssafy.core.repository.review.ReviewRepository;
import com.ssafy.core.repository.UserRepository;
import com.ssafy.core.repository.photographer.PhotographerNCategoriesRepository;
import com.ssafy.core.repository.photographer.PhotographerNPlacesRepository;
import com.ssafy.core.repository.photographer.PhotographerRepository;
import com.ssafy.core.repository.reservation.ReservationRepository;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ?????? ?????? Service
 *
 * @author ?????????
 * @author ?????????
 * @author ?????????
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final PhotographerRepository photographerRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PhotographerNCategoriesRepository photographerNCategoriesRepository;
    private final PhotographerNPlacesRepository photographerNPlacesRepository;
    private final S3UploaderService s3UploaderService;
    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;
    private final AnalyzeService analyzeService;

    @Value("${pay.rest-api}")
    private String restApiKey;

    @Value("${pay.private-key}")
    private String privateKey;

    /**
     * ?????? ??????
     *
     * @param reservation
     */
    @Transactional
    public ReservationResDto reserve(ReservationReqDto reservation) throws IOException {
        Long userId = UserService.getLogInUser().getId();
        reservation.setUserId(userId);
        if(!photographerRepository.existsById(reservation.getPhotographerId())){
            throw new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND);
        }

        if (reservationRepository.existsByPhotographerIdAndReservedAt(userId, reservation.getDate())) {
            throw new CustomException(ErrorCode.RESERVATION_CANNOT);
        }

        Reservation savedReservation = Reservation.builder()
                .photographer(Photographer.builder().id(reservation.getPhotographerId()).build())
                .user(User.builder().id(reservation.getUserId()).build())
                .receiptId(reservation.getReceiptId())
                .price(reservation.getPrice())
                .categoryName(reservation.getCategoryName())
                .options(reservation.getOptions())
                .email(reservation.getEmail())
                .place(reservation.getAddress() + " " + reservation.getDetailAddress())
                .reservedAt(reservation.getDate())
                .reservedTime(Time.valueOf(reservation.getTime()))
                .createdAt(LocalDateTime.now())
                .build();

        Reservation entity = reservationRepository.save(savedReservation);

        User photographer = userRepository.findById(reservation.getPhotographerId()).get();
        notificationService.sendDataMessageTo(NotificationDTO.builder()
                .requestId(userId)
                .registrationToken(photographer.getFcmToken())
                .content(entity.getReservedAt() + "??? ?????????????????????.")
                .build());

        return new ReservationResDto().of(entity, photographer.getName(), photographer.getPhoneNumber());
    }

    /**
     * ?????? ??? ??????????????? ??????(????????? ??????, ????????????, ????????????) ??????
     *
     * @param photographerId
     * @return PhotographerInfoDto
     */
    public PhotographerReservationDto getPhotographerInfo(Long photographerId){
        photographerRepository.findById(photographerId)
                .orElseThrow(() -> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        // ??????????????? ?????? ??? ?????? ??? ??? ?????? ???
        List<Date> findDates =

                reservationRepository
                        .findReservedAtByPhotographerIdAndReservedAt(photographerId, Date.valueOf(LocalDate.now()));

        // ????????????
        List<CategoriesQdslDto> findCategories = photographerNCategoriesRepository.findCategoriesByPhotographerId(photographerId);
        Map<Long, CategoriesInfoResDto> categories = new HashMap<>();

        for(CategoriesQdslDto category: findCategories){
            if(categories.containsKey(category.getId())){   // ???????????? id??? ?????? ?????? ????????? ???
                List<CategoryDetailDto> list = categories.get(category.getId()).getDetails();
                list.add(CategoryDetailDto.builder().price(category.getPrice()).options(category.getDescription()).build());

                categories.get(category.getId()).setDetails(list);
            } else {    // ??????????????? ???????????? ????????? ???
                List<CategoryDetailDto> list = new ArrayList<>(){{
                    add(CategoryDetailDto.builder().price(category.getPrice()).options(category.getDescription()).build());
                }};

                categories.put(category.getId(), CategoriesInfoResDto.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .details(list)
                        .build());
            }
        }
        List<CategoriesInfoResDto> list = new ArrayList<>(categories.values());

        // ??????
        List<PlacesForListDto> places = new ArrayList<>();
        for(Places place : photographerNPlacesRepository.findPlacesByPhotographer(photographerId)){
            PlacesForListDto dto = PlacesForListDto.builder()
                    .place(place.getFirst() + " " + place.getSecond())
                    .build();
            places.add(dto);
        }

        return PhotographerReservationDto.builder()
                .days(findDates)
                .categories(list)
                .places(places)
                .build();
    }

    /**
     * ?????? ?????? ??????
     *
     * @param statusDto
     */
    @Transactional
    public void changeStatus(ReservationStatusDto statusDto) throws IOException {
        statusDto.setUserId(UserService.getLogInUser().getId());

        Reservation reservation = reservationRepository.findById(statusDto.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // ???????????? ????????? ?????? ??????
        if(reservation.getUser().getId() != statusDto.getUserId()
                && reservation.getPhotographer().getId() != statusDto.getUserId()){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if(reservation.getStatus() == ReservationStatus.????????????){
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        reservation.updateStatus(statusDto.getStatus());
        reservationRepository.save(reservation);

        // FCM ??????
        if(statusDto.getStatus() == ReservationStatus.????????????){
            notificationService.sendDataMessageTo(NotificationDTO.builder()
                    .requestId(statusDto.getUserId())
                    .registrationToken(reservation.getUser().getFcmToken())
                    .content(reservation.getReservedAt() + "??? ????????? ?????????????????????.")
                    .build());
        }
    }

    /**
     * ?????? ?????? ?????? ??????
     *
     * @return List<ReservationPhotographerDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationListDto> findPhotographerReservation() {
        User user = UserService.getLogInUser();

        log.info("?????? ?????? ?????? ?????? ??????");
        if (!user.getRole().equals(Role.PHOTOGRAPHER)) {
            throw new CustomException(ErrorCode.FAIL_AUTHORIZATION);
        }
        log.info("Role ?????? ??????");

        List<Reservation> reservationList =
                reservationRepository.findByPhotographerIdOrderByCreatedAtDesc(user.getId());
        log.info("?????? ?????? ?????? ??????");

        List<ReservationListDto> reservationPhotographerList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            ReservationListDto reservationPhotographer = new ReservationListDto();
            User reservationUser = reservation.getUser();
            Review review = reservation.getReview();
            Long reviewId;
            Boolean isReviewed = false;
            if (review == null){
                reviewId = null;
            } else {
                reviewId = review.getId();
                isReviewed = true;
            }
            reservationPhotographerList.add(
                    reservationPhotographer.of(reservation, reservationUser.getName(), reservationUser.getPhoneNumber(), reviewId, isReviewed)
            );
        }
        return reservationPhotographerList;
    }

    /**
     * ?????? ?????? ?????? ??????
     *
     * @return List<ReservationListDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationListDto> findUserReservation() {
        Long userId = UserService.getLogInUser().getId();

        log.info("?????? ?????? ?????? ??????");
        List<Reservation> reservationList =
                reservationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<ReservationListDto> reservationPhotographerList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            ReservationListDto reservationPhotographer = new ReservationListDto();
            User reservationUser = reservation.getPhotographer().getUser();
            Review review = reservation.getReview();
            Long reviewId;
            Boolean isReviewed = false;
            if (review == null){
                reviewId = null;
            } else {
                reviewId = review.getId();
                isReviewed = true;
            }
            reservationPhotographerList.add(
                    reservationPhotographer.of(reservation, reservationUser.getName(), reservationUser.getPhoneNumber(), reviewId, isReviewed)
            );
        }
        return reservationPhotographerList;
    }

    /***
     * ??????????????? ?????? ?????????
     * @param reservationId ???????????????
     * @param reviewPostDto ??????????????? ?????? dto
     * @throws IOException ????????? ?????? ??? ????????? ??????
     */

    public void addReview(Long reservationId, ReviewPostDto reviewPostDto) throws Exception {
        User user = UserService.getLogInUser();

        String fileName = s3UploaderService.upload(reviewPostDto.getImage());

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()->new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        Photographer photographer = reservation.getPhotographer();

        // ?????? ????????? ?????? 2??? ????????? ?????? ?????? ?????????
        if (reviewRepository.findByReservation(reservation).isPresent()) {
            throw new CustomException(ErrorCode.REVIEW_EXISTED);
        }

        String keywords = analyzeService.analyzeEntitiesText(reviewPostDto.getContent());
        log.info("-------------------------keywords : {}", keywords);

        Review review = Review.builder()
                .content(reviewPostDto.getContent())
                .score(reviewPostDto.getScore())
                .PhotoUrl(fileName)
                .user(user)
                .createdAt(LocalDateTime.now())
                .photographer(photographer)
                .reservation(reservation)
                .keywords(keywords)
                .build();

        reviewRepository.save(review);
    }

    /***
     * ?????? ????????? ?????? ????????? ?????? ???????????? ?????????
     * @param photographerId ??????id
     * @return reviewResDto ???????????????
     */
    @Transactional
    public List<ReviewResDto> showReviewList(Long photographerId){
        User user = UserService.getLogInUser();

        Photographer photographer = photographerRepository.findById(photographerId)
                .orElseThrow(()-> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        List<ReviewResDto> reviewResDtoList = new ArrayList<>();

        List<Review> ReviewList = reviewRepository.findAllByPhotographer(photographer);

        for(Review review : ReviewList){
            boolean isMe = review.getUser() == user;
            reviewResDtoList.add(new ReviewResDto().of(review,isMe));
        }
        return reviewResDtoList;
    }

    /***
     * ?????????????????? ?????? ????????? ??????
     * ????????? ????????? ?????? ??????
     * @param reviewId ???????????????
     */
    public void deleteReview(Long reviewId){
        User user = UserService.getLogInUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(review.getUser().getId() == user.getId()){
            reviewRepository.deleteById(reviewId);
        }
        throw new CustomException(ErrorCode.FAIL_AUTHORIZATION);
    }

    /**
     * ?????? ??????
     *
     * @param reservationId
     * @throws IOException
     */
    @Transactional
    public void changeCancelStatus(Long reservationId) throws IOException {
        Long userId = UserService.getLogInUser().getId();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        log.info("?????? ?????? ??????");

        // ???????????? ????????? ?????? ??????
        if(reservation.getUser().getId() != userId
                && reservation.getPhotographer().getId() != userId){
            log.info("?????? ?????? ????????? ????????? ??????");
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (!(reservation.getStatus() == ReservationStatus.???????????????
                || reservation.getStatus() == ReservationStatus.????????????)) {
            log.info("????????? ????????? ??? ??????");
            throw new CustomException(ErrorCode.RESERVATION_NOT_CANCEL);
        }

        String token = "", name = "";
        User user = reservation.getUser();  // ????????? ??????
        User photographer = reservation.getPhotographer().getUser();    // ????????? ????????????
        if(user.getId() == userId){    // ????????? ????????? ????????? ??????
            token = photographer.getFcmToken();  // ?????????????????? ??????
            name = user.getName();     // ?????????????????? ??????
        } else {    // ??????????????? ????????? ??????
            token = user.getFcmToken();    // ????????? ???????????? ??????
            name = photographer.getName(); // ???????????? ???????????? ??????
        }

        cancelPay(reservation.getReceiptId(), name);

        reservation.updateStatus(ReservationStatus.????????????);
        log.info("?????? ?????? : {}", reservation.getStatus());

        reservationRepository.save(reservation);

        // FCM ??????
        notificationService.sendDataMessageTo(NotificationDTO.builder()
                .requestId(userId)
                .registrationToken(token)
                .content(reservation.getReservedAt() + "??? ????????? ?????????????????????.")
                .build());
    }

    /**
     * ????????????
     *
     * @param receiptId ?????? ????????? ??????
     */
    public void cancelPay(String receiptId, String userName){
        try {
            Bootpay bootpay = new Bootpay(restApiKey, privateKey);
            HashMap<String, Object> token = bootpay.getAccessToken();
            if(token.get("error_code") != null) { //failed
                return;
            }
            Cancel cancel = new Cancel();
            cancel.receiptId = receiptId;
            cancel.cancelUsername = userName;
            cancel.cancelMessage = "????????? ?????? ??????";

            HashMap<String, Object> res = bootpay.receiptCancel(cancel);
            if(res.get("error_code") == null) { //success
                log.info("receiptCancel success: " + res);
            } else {
                log.error("receiptCancel false: " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * ?????? ??????
     * @param reviewId
     * @return ?????? ?????????
     */
    public ReviewDetailDto reviewDetail(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        return new ReviewDetailDto().of(review);
    }
}
