package com.travelhub.service;

import com.travelhub.model.Hotel;
import com.travelhub.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }
    
    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }
    
    public List<Hotel> findByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city);
    }
    
    public List<Hotel> findAvailableHotels() {
        return hotelRepository.findByAvailableRoomsGreaterThan(0);
    }
}