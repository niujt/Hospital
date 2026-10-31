package com.hospital.finance.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** New staff credentials are salted hashes; existing plaintext records remain login-compatible. */
public final class LoginPasswordCodec {
    private static final int ROUNDS=210000;
    private static final SecureRandom RANDOM=new SecureRandom();
    private LoginPasswordCodec(){}
    public static String encode(String password){
        if(password==null||password.length()<8||password.length()>72)throw new IllegalArgumentException("新账号密码须为8至72个字符");
        return hash(password);
    }
    /** Only for an already verified existing credential; new credentials must use encode. */
    public static String encodeLegacyVerified(String password){
        if(password==null||password.isEmpty()||password.length()>256)throw new IllegalArgumentException("旧凭据长度无效");
        return hash(password);
    }
    private static String hash(String password){
        byte[] salt=new byte[24];RANDOM.nextBytes(salt);
        return "pbkdf2$"+ROUNDS+"$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(derive(password,salt,ROUNDS));
    }
    public static boolean matches(String stored,String supplied){
        if(stored==null||supplied==null)return false;
        if(!stored.startsWith("pbkdf2$"))return MessageDigest.isEqual(stored.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8));
        if(supplied.length()>256)return false;
        try{String[] parts=stored.split("\\$");if(parts.length!=4)return false;int rounds=Integer.parseInt(parts[1]);if(rounds<ROUNDS||rounds>1000000)return false;return MessageDigest.isEqual(Base64.getDecoder().decode(parts[3]),derive(supplied,Base64.getDecoder().decode(parts[2]),rounds));}catch(RuntimeException ex){return false;}
    }
    private static byte[] derive(String password,byte[] salt,int rounds){PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,rounds,256);try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}catch(java.security.GeneralSecurityException ex){throw new IllegalStateException("密码校验不可用",ex);}finally{spec.clearPassword();}}
}
