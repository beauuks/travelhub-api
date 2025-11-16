package com.travelhub.service;

import com.travelhub.dto.BookingRequest;
import com.travelhub.model.Booking;
import com.travelhub.model.Hotel;
import com.travelhub.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private HotelService hotelService;
    
    @Transactional
    public Booking createBooking(BookingRequest request) {
        // find hotel
        Hotel hotel = hotelService.findById(request.getHotelId())
            .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        // check if it's available 
        if (hotel.getAvailableRooms() < request.getNumberOfGuests()) {
            throw new RuntimeException("Not enough rooms available");
        }
        
        // calculate the total amount 
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalAmount = hotel.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        
        // create booking reference
        String bookingReference = generateBookingReference();
        
        //create booking
        Booking booking = new Booking(
            bookingReference,
            request.getCustomerEmail(),
            request.getCustomerName(),
            hotel,
            request.getCheckInDate(),
            request.getCheckOutDate(),
            request.getNumberOfGuests(),
            totalAmount
        );
        
        // update availability of the hotel
        hotel.setAvailableRooms(hotel.getAvailableRooms() - request.getNumberOfGuests());
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> findBookingsByEmail(String email) {
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
    }
    
    private String generateBookingReference() {
        String reference;
        do {
            reference = "TH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (bookingRepository.existsByBookingReference(reference));
        
        return reference;
    }
}