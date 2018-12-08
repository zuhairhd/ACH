package om.alyusr.inward;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class TransformeIncomeMsg  {
	
	private static Logger log = LogManager.getLogger(TransformeIncomeMsg.class.getName());
	
	public String virMsg(String body){
		
		// remove the XML tags 
		Document xmlDoc = getDocumentstring(body);
		String sigVal  = getXMLTag(xmlDoc,"signature");
		String content = getXMLTag(xmlDoc,"content");
		String date    = getXMLTag(xmlDoc,"date");
		String sicretStr = ""; 
		
		//remove the CDATA section
		//remove the CDATA section
		content.replaceAll("!\\[CDATA", "");
		content.replaceAll("]]", "");
		content.replaceAll("\\[", "");
		
		log.info(content+sicretStr+date);
		log.info(sigVal);

		// Verify signature with CBO public key
		if (verifySig(content+sicretStr+date,sigVal)){
			log.info("+++++++++++( Transform the inward Message )++++++++++++++++");	
			log.info("Verify signature pass:", content, sigVal);
			return content;
		} else {
			log.info("+++++++++++( Transform the inward Message )++++++++++++++++");	
			log.info("Verify signature faile:", content, sigVal);
			return ":-:"+content;
		}
		
	}
	
	private static String getXMLTag(Document xmlDoc, String tagName) {
		NodeList listItem = xmlDoc.getElementsByTagName(tagName);
		// get only tag Element on index 0
		Element tagElement = (Element) listItem.item(0);
		NodeList elementList = tagElement.getChildNodes();
	
		return ((Node)elementList.item(0)).getNodeValue().trim();
	}
	
	
	private static Document getDocumentstring(String docString) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(docString));
			return builder.parse(is);

		} catch (Exception e) {
			
			System.out.println(e.getMessage());

		}
		return null;
	}
	/**
	 * 
	 * @param myString
	 * @param sigVal
	 * @return boolean 
	 */
	private boolean verifySig(String myString,String sigVal) {

		/*******************************************************
		 *  Key store access to get Private Key 
		 *******************************************************/
		//String passwordForKeyCharArray = "password";
		String keyAlias = "alyusr";
		String keystorePasswordCharArray = "password";
		String filePathToStore = "alyusr4.jks";
		/******************************************************/

//		PrivateKey privateKey;
		Signature sig = null;
		KeyStore ks;
		try {

			 
			 ks = KeyStore.getInstance("JKS");

			InputStream readStream = new FileInputStream(filePathToStore);
			ks.load(readStream, keystorePasswordCharArray.toCharArray());
	//		privateKey = (PrivateKey) ks.getKey(keyAlias, passwordForKeyCharArray.toCharArray());

			sig = Signature.getInstance("SHA256withRSA");
						
			sig.initVerify(ks.getCertificate(keyAlias).getPublicKey());
			sig.update(StringUtils.getBytesUtf8(myString));
			

			readStream.close();
			return sig.verify(Base64.decodeBase64(sigVal));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		

	}

}
