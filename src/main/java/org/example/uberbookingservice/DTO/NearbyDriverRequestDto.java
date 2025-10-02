package org.example.uberbookingservice.DTO;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriverRequestDto {

    Double latitude;

    Double longitude;
}
