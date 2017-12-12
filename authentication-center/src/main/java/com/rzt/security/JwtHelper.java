package com.rzt.security;

import com.rzt.entity.Role;
import com.rzt.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class JwtHelper {
    //Sample method to construct a JWT
    public static AccessToken createJWT(String username, String password, List<Role> roleList, long expire) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("rzt82890758");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        StringBuilder roleIds = new StringBuilder();
        for(int i=0;i<roleList.size();i++){
            if(i==0) {
                roleIds.append(roleList.get(i).getId()+",");
            }else{
                roleIds.append(roleList.get(i).getId());
            }
        }
        //Let's set the JWT Claims
        JwtBuilder builder =  Jwts.builder().setHeaderParam("type","JWT")
                .claim("userName",username)
                .claim("password",password)
                .claim("roleIds",roleIds.toString())
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (expire >= 0) {
            long expMillis = nowMillis + expire;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        AccessToken accessToken = new AccessToken(builder.compact(),"JWT",expire);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return accessToken;
    }

    public static Audience parseJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("rzt82890758"))
                .parseClaimsJws(jwt).getBody();
        Audience audience = new Audience();
        if(claims!=null){
            User user = new User();
            user.setUsername(claims.get("userName").toString());
            user.setPassword(claims.get("password").toString());
            String[] roleIds = claims.get("roleIds").toString().split(",");
            List<Role> roleList = new ArrayList<Role>();
            for(String roleId:roleIds){
                Role role = new Role();
                role.setId(roleId);
                roleList.add(role);
            }
            audience.setUser(user);
            audience.setRoleList(roleList);
        }

        return audience;
    }


}
