package com.travelhub.controller;

import com.travelhub.model.Hotel;
import com.travelhub.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private HotelService hotelService;
    
    @Test
    void getAllHotels_ShouldReturnHotelsList() throws Exception {
        Hotel hotel = new Hotel("Test Hotel", "Bangkok", "Thailand",
                               4, BigDecimal.valueOf(150.0), 10);
        hotel.setId(1L);

        when(hotelService.findAvailableHotels()).thenReturn(Arrays.asList(hotel));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Hotel"))
                .andExpect(jsonPath("$[0].city").value("Bangkok"));

        verify(hotelService).findAvailableHotels();
    }
    
    @Test
    void getHotel_WhenExists_ShouldReturnHotel() throws Exception {
        Hotel hotel = new Hotel("Test Hotel", "Bangkok", "Thailand",
                               4, BigDecimal.valueOf(150.0), 10);
        hotel.setId(1L);

        when(hotelService.findById(1L)).thenReturn(Optional.of(hotel));

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Hotel"));

        verify(hotelService).findById(1L);
    }
    
    @Test
    void getHotel_WhenNotExists_ShouldReturn404() throws Exception {
        when(hotelService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isNotFound());

        verify(hotelService).findById(1L);
    }
    
    @Test
    void searchHotels_ShouldReturnFilteredHotels() throws Exception {
        Hotel hotel = new Hotel("Bangkok Hotel", "Bangkok", "Thailand",
                               4, BigDecimal.valueOf(150.0), 10);
        hotel.setId(1L);

        when(hotelService.findByCity("Bangkok")).thenReturn(Arrays.asList(hotel));

        mockMvc.perform(get("/api/hotels/search?city=Bangkok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].city").value("Bangkok"));

        verify(hotelService).findByCity("Bangkok");
    }
}