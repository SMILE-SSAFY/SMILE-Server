package com.ssafy.api.service;

import com.ssafy.api.dto.Photographer.CategoriesReqDto;
import com.ssafy.api.dto.Photographer.PhotographerForListDto;
import com.ssafy.api.dto.Photographer.PhotographerHeartDto;
import com.ssafy.api.dto.Photographer.PhotographerNearDto;
import com.ssafy.api.dto.Photographer.PhotographerReqDto;
import com.ssafy.api.dto.Photographer.PhotographerResDto;
import com.ssafy.api.dto.Photographer.PhotographerUpdateReqDto;
import com.ssafy.api.dto.Photographer.PlacesReqDto;
import com.ssafy.api.dto.article.PhotographerInfoDto;
import com.ssafy.core.code.Role;
import com.ssafy.core.dto.PhotographerQdslDto;
import com.ssafy.core.dto.ReviewQdslDto;
import com.ssafy.core.entity.Article;
import com.ssafy.core.entity.Categories;
import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.PhotographerHeart;
import com.ssafy.core.entity.PhotographerNCategories;
import com.ssafy.core.entity.PhotographerNPlaces;
import com.ssafy.core.entity.Places;
import com.ssafy.core.entity.User;
import com.ssafy.core.exception.CustomException;
import com.ssafy.core.exception.ErrorCode;
import com.ssafy.core.repository.CategoriesRepository;
import com.ssafy.core.repository.review.ReviewRepository;
import com.ssafy.core.repository.UserRepository;
import com.ssafy.core.repository.article.ArticleRepository;
import com.ssafy.core.repository.photographer.PhotographerHeartRepository;
import com.ssafy.core.repository.photographer.PhotographerNCategoriesRepository;
import com.ssafy.core.repository.photographer.PhotographerNPlacesRepository;
import com.ssafy.core.repository.photographer.PhotographerRepository;
import com.ssafy.core.repository.places.PlacesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ?????? ????????? ?????? ?????????
 *
 * @author ?????????
 * @author ?????????
 * @author ?????????
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PhotographerService {
    private final ReviewRepository reviewRepository;
    private final PhotographerNCategoriesRepository photographerNCategoriesRepository;
    private final PhotographerNPlacesRepository photographerNPlacesRepository;
    private final PhotographerRepository photographerRepository;
    private final UserRepository userRepository;
    private final S3UploaderService s3UploaderService;
    private final PhotographerHeartRepository photographerHeartRepository;
    private final CategoriesRepository categoriesRepository;
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;
    private final PlacesRepository placesRepository;

    /**
     * ?????? ??????
     *
     * @param multipartFile ????????? ?????????
     * @param photographer
     * @throws USER_NOT_FOUND ????????? ?????? ??? ?????? ??? ??????
     * @throws IOException
     */
    public void addPhotographer(MultipartFile multipartFile, PhotographerReqDto photographer) throws IOException{
        photographer.setPhotographerId(UserService.getLogInUser().getId());
        User user = userRepository.findById(photographer.getPhotographerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!multipartFile.isEmpty()) {
            // ?????? ?????????
            String fileName = s3UploaderService.upload(multipartFile);
            photographer.setProfileImg(fileName);
        }

        // ???????????? ??????
        List<PhotographerNPlaces> places = new ArrayList<>();
        for(PlacesReqDto place : photographer.getPlaces()){
            places.add(PhotographerNPlaces.builder()
                    .photographer(Photographer.builder().id(user.getId()).build())
                    .places(Places.builder().id(place.getPlaceId()).build())
                    .build()
            );
        }

        int minPrice = Integer.MAX_VALUE;
        // ???????????? ??????
        List<PhotographerNCategories> categories = new ArrayList<>();
        for(CategoriesReqDto category : photographer.getCategories()){
            categories.add(PhotographerNCategories.builder()
                    .photographer(Photographer.builder().id(user.getId()).build())
                    .category(Categories.builder().id(category.getCategoryId()).build())
                    .price(category.getPrice())
                    .description(category.getDescription())
                    .build()
            );
            minPrice = Math.min(minPrice, category.getPrice());
        }

        Photographer savedPhotographer = Photographer.builder()
                .user(user)
                .profileImg(photographer.getProfileImg())
                .introduction(photographer.getIntroduction())
                .bank(photographer.getBank())
                .account(photographer.getAccount())
                .places(places)
                .categories(categories)
                .minPrice(minPrice)
                .build();

        photographerRepository.save(savedPhotographer);

        // ?????? ?????? photographer??? ??????
        user.updateRole(Role.PHOTOGRAPHER);
        userRepository.save(user);
    }

    /**
     * ?????? ????????? ??????
     *
     * @return ?????? ????????? ??????
     * @throws PHOTOGRAPHER_NOT_FOUND ??????????????? ?????? ??? ?????? ??? ??????
     */
    public PhotographerResDto getPhotographer(){
        Long userIdx = UserService.getLogInUser().getId();
        Photographer photographer = photographerRepository.findById(userIdx)
                .orElseThrow(() -> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));
        return new PhotographerResDto().of(photographer);
    }

    /**
     * ?????? ????????? ??????
     *
     * @param file ????????? ??????
     * @param photographer
     * @return ????????? ?????? ????????? ??????
     * @throws PHOTOGRAPHER_NOT_FOUND ??????????????? ?????? ??? ?????? ??? ??????
     * @throws IOException
     */
    public PhotographerResDto changePhotographer(MultipartFile file, PhotographerUpdateReqDto photographer) throws IOException {
        photographer.setPhotographerId(UserService.getLogInUser().getId());
        Photographer findPhotographer = photographerRepository.findById(photographer.getPhotographerId())
                .orElseThrow(() -> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        log.info(photographer.getProfileImg());
        log.info(findPhotographer.getProfileImg());

        if(!photographer.isDeleted()){  // ???????????? ???????????? ????????? ???
            if(file.isEmpty()){ // ???????????? ?????? ???
                photographer.setProfileImg(findPhotographer.getProfileImg());
            } else {    // ???????????? ?????? ??????????????? ???(?????? ????????? null)
                String fileName = s3UploaderService.upload(file);
                photographer.setProfileImg(fileName);
            }
        } else {
            // ?????? ????????? ??????
            s3UploaderService.deleteFile(findPhotographer.getProfileImg().trim());
            if(!file.isEmpty()) {    // ???????????? ??????????????? ???
                String fileName = s3UploaderService.upload(file);
                photographer.setProfileImg(fileName);
            }
        }

        // ???????????? ??????
        for(PhotographerNPlaces places : findPhotographer.getPlaces()){
            photographerNPlacesRepository.deleteById(places.getId());
        }
        List<PhotographerNPlaces> places = new ArrayList<>();
        for(PlacesReqDto place : photographer.getPlaces()){
            places.add(PhotographerNPlaces.builder()
                    .photographer(Photographer.builder().id(photographer.getPhotographerId()).build())
                    .places(Places.builder().id(place.getPlaceId()).build())
                    .build()
            );
        }

        // ???????????? ??????
        for(PhotographerNCategories categories : findPhotographer.getCategories()){
            photographerNCategoriesRepository.deleteById(categories.getId());
        }
        List<PhotographerNCategories> categories = new ArrayList<>();
        for(CategoriesReqDto category : photographer.getCategories()){
            categories.add(PhotographerNCategories.builder()
                    .photographer(Photographer.builder().id(photographer.getPhotographerId()).build())
                    .category(Categories.builder().id(category.getCategoryId()).build())
                    .price(category.getPrice())
                    .description(category.getDescription())
                    .build()
            );
        }

        findPhotographer.updateProfileImg(photographer.getProfileImg());
        findPhotographer.updateBank(photographer.getBank());
        findPhotographer.updateAccount(photographer.getAccount());
        findPhotographer.updateIntroduction(photographer.getIntroduction());
        findPhotographer.updatePlaces(places);
        findPhotographer.updateCategories(categories);

        return new PhotographerResDto().of(photographerRepository.save(findPhotographer));
    }

    /**
     * ?????? ?????? ????????? ??????
     *
     * @throws PHOTOGRAPHER_NOT_FOUND ??????????????? ?????? ??? ?????? ??? ??????
     */
    public void removePhotographer(){
        Long userId = UserService.getLogInUser().getId();
        Photographer findPhotographer = photographerRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        // ????????? ??????
        if(findPhotographer.getProfileImg() != null) {
            s3UploaderService.deleteFile(findPhotographer.getProfileImg().trim());
        }
        photographerRepository.delete(findPhotographer);

        List<Article> articleList = articleRepository.findByUserIdOrderByIdDesc(userId);
        for (Article article : articleList) {
            String photoUriList = article.getPhotoUrls();
            photoUriList = photoUriList.replace("[","").replace("]","");
            List<String> photoUrls = new ArrayList<>(Arrays.asList(photoUriList.split(",")));
            photoUrls.forEach(str -> s3UploaderService.deleteFile(str.trim()));
            articleRepository.delete(article);
        }

        // ?????? ????????? ??????
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateRole(Role.USER);
        userRepository.save(user);
    }

    /**
     * categoryId??? ?????? ??????
     *
     * @param categoryName
     * @return List<PhotographerForListDto>
     * @throws CATEGORY_NOT_FOUND ?????? ??????????????? ?????? ??? ??????
     * @throws PHOTOGRAPHER_NOT_FOUND ??????????????? ?????? ??? ?????? ??? ??????
     */
    public List<PhotographerForListDto> getPhotographerListByCategory(String categoryName) {
        User user = UserService.getLogInUser();
        List<Long> categoryIdList = categoriesRepository.findAllIdByNameContaining(categoryName);
        if (categoryIdList.isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        log.info("?????? ???????????? ??????");

        List<Photographer> photographerList =
                photographerNCategoriesRepository.findByCategoryId(categoryIdList);
        log.info("??????????????? ?????? ??????");

        List<PhotographerForListDto> photographerForList = new ArrayList<>();
        for (Photographer photographer : photographerList) {
            PhotographerQdslDto dto = new PhotographerQdslDto();
            dto.setPhotographer(photographer);      // ????????????
            dto.setHeart(photographerHeartRepository.countByPhotographer(photographer));    // ????????? ????????? ???
            ReviewQdslDto review = reviewRepository.findByPhotographerId(photographer.getId());
            dto.setAvgScore(review.getAvgScore());      // ????????? ??????
            dto.setReviewCount(review.getReviewCount());    // ????????? ?????? ???
            dto.setHasHeart(photographerHeartRepository.findByUserAndPhotographer(user, photographer).isPresent()); // ????????? ????????? ??????
            photographerForList.add(new PhotographerForListDto().of(dto));
        }

        return photographerForList;
    }

    /**
     * ?????? ?????? ??????
     *
     * @param address
     * @param criteria
     * @return List<PhotographerForListDto>
     */
    public PhotographerNearDto getPhotographerListByAddresss(String address, String criteria) {
        User user = UserService.getLogInUser();
        String[] addresssList = address.split(" ");

        String placeId = placesRepository.findByAddress(addresssList[0], addresssList[1]);
        log.info("?????? ?????? ????????? placeId : {}", placeId);
        List<Photographer> photographerList = photographerNPlacesRepository.findPhotographerByPlaceId(placeId);
        log.info("?????? ????????? ?????? ?????? : {}", photographerList.size());

        // ??????????????? ????????? ?????? dto ???????????? list ??????
        List<PhotographerForListDto> photographerForList = new ArrayList<>();
        for (Photographer photographer : photographerList) {
            PhotographerQdslDto dto = new PhotographerQdslDto();
            dto.setPhotographer(photographer);      // ????????????
            dto.setHeart(photographerHeartRepository.countByPhotographer(photographer));    // ????????? ????????? ???
            ReviewQdslDto review = reviewRepository.findByPhotographerId(photographer.getId());
            dto.setAvgScore(review.getAvgScore());      // ????????? ??????
            dto.setReviewCount(review.getReviewCount());    // ????????? ?????? ???
            dto.setHasHeart(photographerHeartRepository.findByUserAndPhotographer(user, photographer).isPresent()); // ????????? ????????? ??????
            photographerForList.add(new PhotographerForListDto().of(dto));
        }

        switch (criteria) {
            case "heart":
                photographerForList.sort((a, b) -> Integer.compare(b.getHeart(), a.getHeart()));
                break;
            case "score":
                photographerForList.sort((a, b) -> Double.compare(b.getAvgScore(), a.getAvgScore()));
                break;
            case "review":
                photographerForList.sort((a, b) -> Integer.compare(b.getReviewCount(), a.getReviewCount()));
                break;
            default:
                break;
        }


        String photoUrl = null;
        Optional<Photographer> photographer = photographerRepository.findById(user.getId());
        if (photographer.isPresent()) {     // ???????????? ??????????????? ????????? ????????? ??????
            photoUrl = photographer.get().getProfileImg();
        }

        return new PhotographerNearDto().of(photoUrl, photographerForList);
    }

    /***
     * ???????????? ????????? / ????????? ??????
     *
     * @param photographerId
     * @return ??????????????? id, isHeart boolean
     * @throws PHOTOGRAPHER_NOT_FOUND
     * @throws USER_NOT_FOUND
     */
    public PhotographerHeartDto addHeartPhotographer(Long photographerId){
        Photographer photographer = photographerRepository.findById(photographerId)
                .orElseThrow(()-> new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        User user = UserService.getLogInUser();
        boolean isHeart = isHearted(user, photographer);

        // ???????????? ?????? ??? -> ????????? ??????
        if(!isHeart){
            photographerHeartRepository.save(new PhotographerHeart(user, photographer));
        } else {
            photographerHeartRepository.deleteByUserAndPhotographer(user, photographer);
        }

        // ????????? ?????? ?????? ?????????, ???????????? ?????????, ???????????? ???????????? ??????
        return PhotographerHeartDto.builder()
                .photographerId(photographerId)
                .isHeart(!isHeart)
                .build();
    }

    /***
     *
     * @param user
     * @param photographer
     * @return user??? photographer??? ???????????? ???????????? ??????
     */
    private Boolean isHearted(User user, Photographer photographer){
        return photographerHeartRepository.findByUserAndPhotographer(user, photographer).isPresent();
    }

    /***
     * ?????? ????????? ?????? ?????? ??????
     * @return List<PhotographerForListDto> ?????? ????????? ?????? ???????????????
     */
    public List<PhotographerForListDto> getPhotographerListByUser() {
        User user = UserService.getLogInUser();

        List<PhotographerHeart> photographerList = photographerHeartRepository.findByUser(user);

        log.info(photographerList.toString());

        if (photographerList.isEmpty()){
            return new ArrayList<>();
        }

        List<PhotographerForListDto> photographerForList = new ArrayList<>();
        for (PhotographerHeart photographerHeart : photographerList){
            Photographer photographer = photographerHeart.getPhotographer();
            ReviewQdslDto review = reviewRepository.findByPhotographerId(photographer.getId());

            photographerForList.add(new PhotographerForListDto().of(PhotographerQdslDto.builder()
                    .photographer(photographer)
                    .heart(photographerHeartRepository.countByPhotographer(photographer))
                    .hasHeart(true)
                    .avgScore(review.getAvgScore())
                    .reviewCount(review.getReviewCount())
                    .build()));
        }

        return photographerForList;
    }

    /***
     * ?????????????????? ????????????
     * @param photographerId ?????? ?????????
     * @return ????????????????????? + ?????? ?????????????????? ?????? article ????????? ????????????
     * @throws PHOTOGRAPHER_NOT_FOUND
     * @throws UsernameNotFoundException ?????? ?????? ???
     */

    @Transactional
    public PhotographerInfoDto getPhotographerInformation(Long photographerId) {
        User logInUser = UserService.getLogInUser();

        Photographer photographer = photographerRepository.findById(photographerId)
                .orElseThrow(()->new CustomException(ErrorCode.PHOTOGRAPHER_NOT_FOUND));

        Boolean isMe = articleService.isMe(logInUser, photographer.getUser());
        Boolean isHeart = this.isHearted(logInUser, photographer);
        Long hearts = photographerHeartRepository.countByPhotographer(photographer);

        // ????????????
        List<String> places = new ArrayList<>();
        for(PhotographerNPlaces place : photographer.getPlaces()){
            places.add(place.getPlaces().getFirst()+" " +place.getPlaces().getSecond());
        }

        // ????????????
        List<String> categories = new ArrayList<>();
        for(PhotographerNCategories category : photographer.getCategories()){
            categories.add(category.getCategory().getName());
        }

        return new PhotographerInfoDto().of(photographer, isMe, isHeart, hearts, places, categories);
    }

}
