package org.example.uberbookingservice.apis;

import org.example.uberbookingservice.DTO.DriverLocationDto;
import org.example.uberbookingservice.DTO.NearbyDriverRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceApi {
    @POST("/api/location/nearby/drivers")
    Call<DriverLocationDto[]> getNearByDriver(@Body NearbyDriverRequestDto requestDto);
}
