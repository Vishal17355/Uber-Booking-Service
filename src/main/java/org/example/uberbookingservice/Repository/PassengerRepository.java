package org.example.uberbookingservice.Repository;

import org.example.uberprojectentityservice.Models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long >{

}
