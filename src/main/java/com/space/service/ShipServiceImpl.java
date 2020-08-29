package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    ShipRepository shipRepository;

    @Override//1
    public List<Ship> getAllShip() {
        return shipRepository.findAll();
    }

    @Override//1
    public Ship create(Ship ship) {
        if (ship.isUsed() == null) {
            ship.setUsed(false);
        }
        ship.setSpeed((double) Math.round(ship.getSpeed() * 100) / 100);
        ship.setRating(ratingCalc(ship));
       return shipRepository.save(ship);
    }

    @Override//1
    public Ship updateShip(Ship ship, long id) {
        Ship oldShip = getShip(id);
        if (oldShip == null) {
            return null;
        }
        if (ship.getName() != null) {
            oldShip.setName(ship.getName());
        }
        if (ship.getCrewSize() != null) {
            oldShip.setCrewSize(ship.getCrewSize());
        }
        if (ship.getSpeed() != null) {
            oldShip.setSpeed(ship.getSpeed());
        }
        if (ship.getPlanet() != null) {
            oldShip.setPlanet(ship.getPlanet());
        }
        if (ship.getProdDate() != null) {
            oldShip.setProdDate(ship.getProdDate());
        }
        if (ship.getShipType() != null) {
            oldShip.setShipType(ship.getShipType());
        }
        oldShip.setRating(ratingCalc(oldShip));
        return shipRepository.save(oldShip);
    }

    @Override//1
    public boolean deleteShip(long id) {
        if (shipRepository.existsById(id)) {
            shipRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override//1
    public Ship getShip(long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override//1
    public List<Ship> getFilteredShipList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        return getAllShip().stream()
                .filter(o -> name == null || o.getName().contains(name))
                .filter(o -> planet == null || o.getPlanet().contains(planet))
                .filter(o -> shipType == null || o.getShipType().equals(shipType))
                .filter(o -> after == null || o.getProdDate().getTime() >= new Date(after).getTime())
                .filter(o -> before == null || o.getProdDate().getTime() <= new Date(before).getTime())
                .filter(o -> isUsed == null || o.isUsed().equals(isUsed))
                .filter(o -> minSpeed == null || o.getSpeed() >= minSpeed)
                .filter(o -> maxSpeed == null || o.getSpeed() <= maxSpeed)
                .filter(o -> minCrewSize == null || o.getCrewSize() >= minCrewSize)
                .filter(o -> maxCrewSize == null || o.getCrewSize() <= maxCrewSize)
                .filter(o -> minRating == null || o.getRating() >= minRating)
                .filter(o -> maxRating == null || o.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    @Override//3
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED:return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE:return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING:return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }
        return ships;
    }


    public List<Ship> getPage(List<Ship> ship, Integer pageNumber, Integer pageSize) {//3
        Integer page = pageNumber == null ? 0 : pageNumber;
        Integer size = pageSize == null ? 3 : pageSize;
        int x = page * size;
        int y = x + size;
        if ( y > ship.size()) {
            y = ship.size();
        }
        return ship.subList(x, y);
    }

    public Double ratingCalc (Ship ship) {//1
        double value = ship.isUsed() ? 0.5 : 1.0;
        int year = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double rating = (80 * ship.getSpeed() * value) / (3019 - year + 1d);
        return (double) Math.round(rating * 100) / 100;
    }

}
