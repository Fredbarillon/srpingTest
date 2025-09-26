package org.app.exo3testing.controllers;

import org.app.exo3testing.entities.Weapon;
import org.app.exo3testing.payloads.WeaponRequestPayload;
import org.app.exo3testing.payloads.WeaponResponsePayload;
import org.app.exo3testing.repositories.WeaponRepository;
import org.app.exo3testing.services.WeaponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/weapon")
public class HomeController {

    private final WeaponService weaponService;
    private final WeaponRepository weaponRepository;

    public HomeController(WeaponService weaponService, WeaponRepository weaponRepository) {
        this.weaponService = weaponService;
        this.weaponRepository = weaponRepository;
    }


    @GetMapping("/weapon-listing")
    public ResponseEntity<Map<String, Object>> getListing() {
        List<Weapon> weaponStored = List.of(
                Weapon.builder().name("Katana").description("A japanese blade").price(599.99).size(81.00).stock(7).build(),
                Weapon.builder().name("Axe").description("A sturdy viking weapon").price(359.50).size(68.50).stock(12).build(),
                Weapon.builder().name("Hammer").description("The Bonker").price(225.25).size(70.00).stock(8).build()
        );

        return ResponseEntity.ok(Map.of("weapons", weaponStored));
    }

    @PostMapping("/add-weapon")
    public ResponseEntity<Map<String, Object>> create(@RequestBody WeaponRequestPayload payload) {
        WeaponResponsePayload created = weaponService.createWeapon(payload);
        return ResponseEntity.ok(Map.of("weapon", created));
    }

    @DeleteMapping("/remove-weapon/{weaponid}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("weaponid") UUID weaponId) {
        return weaponRepository.findById(weaponId)
                .map(entity -> {
                    weaponRepository.delete(entity);
                    Map<String, Object> body = new HashMap<>();
                    body.put("deleted", true);
                    body.put("id", weaponId.toString());
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("deleted", false);
                    body.put("message", "Weapon not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
                });
    }

    @GetMapping("/weapon/{weaponid}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable("weaponid") UUID weaponId) {
        WeaponResponsePayload weapon = weaponService.getWeaponById(weaponId);
        return ResponseEntity.ok(Map.of("weapon", weapon));
    }

    @PatchMapping("/weapon-edit/{weaponid}")
    public ResponseEntity<Map<String, Object>> patch(
            @PathVariable("weaponid") UUID weaponId,
            @RequestBody WeaponRequestPayload payload
    ) {
        return weaponRepository.findById(weaponId).map(entity -> {
            if (payload.name() != null) entity.setName(payload.name());
            if (payload.description() != null) entity.setDescription(payload.description());
            if (payload.price() != null) entity.setPrice(payload.price());
            if (payload.size() != null) entity.setSize(payload.size());
            if (payload.stock() != null) entity.setStock(payload.stock());
            var saved = weaponRepository.save(entity);
            WeaponResponsePayload response = new WeaponResponsePayload(
                    saved.getId(),
                    saved.getName(),
                    saved.getDescription(),
                    saved.getPrice(),
                    saved.getSize(),
                    saved.getStock()
            );
            return ResponseEntity.ok(Map.of("weapon", response, "updated", true));
        }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("updated", false, "message", "Weapon not found")));
    }
}
