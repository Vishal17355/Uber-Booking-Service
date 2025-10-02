package org.example.uberbookingservice.Services;

import org.example.uberbookingservice.DTO.CreateBookingDto;
import org.example.uberbookingservice.DTO.CreateBookingResponseDto;
import org.example.uberprojectentityservice.Models.Booking;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookingService {

     CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails);
}
