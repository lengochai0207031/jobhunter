package vn.hoidanit.jobhunter.util;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${hoidanit.jwt.base64-secret}")
    private String jwtKey;
    @Value("${hoidanit.jwt.token-validity-in-seconds}")
    private String jwtExpiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public void createToken(Authentication authentication) {

    }
}
