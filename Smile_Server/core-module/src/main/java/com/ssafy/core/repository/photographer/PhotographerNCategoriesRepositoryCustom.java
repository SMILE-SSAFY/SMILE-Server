package com.ssafy.core.repository.photographer;

import com.ssafy.core.dto.CategoriesQdslDto;
import com.ssafy.core.entity.Photographer;

import java.util.List;

/**
 * querydsl 작성할 인터페이스
 *
 * @author 서재건
 */
public interface PhotographerNCategoriesRepositoryCustom {

    /**
     * categoryId로 사진 작가 조회
     *
     * @param categoryIdList
     * @return List<PhotographerQuerydslDto>
     */
    List<Photographer> findByCategoryId(List<Long> categoryIdList);

    /**
     * photographerId로 카테고리 조회
     *
     * @param photographerId
     * @return List<CategoriesQdslDto>
     */
    List<CategoriesQdslDto> findCategoriesByPhotographerId(Long photographerId);
}
