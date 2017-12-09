package com.rzt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class JwtHelper {
    //Sample method to construct a JWT
    public static AccessToken createJWT(String username, String password, long expire) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("rzt82890758");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder =  Jwts.builder().setHeaderParam("type","JWT")
                .claim("userName",username)
                .claim("password",password)
                .signWith(signatureAlgorithm, signingKey);
        if (expire >= 0) {
            long expMillis = nowMillis + expire;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        AccessToken accessToken = new AccessToken(builder.compact(),"JWT",expire);

        return accessToken;
    }

    public static Map<String,String> parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("rzt82890758"))
                .parseClaimsJws(jwt).getBody();
        Map<String,String> map = new HashMap<String, String>();
        if(claims!=null){
            String userName = claims.get("userName").toString();
            String password = claims.get("password").toString();
            map.put("username",userName);
            map.put("password",password);
        }
        return map;
    }


}
