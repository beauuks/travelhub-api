package com.travelhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelhub.dto.BookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TravelHubAppTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void fullBookingFlow_ShouldWork() throws Exception {
        // 1. Get available hotels
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    
        // 2. Search hotels by city
        mockMvc.perform(get("/api/hotels/search").param("city", "Bangkok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 3. Create a booking
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setHotelId(1L);
        bookingRequest.setCustomerEmail("integration-test@example.com");
        bookingRequest.setCustomerName("Integration Test User");
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(4));
        bookingRequest.setNumberOfGuests(2);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("integration-test@example.com"))
                .andExpect(jsonPath("$.bookingReference").exists())
                .andExpect(jsonPath("$.totalAmount").exists());

        // 4. Get booking history
        mockMvc.perform(get("/api/bookings").param("email", "integration-test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerEmail").value("integration-test@example.com"));
    }
}