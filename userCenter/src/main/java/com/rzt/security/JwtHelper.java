package com.rzt.security;

import com.rzt.entity.RztSysRole;
import com.rzt.entity.RztSysUser;
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
import java.util.Map;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class JwtHelper {
    //Sample method to construct a JWT
    public static AccessToken createJWT(Map<String,Object> map, long expire) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("rzt82890758");
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		//稍后添加
        /*StringBuilder roleIds = new StringBuilder();
		for(int i=0;i<roleList.size();i++){
            if(i==0) {
                roleIds.append(roleList.get(i).getId()+",");
            }else{
                roleIds.append(roleList.get(i).getId());
            }
        }*/
        //Let's set the JWT Claims
        JwtBuilder builder =  Jwts.builder().setHeaderParam("type","JWT")
				.claim("user",map)
//                .claim("username",username)
//                .claim("password",password)
//                .claim("roleIds",roleIds.toString())
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
        /*if(claims!=null){
            RztSysUser user = new RztSysUser();
            user.setUsername(claims.get("USERNAME").toString());
//            user.setPassword(claims.get("password").toString());
            String[] roleIds = claims.get("roleIds").toString().split(",");
            List<RztSysRole> roleList = new ArrayList<RztSysRole>();
            for(String roleId:roleIds){
				RztSysRole role = new RztSysRole();
                role.setId(roleId);
                roleList.add(role);
            }
            audience.setRoleList(roleList);
            audience.setUser(user);
        }*/

        return audience;
    }

    public static void main(String[] args) {
        System.out.println(System.nanoTime());
        String auth = "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VyIjp7IldPUktUWVBFIjozLCJXT1JLWUVBUiI6MTIsIlJFQUxOQU1FIjoi6LW15YWtIiwiQ0VSVElGSUNBVEUiOiIxNDA0MzAxOTYwMDQyODI5MTEiLCJQSE9ORSI6IjE1MjE1NDY1NDc4IiwiVVNFUkRFTEVURSI6MSwiRU1BSUwiOiJ6aGFvbGl1QDE2My5jb20iLCJDTEFTU05BTUUiOiLmsZ_mtbfljZciLCJVU0VSVFlQRSI6MSwiVVNFUk5BTUUiOiJ6aGFvbGl1IiwiQ09NUEFOWU5BTUUiOiLkuqzlronpobrkv53lronlhazlj7giLCJERVBUIjoi5Liw5Y-w5YWs5Y-4IiwiQ09NUEFOWUlEIjoiMDIyZGFiNDhhZTMwNDQwZDlkMTExMjQ3YjhmYjNiZjMiLCJJRCI6IjQzMTE1MmE5MThmOTRmMDNiNDAyODZjOTU5YmI3ODQ0IiwiREVQVElEIjoiNDAyODgxZTY2MDNhNjliODAxNjAzYTcyNmQ2YTAwMTQiLCJST0xFSUQiOm51bGwsIkxPR0lOU1RBVFVTIjoxLCJBR0UiOjQyLCJDTEFTU0lEIjoiMjgwMjgxOWY2MDkxOWVjMDAxNjA5MmFlZTAxYTAwMDQifSwiZXhwIjoxNTE1MTg3NTExfQ.cPxBvBanJu8AZFzPK1Qf36dVQeloonF3vlV0UiVpb5Y";
        parseJWT(auth);
        System.out.println(System.nanoTime());
    }

}
