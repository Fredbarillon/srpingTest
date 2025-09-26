package org.app.exo3testing.services;

import lombok.RequiredArgsConstructor;
import org.app.exo3testing.payloads.WeaponRequestPayload;
import org.app.exo3testing.payloads.WeaponResponsePayload;
import org.app.exo3testing.repositories.WeaponRepository;
import org.springframework.stereotype.Service;
import org.app.exo3testing.exceptions.ElementNotFoundException;
import org.app.exo3testing.entities.Weapon;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeaponServiceImpl implements WeaponService {
    private final WeaponRepository weaponRepository;



    @Override
    public Set<WeaponResponsePayload> getAllWeapons() {
        return weaponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public WeaponResponsePayload getWeaponById(UUID weaponId) {
        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ElementNotFoundException("Weapon", weaponId));
        return toResponse(weapon);
    }

    @Override
    public WeaponResponsePayload createWeapon(WeaponRequestPayload request) {
        return toResponse(weaponRepository.save(toEntity(request)));
    }

    private WeaponResponsePayload toResponse(Weapon weapon) {
        return new WeaponResponsePayload(
                weapon.getId(),
                weapon.getName(),
                weapon.getDescription(),
                weapon.getPrice(),
                weapon.getSize(),
                weapon.getStock()
        );
    }

    private Weapon toEntity(WeaponRequestPayload request) {
        return Weapon.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .size(request.size())
                .stock(request.stock())
                .build();
    }
}
