package com.kkimleang.rrms.util;

import lombok.Getter;

@Getter
public enum JwtUtils {
    USER_ID("idUser"),
    NAME("name"),
    SURNAME("surname"),
    EMAIL("email"),
    EXPIRE("ieExpire"),
    SCOPE("scope");

    private final String property;

    JwtUtils(String property) {
        this.property = property;
    }
}
