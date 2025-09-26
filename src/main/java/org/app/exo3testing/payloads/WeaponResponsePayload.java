package org.app.exo3testing.payloads;

import java.math.BigDecimal;
import java.util.UUID;

public record WeaponResponsePayload(UUID id, String name, String description, Double price, Double size, Integer stock) {
}
