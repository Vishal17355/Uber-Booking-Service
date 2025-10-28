package org.example.uberbookingservice.DTO;

import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.*;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Driver;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingResonseDto {

    private Long bookingId;
    private BookingStatus status;
    private Optional<Driver> driver;
}
