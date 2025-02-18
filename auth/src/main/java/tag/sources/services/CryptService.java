package tag.sources.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tag.sources.system.AuthConfig;


import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

@Service
public class CryptService {
    private final AuthConfig authConfig;

    @Autowired
    public CryptService(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public String hashPassword(String password) {
        String saltedPassword = password + authConfig.getSalt();

        HashFunction hf = Hashing.md5();
        return hf.newHasher()
                .putBytes(saltedPassword.getBytes(StandardCharsets.UTF_8))
                .hash()
                .toString();
    }

    public boolean validatePassword(String password, String storedHash) {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }

}
