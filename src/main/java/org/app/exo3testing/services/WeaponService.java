package org.app.exo3testing.services;

import org.app.exo3testing.payloads.WeaponRequestPayload;
import org.app.exo3testing.payloads.WeaponResponsePayload;

import java.util.Set;
import java.util.UUID;

public interface WeaponService {
    Set<WeaponResponsePayload> getAllWeapons();
    WeaponResponsePayload getWeaponById(UUID weaponId);
    WeaponResponsePayload createWeapon(WeaponRequestPayload request);
}
