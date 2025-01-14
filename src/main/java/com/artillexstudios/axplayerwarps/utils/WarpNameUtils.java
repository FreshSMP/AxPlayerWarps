package com.artillexstudios.axplayerwarps.utils;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class WarpNameUtils {
    public enum ValidationResult {
        ALLOWED,
        CONTAINS_SPACES,
        INVALID_LENGTH
    }

    public static ValidationResult isAllowed(String name) {
        if (!CONFIG.getBoolean("warp-naming.allow-spaces", false) && name.contains(" ")) {
            return ValidationResult.CONTAINS_SPACES;
        }

        if (name.length() < CONFIG.getInt("warp-naming.length.min", 1)
                || name.length() > CONFIG.getInt("warp-naming.length.max", 16)
        ) {
            return ValidationResult.INVALID_LENGTH;
        }

        return ValidationResult.ALLOWED;
    }
}
