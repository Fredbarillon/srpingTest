package org.app.exo3testing.services;

import org.app.exo3testing.entities.Weapon;
import org.app.exo3testing.exceptions.ElementNotFoundException;
import org.app.exo3testing.payloads.WeaponRequestPayload;
import org.app.exo3testing.payloads.WeaponResponsePayload;
import org.app.exo3testing.repositories.WeaponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class WeaponServiceIT {

    @Autowired
    private WeaponService service;

    @Autowired
    private WeaponRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("getAllWeapons")
    void getAll_ok() {
        var w1 = repository.save(Weapon.builder().name("Katana").description("A japanese blade").price(599.99).size(81.0).stock(7).build());
        var w2 = repository.save(Weapon.builder().name("Axe").description("A sturdy viking weapon").price(359.50).size(68.50).stock(12).build());

        Set<WeaponResponsePayload> all = service.getAllWeapons();

        assertThat(all).hasSize(2);
        assertThat(all.stream().anyMatch(w -> w.name().equals("Katana"))).isTrue();
        assertThat(all.stream().anyMatch(w -> w.name().equals("Axe"))).isTrue();
    }

   // @Test
    // void getAll_should_fail_wrong_size() {
    //     Set<WeaponResponsePayload> all = service.getAllWeapons();
    //     assertThat(all).hasSize(42); 
    // }

    @Test
    @DisplayName("getWeaponById = payload")
    void getById_ok() {
        var saved = repository.save(Weapon.builder().name("Hammer").description("The Bonker").price(225.25).size(70.0).stock(8).build());

        WeaponResponsePayload res = service.getWeaponById(saved.getId());

        assertThat(res.id()).isEqualTo(saved.getId());
        assertThat(res.name()).isEqualTo("Hammer");
        assertThat(res.stock()).isEqualTo(8);
    }

    @Test
    @DisplayName("getWeaponById = exception")
    void getById_not_found() {
        assertThrows(ElementNotFoundException.class, () -> service.getWeaponById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("createWeapon = payload")
    void create_ok() {
        var req = new WeaponRequestPayload("Spear", "Long reach", 99.9, 120.0, 3);

        WeaponResponsePayload res = service.createWeapon(req);

        assertThat(res.id()).isNotNull();
        var inDb = repository.findById(res.id()).orElseThrow();
        assertThat(inDb.getName()).isEqualTo("Spear");
        assertThat(inDb.getSize()).isEqualTo(120.0);
        assertThat(inDb.getStock()).isEqualTo(3);
    }
}
