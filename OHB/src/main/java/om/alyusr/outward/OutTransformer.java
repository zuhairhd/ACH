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

import om.alyusr.MainAMRA;


public class OutTransformer {
	
	MainAMRA mainobj = new MainAMRA();
	private static Logger log = LogManager.getLogger(OutTransformer.class.getName());

    /******************************************************/
    /******************************************************/
    /******************************************************/
	/*******************************************************
	 * Bean start with sigMSG : which generate the message 
	 * before send to CBO
	 *******************************************************/
	public String signMsg(String body) {
		
		String sigStr = "looking for signature";
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+4"));
		String todaydate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		@SuppressWarnings("static-access")
		String sicretStr = mainobj.sicretStr;
		String msgContentsend = (body.replaceAll("(\\r)", "")).trim();
    	String msgContent = (msgContentsend + sicretStr + todaydate);
    	//log.info("Token send for signature ="+msgContent);
		String msgId = getMsgId(msgContent);
		String tranType = getTran(msgContent);
	   	//log.info("Message ID  ="+msgId);
		sigStr = creatSig(msgContent);
		//log.info("Signature  ="+sigStr);
		String bldMsg = "";
		bldMsg = bldMsg + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		//bldMsg = bldMsg + "<psy:request xmlns:psy=\"urn:iso:std:psys:request\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n";
		//<psys:request xmlns:psys=\"urn:iso:std:psys:request\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">
		bldMsg = bldMsg + "<psys:request xmlns:psys=\"urn:iso:std:psys:request\" ";
		bldMsg = bldMsg + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n";
		bldMsg = bldMsg + "<id>" + msgId + "</id>\r\n";
		if(tranType.equals("102")) {
			bldMsg = bldMsg + "<type>CR</type>\r\n";		
		} else if (tranType.equals("196")) {
			bldMsg = bldMsg + "<type>SR</type>\r\n";
		} else if (tranType.equals("192")){
			bldMsg = bldMsg + "<type>CN</type>\r\n";
		} else {
			bldMsg = bldMsg + "<type>CR</type>\r\n";
		}
		bldMsg = bldMsg + "<format>MT</format>\r\n";
		bldMsg = bldMsg + "<date>" + todaydate + "</date>\r\n";
		bldMsg = bldMsg + "<signature>" + sigStr + "</signature>\r\n";
 		bldMsg = bldMsg + "<content><![CDATA[" + msgContentsend + "]]></content>\r\n";
		bldMsg = bldMsg + "</psys:request>";
		
		 //  log after each step (date , message content , signature , final xml )
		
	    log.info("+++++++++++( processing the output Message )++++++++++++++++");	
		//log.info(todaydate, msgContentsend, sigStr, bldMsg);
		
		return bldMsg;
	}
	/*
	 *  Transaction type
	 */

	private String getTran(String body) {
		int loc1 = body.indexOf("{2:I") + 4;
		int loc2 = loc1 + 3;				
		return (body.replaceAll("(\\r)", "")).substring(loc1, loc2).trim();
	}
	/*
	 *  getting ID
	 */

	private String getMsgId(String body) {
		int loc1 = body.indexOf(":20:") + 4;
		int loc2 = body.indexOf('\n', loc1);
		
		return (body.replaceAll("(\\r)", "")).substring(loc1, loc2).trim();
	}

	
	@SuppressWarnings("static-access")
	private String creatSig(String myString) {

		/*******************************************************
		 *  Key store access to get Private Key 
		 *******************************************************/
		
		String passwordForKeyCharArray = mainobj.passwordForKeyCharArray;
		String keyAlias = "ohb";
		String keystorePasswordCharArray = mainobj.keystorePasswordCharArray;
		String filePathToStore = mainobj.filePathToStore;
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
			
			e.printStackTrace();
		}
		return "Error creating Signature";

	}


}
