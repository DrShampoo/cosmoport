package com.space.controller;

import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    private ShipService shipService;

    private boolean isCheckId(Long id) {
        return id == null || id <= 0 || id != Math.floor(id);
    }
    private boolean isCheckName(String name) {
        return name == null || name.isEmpty()|| name.length() > 50;
    }
    private boolean isCheckProdDate(Date date) {
        return date == null
                || date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800
                || date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019;
    }
    private boolean isCheckSpeed(Double speed) {
        return speed == null || speed < 0.01d || speed > 0.99d;
    }
    private boolean isCheckPlanet(String planet) {
        return isCheckName(planet);
    }
    private boolean isCheckCrewSize(Integer crewSize) {
        return crewSize == null || crewSize < 1 || crewSize > 9999;
    }

}
