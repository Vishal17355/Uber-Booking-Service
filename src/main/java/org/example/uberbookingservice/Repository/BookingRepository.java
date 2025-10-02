package org.example.uberbookingservice.Repository;

import org.example.uberprojectentityservice.Models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BookingRepository extends JpaRepository<Booking, Long> {


}
