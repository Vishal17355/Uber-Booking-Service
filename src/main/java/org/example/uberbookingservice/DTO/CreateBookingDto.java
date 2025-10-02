package org.example.uberbookingservice.DTO;

import lombok.*;
import org.example.uberprojectentityservice.Models.ExactLocation;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreateBookingDto {
    private long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
}
