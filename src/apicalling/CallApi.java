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
import org.apache.http.ParseException;
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
public class CallApi {

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

    public void gethttp() {

        String ENCRYPTION_KEY = "MyEncryptionKeyERA_IBAPP_BANKING";
        String responseString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("key", ENCRYPTION_KEY);
            json.put("terminalID", "webadmin");
            json.put("operationMode", "I");

            String jsonString = json.toString();
            System.out.println("jsonString = " + jsonString);
            String encrypt = encrypt(jsonString, ENCRYPTION_KEY);
            System.out.println("encrypt = " + encrypt);

            String url = "http://192.183.155.22/ERABAsiaCreditCardTransfer/V01/tokenGenerateCreditCardMoneyTransfer";
            System.out.println("url = " + url);

            // JSONObject params = new JSONObject();
            // params.put("key", encrypt);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            JSONObject jsonObject = new JSONObject();
            StringEntity enttity = new StringEntity(encrypt);
            httppost.setEntity(enttity);

            CloseableHttpResponse httpResponse = httpClient.execute(httppost);

            int status = httpResponse.getStatusLine().getStatusCode();

            if (status == 200) {
                System.out.println("status = " + status);
                responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                System.out.println("response = " + responseString);

                JSONObject obj = new JSONObject(responseString);

                String keyId = "";
                String key1 = "";
                String key2 = "";

                if (obj.has("keyId") && !obj.isNull("keyId")) {
                    keyId = (String) obj.get("keyId");
                }
                System.out.println("keyId = " + keyId);
                if (obj.has("key1") && !obj.isNull("key1")) {
                    key1 = (String) obj.get("key1");
                    
                }
                System.out.println("key1 = " + key1);
                if (obj.has("key2") && !obj.isNull("key2")) {
                    key2 = (String) obj.get("key2");
                }               
               
                System.out.println("key2 = " + key2);
            }else{
            }

        } catch (JSONException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(CallApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {

        CallApi callApi = new CallApi();
        callApi.gethttp();
    }

}
