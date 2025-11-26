package com.travelhub.service;

import com.travelhub.model.Hotel;
import com.travelhub.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {
    
    @Mock
    private HotelRepository hotelRepository;
    
    @InjectMocks
    private HotelService hotelService;
    
    private Hotel testHotel;
    
    @BeforeEach
    void setUp() {
        testHotel = new Hotel("Test Hotel", "Bangkok", "Thailand", 
                             4, BigDecimal.valueOf(150.0), 10);
        testHotel.setId(1L);
    }
    
    @Test
    void findById_WhenHotelExists_ShouldReturnHotel() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        
        // When
        Optional<Hotel> result = hotelService.findById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Hotel", result.get().getName());
        verify(hotelRepository).findById(1L);
    }
    
    @Test
    void findById_WhenHotelDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<Hotel> result = hotelService.findById(1L);
        
        // Then
        assertFalse(result.isPresent());
        verify(hotelRepository).findById(1L);
    }
    
    @Test
    void findByCity_ShouldReturnHotelsInCity() {
        // Given
        List<Hotel> expectedHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByCityIgnoreCase("Bangkok")).thenReturn(expectedHotels);
        
        // When
        List<Hotel> result = hotelService.findByCity("Bangkok");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getName());
        verify(hotelRepository).findByCityIgnoreCase("Bangkok");
    }
    
    @Test
    void findAvailableHotels_ShouldReturnOnlyAvailableHotels() {
        // Given
        List<Hotel> expectedHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByAvailableRoomsGreaterThan(0)).thenReturn(expectedHotels);
        
        // When
        List<Hotel> result = hotelService.findAvailableHotels();
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailableRooms() > 0);
        verify(hotelRepository).findByAvailableRoomsGreaterThan(0);
    }
}
