package com.travelhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelhub.dto.BookingRequest;
import com.travelhub.model.Booking;
import com.travelhub.model.Hotel;
import com.travelhub.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequest validBookingRequest;
    private Booking testBooking;
    private Hotel testHotel;

    @BeforeEach
    void setUp() {
        // Setup test hotel
        testHotel = new Hotel("Test Hotel", "Bangkok", "Thailand",
                4, BigDecimal.valueOf(150.0), 10);
        testHotel.setId(1L);

        // Setup valid booking request
        validBookingRequest = new BookingRequest();
        validBookingRequest.setHotelId(1L);
        validBookingRequest.setCustomerEmail("test@example.com");
        validBookingRequest.setCustomerName("Test User");
        validBookingRequest.setCheckInDate(LocalDate.now().plusDays(5));
        validBookingRequest.setCheckOutDate(LocalDate.now().plusDays(8));
        validBookingRequest.setNumberOfGuests(2);

        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setBookingReference("TH-12345678");       
        testBooking.setCustomerEmail("test@example.com");     
        testBooking.setCustomerName("Test User");             
        testBooking.setNumberOfGuests(2);                     
        testBooking.setTotalAmount(BigDecimal.valueOf(450.0));
        testBooking.setCreatedAt(LocalDateTime.now());
        testBooking.setStatus(Booking.BookingStatus.PENDING);
    }

    @Test
    void createBooking_WithValidRequest_ShouldReturnBooking() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(testBooking);

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookingReference").value("TH-12345678"))
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.customerName").value("Test User"))
                .andExpect(jsonPath("$.numberOfGuests").value(2))
                .andExpect(jsonPath("$.totalAmount").value(450.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(bookingService).createBooking(any(BookingRequest.class));
    }
    @Test
    void createBooking_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest invalidRequest = new BookingRequest();
        invalidRequest.setHotelId(1L);
        invalidRequest.setCustomerEmail("invalid-email"); // Invalid email format
        invalidRequest.setCustomerName("Test User");
        invalidRequest.setCheckInDate(LocalDate.of(2024, 12, 15));
        invalidRequest.setCheckOutDate(LocalDate.of(2024, 12, 18));
        invalidRequest.setNumberOfGuests(2);

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(any());
    }
    @Test
    void createBooking_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest invalidRequest = new BookingRequest();
        // Missing required fields
        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any());
    }

    @Test
    void createBooking_WithZeroGuests_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest invalidRequest = new BookingRequest();
        invalidRequest.setHotelId(1L);
        invalidRequest.setCustomerEmail("test@example.com");
        invalidRequest.setCustomerName("Test User");
        invalidRequest.setCheckInDate(LocalDate.of(2024, 12, 15));
        invalidRequest.setCheckOutDate(LocalDate.of(2024, 12, 18));
        invalidRequest.setNumberOfGuests(0); // Invalid: 0 guests

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any());
    }

    @Test
    void createBooking_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingRequest.class)))
            .thenThrow(new RuntimeException("Hotel not found"));

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBookingRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService).createBooking(any(BookingRequest.class));
    }

    @Test
    void getBookings_WithValidEmail_ShouldReturnBookingsList() throws Exception {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.findBookingsByEmail("test@example.com")).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$[0].bookingReference").value("TH-12345678"));

        verify(bookingService).findBookingsByEmail("test@example.com");
    }

    @Test
    void getBookings_WithNonExistentEmail_ShouldReturnEmptyList() throws Exception {
        // Given
        when(bookingService.findBookingsByEmail("nonexistent@example.com"))
            .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/bookings")
                .param("email", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bookingService).findBookingsByEmail("nonexistent@example.com");
    }

    @Test
    void getBookings_WithMissingEmailParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsByEmail(any());
    }
}