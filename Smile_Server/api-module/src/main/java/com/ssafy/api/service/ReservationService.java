package com.ssafy.api.service;

import com.ssafy.api.dto.Reservation.ReservationDto;
import com.ssafy.core.entity.Photographer;
import com.ssafy.core.entity.Reservation;
import com.ssafy.core.entity.User;
import com.ssafy.core.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public void reserve(ReservationDto reservation){

        Reservation savedReservation = Reservation.builder()
                .photographer(Photographer.builder().id(reservation.getPhotographerId()).build())
                .user(User.builder().id(reservation.getUserId()).build())
                .price(reservation.getPrice())
                .date(reservation.getDate())
                .time(Time.valueOf(reservation.getTime()))
                .build();

        reservationRepository.save(savedReservation);
    }
}
