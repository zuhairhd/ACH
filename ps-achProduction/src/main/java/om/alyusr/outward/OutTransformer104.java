package om.alyusr.outward;



import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class OutTransformer104 {
	
	
	private static Logger log = LogManager.getLogger(OutTransformer104.class.getName());


	/*******************************************************
	 * Bean start with sigMSG : which generate the message 
	 * before send to CBO
	 *******************************************************/
	public String signMsg104(String body) {
		
		String sigStr = "looking for signature";
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+4"));
		String todaydate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		String sicretStr = "";
		String msgContentsend = (body.replaceAll("(\\r)", "")).trim();
    	String msgContent = (msgContentsend + sicretStr + todaydate);
    	log.info("Token send for signature ="+msgContent);
		String msgId = getMsgId(msgContent);
	   	log.info("Message ID  ="+msgId);
		sigStr = creatSig(msgContent);
		log.info("Signature  ="+sigStr);
		String bldMsg = "";
		bldMsg = bldMsg + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		bldMsg = bldMsg + "<psys:request xmlns:psys=\"urn:iso:std:psys:request\" ";
		bldMsg = bldMsg + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n";
		bldMsg = bldMsg + "<id>" + msgId + "</id>\r\n";		
		bldMsg = bldMsg + "<type>DB</type>\r\n";
		bldMsg = bldMsg + "<format>MT</format>\r\n";
		bldMsg = bldMsg + "<date>" + todaydate + "</date>\r\n";
		bldMsg = bldMsg + "<signature>" + sigStr + "</signature>\r\n";
 		bldMsg = bldMsg + "<content><![CDATA[" + msgContentsend + "]]></content>\r\n";
		bldMsg = bldMsg + "</psys:request>";
		
		 //  log after each step (date , message content , signature , final xml )
		
	    log.info("+++++++++++( Transform the Message )++++++++++++++++");	
		log.info(todaydate, msgContentsend, sigStr, bldMsg);
		
		return bldMsg;
	}
	
	/*
	 *  getting ID
	 */

	private String getMsgId(String body) {
		int loc1 = body.indexOf(":20:") + 4;
		int loc2 = body.indexOf('\n', loc1);
		
		return (body.replaceAll("(\\r)", "")).substring(loc1, loc2).trim();
	}

	private String creatSig(String myString) {

		/*******************************************************
		 *  Key store access to get Private Key 
		 *******************************************************/
		String passwordForKeyCharArray = "password";
		String keyAlias = "alyusr";
		String keystorePasswordCharArray = "password";
		String filePathToStore = "alyusr4.jks";
		/******************************************************/

		PrivateKey privateKey;
		Signature sig = null;
		KeyStore ks;
		try {

			 
			 ks = KeyStore.getInstance("JKS");

			InputStream readStream = new FileInputStream(filePathToStore);
			ks.load(readStream, keystorePasswordCharArray.toCharArray());
			privateKey = (PrivateKey) ks.getKey(keyAlias, passwordForKeyCharArray.toCharArray());

			sig = Signature.getInstance("SHA256withRSA");
			sig.initSign(privateKey);
			sig.update(StringUtils.getBytesUtf8(myString));

			readStream.close();
			return Base64.encodeBase64String(sig.sign());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error creating Signature";

	}

}
