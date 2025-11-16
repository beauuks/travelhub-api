package com.travelhub.controller;

import com.travelhub.model.Hotel;
import com.travelhub.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    
    @Autowired
    private HotelService hotelService;
    
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelService.findAvailableHotels();
        return ResponseEntity.ok(hotels);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable Long id) {
        return hotelService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(@RequestParam String city) {
        List<Hotel> hotels = hotelService.findByCity(city);
        return ResponseEntity.ok(hotels);
    }
}