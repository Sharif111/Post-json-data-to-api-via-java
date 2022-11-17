package apicalling;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author User
 */
public class ResponseApi {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey) throws NoSuchAlgorithmException {

        byte[] key = myKey.getBytes();
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
        
    }

    public static String encrypt(String strToEncrypt, String ENCRYPTION_KEY) {
        System.out.println("strToEncrypt : " + strToEncrypt);
        System.out.println("ENCRYPTION_KEY : " + ENCRYPTION_KEY);
        String hexString = "";
        try {
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            setKey(ENCRYPTION_KEY);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            hexString = Hex.encodeHexString(cipher.doFinal(strToEncrypt.getBytes()));
            return hexString;
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    

    public static void main(String[] args) throws IOException, Exception {

        String ENCRYPTION_KEY = "MyEncryptionKeyERA_IBAPP_BANKING";
        
                String sEncryptedPassword="";
                String responseString="";
                String keyId = "989631";
                String key1 = "osNQ7udAEfCR5vDDBnkFKEbR";
                String key2 = "90HMpalI";
                String sPassword="BALUSER";
             
        sEncryptedPassword = TDesCommonDataEncryption.encryptText(key1.getBytes(), key2.getBytes(), sPassword);
        System.out.println("sEncryptedPassword "+ sEncryptedPassword);
        try {
            JSONObject json = new JSONObject();
            json.put("encryptedPassword", sEncryptedPassword);  
            
            json.put("key", keyId);
            json.put("itclTranID", "test");
            json.put("requestNo", "52223");
            json.put("reason", "test");        
            json.put("terminalID", "001");
            json.put("mailID", "user");
            json.put("sessionID", "user01");
            json.put("operationMode", "AAA"); 
            

            String jsonString = json.toString();
            System.out.println("jsonString = " + jsonString);
            String encrypt = encrypt(jsonString, ENCRYPTION_KEY);
            System.out.println("encrypt = " + encrypt);

            String url = "http://192.183.155.22/ERABAsiaCreditCardTransfer/V01/creditCardTransferReversal";
            System.out.println("url = " + url);

          

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            JSONObject jsonObject = new JSONObject();
            StringEntity enttity = new StringEntity(encrypt);
             httppost.setEntity(enttity);
            
            CloseableHttpResponse httpResponse = httpClient.execute(httppost);
            responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("response = " + responseString);
        

        } catch (JSONException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

