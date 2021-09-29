package com.arthia.arthia.controller;

import com.arthia.arthia.model.City;
import com.arthia.arthia.service.ICityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CityRessource {
    @Autowired
    private ICityService cityService;

    @GetMapping("/showCities")
    public ResponseEntity<List<City>> findCities() {

        List<City> cities = cityService.findAll();

        return ResponseEntity.ok().body(cities);
    }
}
