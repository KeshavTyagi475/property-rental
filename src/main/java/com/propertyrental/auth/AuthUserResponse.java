package com.propertyrental.auth;

import java.util.List;

public record AuthUserResponse(
        String username,
        List<String> roles
) {
}
