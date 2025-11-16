package com.travelhub.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "star_rating")
    private Integer starRating;
    
    @Column(name = "price_per_night", precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    
    @Column(name = "available_rooms")
    private Integer availableRooms;
    
    // constrcutors
    public Hotel() {}
    
    public Hotel(String name, String city, String country, Integer starRating, 
                 BigDecimal pricePerNight, Integer availableRooms) {
        this.name = name;
        this.city = city;
        this.country = country;
        this.starRating = starRating;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
    }
    
    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Integer getStarRating() { return starRating; }
    public void setStarRating(Integer starRating) { this.starRating = starRating; }
    
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    
    public Integer getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(Integer availableRooms) { this.availableRooms = availableRooms; }
}