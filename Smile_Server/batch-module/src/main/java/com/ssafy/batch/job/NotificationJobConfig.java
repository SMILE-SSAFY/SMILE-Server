package com.ssafy.batch.job;

import com.ssafy.batch.dto.NotificationDTO;
import com.ssafy.batch.service.NotificationService;
import com.ssafy.core.code.ReservationStatus;
import com.ssafy.core.entity.Reservation;
import com.ssafy.core.entity.User;
import com.ssafy.core.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * fcm 알림 전송하기 위한 Job
 *
 * @author 서재건
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationJobConfig {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    public final ReservationRepository reservationRepository;
    public final NotificationService notificationService;

    /**
     * Job 설정
     *
     * @param notificationStep
     * @return
     */
    @Bean
    public Job NotificationJob(Step notificationStep) {

        return jobBuilderFactory.get("notificationJob")
                .incrementer(new RunIdIncrementer())
                .start(notificationStep)
                .build();
    }

    /**
     * Step 설정
     *
     * @param reservationReader
     * @param reservationWriter
     * @return
     */
    @Bean
    @JobScope
    public Step notificationStep(ItemReader reservationReader,
                         ItemWriter reservationWriter) {
        return stepBuilderFactory.get("notificationStep")
                .<Reservation, Reservation>chunk(10)
                .reader(reservationReader)
                .writer(reservationWriter)
                .build();
    }

    /**
     * db 조회하는 Reader
     *
     * @return
     */
    @Bean
    @StepScope
    public RepositoryItemReader<Reservation> reservationReader() {
        return new RepositoryItemReaderBuilder<Reservation>()
                .name("reservationReader")
                .repository(reservationRepository)
                .methodName("findByReservedAtAndStatusNot")
                .pageSize(10)
                .arguments(Date.valueOf(LocalDate.now().plusDays(1)), ReservationStatus.예약취소)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    /**
     * 조회된 값을 처리하는 Writer
     * @return
     */
    @Bean
    @StepScope
    public ItemWriter<Reservation> reservationWriter() {
        return new ItemWriter<Reservation>() {
            @Override
            public void write(List<? extends Reservation> reservationList) throws Exception {
                for (Reservation reservation : reservationList) {
                    String content;
                    if (reservation.getStatus().equals(ReservationStatus.예약확정전)) {
                        content = "의 예약이 확정되지 않았습니다. 확인해주세용 ><";
                    } else {
                        content = "에 예약이 있습니다.";
                    }
                    User user = reservation.getUser();
                    notificationService.sendDataMessageTo(NotificationDTO.builder()
                                    .requestId(user.getId())
                                    .registrationToken(user.getFcmToken())
                                    .content(reservation.getReservedAt() + "에 예약이 있습니다.")
                                    .build());
                }
            }
        };
    }

}

