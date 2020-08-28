package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    private ShipService shipService;


    private boolean isCheckId(Long id) {
        return id == null || id <= 0 || id != Math.floor(id);
    }

    private boolean isCheckName(String name) {
        return name == null || name.isEmpty() || name.length() > 50;
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

    @GetMapping
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String planet,
                                                   @RequestParam(required = false) ShipType shipType,
                                                   @RequestParam(required = false) Long after,
                                                   @RequestParam(required = false) Long before,
                                                   @RequestParam(required = false) Boolean isUsed,
                                                   @RequestParam(required = false) Double minSpeed,
                                                   @RequestParam(required = false) Double maxSpeed,
                                                   @RequestParam(required = false) Integer minCrewSize,
                                                   @RequestParam(required = false) Integer maxCrewSize,
                                                   @RequestParam(required = false) Double minRating,
                                                   @RequestParam(required = false) Double maxRating,
                                                   @RequestParam(required = false) ShipOrder order,
                                                   @RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {
        List<Ship> shipList = shipService.getFilteredShipList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        List<Ship> sortList = shipService.sortShips(shipList, order);
        shipList = shipService.getPage(sortList, pageNumber, pageSize);

        if (shipList == null || shipList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(shipList, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteShip(@PathVariable Long id) {
        if (isCheckId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipService.deleteShip(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if (ship == null || isCheckId(id) || isCheckName(ship.getName()) || isCheckPlanet(ship.getPlanet())
                || isCheckProdDate(ship.getProdDate()) || isCheckSpeed(ship.getSpeed()) || isCheckCrewSize(ship.getCrewSize())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (shipService.updateShip(ship, id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(shipService.updateShip(ship, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ship == null || isCheckName(ship.getName()) || isCheckPlanet(ship.getPlanet()) || ship.getShipType() == null
                || isCheckProdDate(ship.getProdDate()) || isCheckSpeed(ship.getSpeed()) || isCheckCrewSize(ship.getCrewSize())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(shipService.create(ship), HttpStatus.OK);
    }

    @GetMapping(value = "count")
    public ResponseEntity<Integer> getShipCount(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String planet,
                                                @RequestParam(required = false) ShipType shipType,
                                                @RequestParam(required = false) Long after,
                                                @RequestParam(required = false) Long before,
                                                @RequestParam(required = false) Boolean isUsed,
                                                @RequestParam(required = false) Double minSpeed,
                                                @RequestParam(required = false) Double maxSpeed,
                                                @RequestParam(required = false) Integer minCrewSize,
                                                @RequestParam(required = false) Integer maxCrewSize,
                                                @RequestParam(required = false) Double minRating,
                                                @RequestParam(required = false) Double maxRating) {
        Integer count = shipService.getFilteredShipList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable Long id) {
        if (isCheckId(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = shipService.getShip(id);
        if (ship == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }
}
