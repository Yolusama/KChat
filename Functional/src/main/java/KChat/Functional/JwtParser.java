package KChat.Functional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JwtParser {
    public static Map<String,Object> parse(String secretKey, String jwtToken){
              return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwtToken).getBody();
    }
}
