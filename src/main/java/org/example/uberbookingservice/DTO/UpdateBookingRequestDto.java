package org.example.uberbookingservice.DTO;

import lombok.*;
import org.example.uberprojectentityservice.Models.BookingStatus;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingRequestDto {
    private String status;
    private Optional<Long> driverId;
}
