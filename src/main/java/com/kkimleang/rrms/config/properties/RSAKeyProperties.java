package com.kkimleang.rrms.config.properties;

import java.security.interfaces.*;
import org.springframework.boot.context.properties.*;

@ConfigurationProperties(prefix = "rsa")
public record RSAKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
