package org.example.uberbookingservice.Services;

import org.example.uberbookingservice.DTO.*;
import org.example.uberbookingservice.Repository.BookingRepository;
import org.example.uberbookingservice.Repository.DriverRepository;
import org.example.uberbookingservice.Repository.PassengerRepository;
import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberprojectentityservice.Models.Booking;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Driver;
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
    private final DriverRepository driverRepository;

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository,
                              LocationServiceApi locationServiceApi,
                              DriverRepository driverRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.locationServiceApi = locationServiceApi;
        this.driverRepository = driverRepository;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        Passenger passenger = passengerRepository.findById(bookingDetails.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + bookingDetails.getPassengerId()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.INITIATED)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger)
                .build();

        Booking newBooking = bookingRepository.save(booking);

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

    @Override
    public UpdateBookingResonseDto updateBooking(UpdateBookingRequestDto bookingRequestDto, Long bookingId) {
        Optional<Driver> driver = driverRepository.findById(bookingRequestDto.getDriverId().get());

        bookingRepository.updateBookingStatusAndDriverById(
                bookingId,
            BookingStatus.SCHEDULED,
                driver.get()
        );

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        return UpdateBookingResonseDto.builder()
                .bookingId(bookingId)
                .status(booking.getBookingStatus())
                .driver(Optional.ofNullable(booking.getDriver()))
                .build();
    }

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
