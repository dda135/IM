package mine.fanjh.encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
/**
 * Created by wsdevotion on 15/10/24.
 */
public class SecUtil {


    private static Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对
     *
     * @param filePath 生成密钥的路径
     * @return
     */
    public static Map<String, String> generateKeyPair(String filePath) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 密钥位数
            keyPairGen.initialize(1024);
            // 密钥对
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //得到公钥字符串
            String publicKeyString = getKeyString(publicKey);
            //得到私钥字符串
            String privateKeyString = getKeyString(privateKey);
            //将密钥对写入到文件
//            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
//            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
//            BufferedWriter pubbw = new BufferedWriter(pubfw);
//            BufferedWriter pribw = new BufferedWriter(prifw);
//            pubbw.write(publicKeyString);
//            pribw.write(privateKeyString);
//            pubbw.flush();
//            pubbw.close();
//            pubfw.close();
//            pribw.flush();
//            pribw.close();
//            prifw.close();
            //将生成的密钥对返回
            Map<String, String> map = new HashMap<String, String>();
            map.put("publicKey", publicKeyString);
            map.put("privateKey", privateKeyString);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64Utils.decode(key);
        //keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64Utils.decode(key);
        //keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = Base64Utils.encode(keyBytes);
        //String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    /**
     * 使用公钥对明文进行加密，返回BASE64编码的字符串
     *
     * @param publicKey
     * @param plainText
     * @return
     */
    public static String encrypt(PublicKey publicKey, String plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes());
            return Base64Utils.encode(enBytes);
            //return (new BASE64Encoder()).encode(enBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用keystore对明文进行加密
     *
     * @param publicKeystore 公钥文件路径
     * @param plainText      明文
     * @return
     */
    public static String encrypt(String publicKeyString, String plainText) {
        try {
//            FileReader fr = new FileReader(publicKeystore);
//            BufferedReader br = new BufferedReader(fr);
//            String publicKeyString = "";
//            String str;
//            while ((str = br.readLine()) != null) {
//                publicKeyString += str;
//            }
//            br.close();
//            fr.close();
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyString));
            byte[] enBytes = cipher.doFinal(plainText.getBytes());
            return Base64Utils.encode(enBytes);
            //return (new BASE64Encoder()).encode(enBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptMax(String publicKeyString, String plainText) {
        int len = LenUtil.length(plainText);
        String s = "";
        String plain = "";
        if (len < 117) {
            return SecUtil.encrypt(publicKeyString, plainText);
        } else {
            for (int i = 0; i <len;){
                //分块加密，没有考虑汉字的问题
                if(i+100<len) {
                    plain = plainText.substring(i, (i + 100));
                }else{
                    plain = plainText.substring(i, len);
                }
                s = s + SecUtil.encrypt(publicKeyString, plain) + "|";
                i = i + 100;
            }
            s = s.substring(0, s.length()-1);
            return s;
        }
    }

    public static String decryptMax(String privateKeyString, String enStr) {
        String res = "";
        if(!enStr.contains("|")){
            return SecUtil.decrypt(privateKeyString, enStr);
        }else{
            //分块解密
            String [] str = enStr.split("[|]");
            for(String s : str){
                String reg = SecUtil.decryptMax(privateKeyString, s);
                res = res + reg;
            }

            return res;
        }
    }

    /**
     * 使用私钥对明文密文进行解密
     *
     * @param privateKey
     * @param enStr
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String enStr) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64Utils.decode(enStr));
            //byte[] deBytes = cipher.doFinal((new BASE64Decoder()).decodeBuffer(enStr));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用keystore对密文进行解密
     *
     * @param privateKeystore 私钥路径
     * @param enStr           密文
     * @return
     */
    public static String decrypt(String privateKeyString, String enStr) {
        try {
//            FileReader fr = new FileReader(privateKeystore);
//            BufferedReader br = new BufferedReader(fr);
//            String privateKeyString = "";
//            String str;
//            while ((str = br.readLine()) != null) {
//                privateKeyString += str;
//            }
//            br.close();
//            fr.close();
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyString));
            byte[] deBytes = cipher.doFinal(Base64Utils.decode(enStr));
            //byte[] deBytes = cipher.doFinal((new BASE64Decoder()).decodeBuffer(enStr));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
