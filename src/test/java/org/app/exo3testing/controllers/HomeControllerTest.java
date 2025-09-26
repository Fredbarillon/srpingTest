package org.app.exo3testing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.exo3testing.entities.Weapon;
import org.app.exo3testing.payloads.WeaponRequestPayload;
import org.app.exo3testing.repositories.WeaponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeaponRepository weaponRepository;


    @Test
    @DisplayName("GET /api/weapon/weapon-listing -> 200 + 3 armes (liste statique)")
    void listing_ok() throws Exception {
        mockMvc.perform(get("/api/weapon/weapon-listing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weapons.length()").value(3))
                .andExpect(jsonPath("$.weapons[0].name").value("Katana"));
    }

    @Test
    @DisplayName("POST /api/weapon/add-weapon -> 200 et crée l'arme")
    void create_ok_and_persisted() throws Exception {
        WeaponRequestPayload req = new WeaponRequestPayload("WarHammer", "The ultimate onker", 725.25, 70.0, 8);

        mockMvc.perform(post("/api/weapon/add-weapon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weapon.name").value("WarHammer"))
                .andExpect(jsonPath("$.weapon.price").value(725.25));

        assertThat(weaponRepository.findAll())
                .anyMatch(w -> "WarHammer".equals(w.getName()) && w.getStock() == 8);
    }

    @Test
    @DisplayName("GET /api/weapon/weapon/{id} -> 200 quand trouvé")
    void get_one_ok() throws Exception {
        Weapon saved = weaponRepository.save(
                Weapon.builder().name("Axe").description("A sturdy viking weapon").price(359.5).size(68.5).stock(12).build()
        );

        mockMvc.perform(get("/api/weapon/weapon/{weaponid}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weapon.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.weapon.name").value("Axe"));
    }

    @Test
    @DisplayName("DELETE /api/weapon/remove-weapon/{id} -> 200 et supprime")
    void delete_ok() throws Exception {
        Weapon saved = weaponRepository.save(
                Weapon.builder().name("Dagger").description("sharp and poisoned").price(5.0).size(0.5).stock(9).build()
        );

        mockMvc.perform(delete("/api/weapon/remove-weapon/{weaponid}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));

        assertThat(weaponRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/weapon/remove-weapon/{id} -> 404 quand introuvable")
    void delete_404() throws Exception {
        mockMvc.perform(delete("/api/weapon/remove-weapon/{weaponid}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.message").value("Weapon not found"));
    }

    @Test
    @DisplayName("PATCH /api/weapon/weapon-edit/{id} -> 200 et met à jour")
    void patch_ok() throws Exception {
        Weapon saved = weaponRepository.save(
                Weapon.builder().name("knife").description("shit").price(10.0).size(1.0).stock(1).build()
        );

        WeaponRequestPayload patchBody = new WeaponRequestPayload("NewName", null, null, null, 5);

        mockMvc.perform(patch("/api/weapon/weapon-edit/{weaponid}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(true))
                .andExpect(jsonPath("$.weapon.name").value("NewName"))
                .andExpect(jsonPath("$.weapon.stock").value(5));

        Weapon after = weaponRepository.findById(saved.getId()).orElseThrow();
        assertThat(after.getName()).isEqualTo("NewName");
        assertThat(after.getStock()).isEqualTo(5);
    }

    @Test
    @DisplayName("PATCH /api/weapon/weapon-edit/{id} -> 404 quand introuvable")
    void patch_404() throws Exception {
        WeaponRequestPayload patchBody = new WeaponRequestPayload("X", null, null, null, null);

        mockMvc.perform(patch("/api/weapon/weapon-edit/{weaponid}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.updated").value(false));
    }

    // @Test
    // void listing_should_fail_wrong_size() throws Exception {
    //     mockMvc.perform(get("/api/weapon/weapon-listing"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.weapons.length()").value(4));
    // }
}
