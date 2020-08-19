//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.exception.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtToken {
    private static final Logger log = LoggerFactory.getLogger(JwtToken.class);
    public static final String SECRET = "JINGDATAINVESTMENT";
    public static final int calendarField = 5;
    public static final int calendarInterval = 10;

    public JwtToken() {

    }

    public static String createToken(String user_id) throws Exception {
        Date iatDate = new Date();
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(5, 10);
        Date expiresDate = nowTime.getTime();
        Map<String, Object> map = new HashMap();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create().withHeader(map).withClaim("iss", "Jingdata").withClaim("aud", "APP").withClaim("user_id", user_id).withIssuedAt(iatDate).withExpiresAt(expiresDate).sign(Algorithm.HMAC256("JINGDATAINVESTMENT"));
        return token;
    }

    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt = null;

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("JINGDATAINVESTMENT")).build();
            jwt = verifier.verify(token);
        } catch (Exception var3) {
            var3.printStackTrace();
            log.error("token失效，请重新登录! token:{}", token);
            throw new RuntimeException("身份验证失败，请重新登录！");
        }

        return jwt.getClaims();
    }

    public static String getUserId(String token) {
        Map<String, Claim> claims = verifyToken(token);
        Claim user_id_claim = (Claim)claims.get("user_id");
        if (null != user_id_claim && StringUtils.isEmpty(user_id_claim.asString())) {
        }

        return String.valueOf(user_id_claim.asString());
    }

}
