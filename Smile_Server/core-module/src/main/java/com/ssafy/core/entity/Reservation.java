package com.ssafy.core.entity;

import com.ssafy.core.code.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

/**
 * 예약 관련 Entity
 *
 * @author 김정은
 * @author 신민철
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservation")
@DynamicUpdate
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id")
    private Photographer photographer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ColumnDefault("0")
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.예약확정전;

    private String receiptId;

    private int price;

    private String categoryName;

    private String options;

    private String email;

    // 만나는 장소
    private String place;

    private Date reservedAt;

    private Time reservedTime;

    @Column(name = "reviewed")
    @ColumnDefault("false")
    private boolean isReviewed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Review review;

    /**
     * 예약 상태 변경
     *
     * @param status
     */
    public void updateStatus(ReservationStatus status){
        this.status = status;
    }
}