package org.example.uberbookingservice.DTO;

import lombok.*;
import org.example.uberprojectentityservice.Models.Driver;

import java.util.Optional;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingResponseDto {
    private long bookingId;
    private String bookingStatus;
    private Optional<Driver> driver;
}
