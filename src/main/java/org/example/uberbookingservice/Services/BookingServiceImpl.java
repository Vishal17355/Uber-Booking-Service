package org.example.uberbookingservice.Services;

import org.example.uberbookingservice.DTO.CreateBookingDto;
import org.example.uberbookingservice.DTO.CreateBookingResponseDto;
import org.example.uberbookingservice.DTO.DriverLocationDto;
import org.example.uberbookingservice.DTO.NearbyDriverRequestDto;
import org.example.uberbookingservice.Repository.BookingRepository;
import org.example.uberbookingservice.Repository.PassengerRepository;
import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberprojectentityservice.Models.Booking;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Passenger;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final LocationServiceApi locationServiceApi;

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository,
                              LocationServiceApi locationServiceApi) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.locationServiceApi = locationServiceApi;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        // Fetch passenger
        Passenger passenger = passengerRepository.findById(bookingDetails.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + bookingDetails.getPassengerId()));

        // Create booking
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.INITIATED)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger)
                .build();

        Booking newBooking = bookingRepository.save(booking);

        // Call location service asynchronously
        NearbyDriverRequestDto request = NearbyDriverRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        processNearByDriverAsync(request);

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .driver(Optional.ofNullable(newBooking.getDriver()))
                .build();
    }

    /**
     * ✅ Asynchronous call to Location Service using Retrofit
     */
    private void processNearByDriverAsync(NearbyDriverRequestDto requestDto) {
        Call<DriverLocationDto[]> call = locationServiceApi.getNearByDriver(requestDto);

        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                System.out.println("✅ Response received from Location Service");

                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("Number of drivers: " + response.body().length);

                    List<DriverLocationDto> driverLocationDtoList = Arrays.asList(response.body());
                    driverLocationDtoList.forEach(driverLocationDto ->
                            System.out.println("DriverId: " + driverLocationDto.getDriverId()
                                    + ", lat: " + driverLocationDto.getLatitude()
                                    + ", long: " + driverLocationDto.getLongitude())
                    );
                } else {
                    System.out.println("❌ Request failed: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            System.out.println("Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                System.out.println("🚨 Retrofit call failed: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }
}
