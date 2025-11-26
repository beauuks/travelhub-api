package com.travelhub.service;

import com.travelhub.dto.BookingRequest;
import com.travelhub.model.Booking;
import com.travelhub.model.Hotel;
import com.travelhub.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private HotelService hotelService;
    
    @InjectMocks
    private BookingService bookingService;
    
    private Hotel testHotel;
    private BookingRequest bookingRequest;
    
    @BeforeEach
    void setUp() {
        testHotel = new Hotel("Test Hotel", "Bangkok", "Thailand", 
                             4, BigDecimal.valueOf(150.0), 10);
        testHotel.setId(1L);
        
        bookingRequest = new BookingRequest();
        bookingRequest.setHotelId(1L);
        bookingRequest.setCustomerEmail("test@example.com");
        bookingRequest.setCustomerName("Test User");
        bookingRequest.setCheckInDate(LocalDate.of(2024, 12, 15));
        bookingRequest.setCheckOutDate(LocalDate.of(2024, 12, 18)); // 3 nights
        bookingRequest.setNumberOfGuests(2);
    }
    
    @Test
    void createBooking_WithValidRequest_ShouldCreateBooking() {
        // Given
        when(hotelService.findById(1L)).thenReturn(Optional.of(testHotel));
        when(bookingRepository.existsByBookingReference(anyString())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        
        // When
        Booking result = bookingService.createBooking(bookingRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getCustomerEmail());
        assertEquals("Test User", result.getCustomerName());
        assertEquals(BigDecimal.valueOf(450.0), result.getTotalAmount()); // 3 nights * 150
        assertEquals(Booking.BookingStatus.PENDING, result.getStatus());
        
        // Verify hotel availability was updated
        assertEquals(8, testHotel.getAvailableRooms()); // 10 - 2 = 8
        
        verify(hotelService).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }
    
    @Test
    void createBooking_WithNonExistentHotel_ShouldThrowException() {
        // Given
        when(hotelService.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(bookingRequest));
        
        assertEquals("Hotel not found", exception.getMessage());
        verify(hotelService).findById(1L);
        verify(bookingRepository, never()).save(any());
    }
    
    @Test
    void createBooking_WithInsufficientRooms_ShouldThrowException() {
        // Given
        testHotel.setAvailableRooms(1); // Less than requested guests (2)
        when(hotelService.findById(1L)).thenReturn(Optional.of(testHotel));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(bookingRequest));
        
        assertEquals("Not enough rooms available", exception.getMessage());
        verify(hotelService).findById(1L);
        verify(bookingRepository, never()).save(any());
    }
}