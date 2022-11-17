package apicalling;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;



public class TDesCommonDataEncryption {

    public static String encryptText(byte[] keyID1,byte[] keyID2, String plainText) throws Exception {
         
	        byte[] plaintext = plainText.getBytes();
	        Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	        SecretKeySpec myKey = new SecretKeySpec(keyID1, "DESede");
	        IvParameterSpec ivspec = new IvParameterSpec(keyID2);
	        c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
	        byte[] cipherText = c3des.doFinal(plaintext);
	        return Base64.getEncoder().encodeToString(cipherText);
	    }
	
	
		
	
}
