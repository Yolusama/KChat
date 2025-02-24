package KChat.Service;

import KChat.Common.Constants;
import KChat.Configuration.JwtConfig;
import KChat.Functional.JwtGenerator;
import KChat.Functional.JwtParser;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public String secretKey()
    {
        return jwtConfig.getSecretKey();
    }

    @Autowired
    public JwtService(JwtConfig jwtConfig)
    {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Map<String,Object> claims, Duration expire)
    {
        return JwtGenerator.generateToken(jwtConfig.getSecretKey(),
                claims,expire,jwtConfig.getIssuer(),jwtConfig.getAudience());
    }

    public String getUserIdFromToken(String token)
    {
        try {
            return JwtParser.parse(jwtConfig.getSecretKey(), token)
                    .get(Constants.JwtUserClaim).toString();
        }
        catch (ExpiredJwtException ex){
            ex.printStackTrace();
            return Constants.TokenExpireSign;
        }
    }
}
