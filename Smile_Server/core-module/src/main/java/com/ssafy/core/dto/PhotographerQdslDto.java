package com.ssafy.core.dto;

import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * querydsl 매핑 dto
 *
 * @author 서재건
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotographerQdslDto {
    private Photographer photographer;
    private double avgScore;
    private Long heart;
    private Long reviewCount;
    private boolean hasHeart;

}
