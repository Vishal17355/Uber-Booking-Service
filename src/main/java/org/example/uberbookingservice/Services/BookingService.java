package org.example.uberbookingservice.Services;

import org.example.uberbookingservice.DTO.CreateBookingDto;
import org.example.uberbookingservice.DTO.CreateBookingResponseDto;
import org.example.uberbookingservice.DTO.UpdateBookingRequestDto;
import org.example.uberbookingservice.DTO.UpdateBookingResonseDto;

public interface BookingService {

     CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails);
     UpdateBookingResonseDto updateBooking(UpdateBookingRequestDto bookingRequesteDto, Long bookingId);
}
