package com.ssafy.core.repository;

import com.ssafy.core.dto.PhotographerQuerydslDto;
import com.ssafy.core.entity.Photographer;

import java.util.List;

/**
 * querydsl 작성할 인터페이스
 *
 * author @서재건
 */
public interface PhotographerNCategoriesRepositoryCustom {

    /**
     * categoryId로 사진 작가 및 사진 작가의 좋아요 상태 조회
     *
     * @param userId
     * @param categoryIdList
     * @return List<PhotographerQuerydslDto>
     */
    List<PhotographerQuerydslDto> findByCategoryId(Long userId, List<Long> categoryIdList);
}
