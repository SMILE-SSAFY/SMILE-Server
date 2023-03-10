package com.ssafy.core.repository.photographer;

import com.ssafy.core.dto.PhotographerIdQdslDto;
import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.Places;

import java.util.List;

/**
 * 사진 작가와 활동 지역 Repository Querydsl custom
 *
 * @author 서재건
 * @author 김정은
 * @author 이지윤
 */
public interface PhotographerNPlacesRepositoryCustom {

    /**
     * 활동지역에 해당하는 사진작가 Id 조회
     *
     * @param addressList
     * @return List<PhotographerIdQdslDto>
     */
    List<PhotographerIdQdslDto> findPhotographerIdByAddress(String[] addressList);

    /**
     * placeId에 해당하는 사진작가 조회
     *
     * @param placeId
     * @return List<Photographer>
     */
    List<Photographer> findPhotographerByPlaceId(String placeId);

    /**
     * 사진작가 별 활동지역 검색
     *
     * @param photographerId
     * @return 활동지역
     */
    List<Places> findPlacesByPhotographer(Long photographerId);
}
