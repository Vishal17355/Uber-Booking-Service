package org.example.uberbookingservice.Services;

import org.example.uberbookingservice.DTO.CreateBookingDto;
import org.example.uberbookingservice.DTO.CreateBookingResponseDto;
import org.example.uberbookingservice.DTO.DriverLocationDto;
import org.example.uberbookingservice.DTO.NearbyDriverRequestDto;
import org.example.uberbookingservice.Repository.BookingRepository;
import org.example.uberbookingservice.Repository.PassengerRepository;
import org.example.uberprojectentityservice.Models.Booking;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Passenger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Driver;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    public final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    private static final String LOCATION_SERVICE = "http://localhost:7070";

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        // Fetch passenger safely
        Passenger passenger = passengerRepository.findById(bookingDetails.getPassengerId())
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found with id: " + bookingDetails.getPassengerId()
                ));

        // Create and save booking
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.INITIATED)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger)
                .build();

        Booking newBooking = bookingRepository.save(booking);

        // Fetch nearby drivers from location service
        NearbyDriverRequestDto request = NearbyDriverRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(
                LOCATION_SERVICE + "/api/location/nearby/drivers",
                request,
                DriverLocationDto[].class
        );

        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null && result.getBody().length > 0) {
            List<DriverLocationDto> driverLocationDtoList = Arrays.asList(result.getBody());
            driverLocationDtoList.forEach(driverLocationDto ->
                    System.out.println("DriverId: " + driverLocationDto.getDriverId()
                            + ", lat: " + driverLocationDto.getLatitude()
                            + ", long: " + driverLocationDto.getLongitude())
            );
        }

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .driver(Optional.ofNullable(newBooking.getDriver()))  // safer
                .build();
    }
}