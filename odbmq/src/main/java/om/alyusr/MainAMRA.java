package om.alyusr;

//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.PrivateKey;
//import java.security.Signature;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
//import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.binary.StringUtils;
//import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainAMRA {
	
	/*******************************************************
	 * Key store access to get Private Key
	 *******************************************************/
	public static String passwordForKeyCharArray;
	public static String keyAlias = "alyusr";
	public static String keystorePasswordCharArray ;
	public static String filePathToStore ;
	public static String sicretStr ; 
	/******************************************************/

	private static Logger log = LogManager.getLogger(MainAMRA.class.getName());

	public static void main(String[] args) {
		Integer intTime = 480;
		sicretStr = "";

		// System.setProperty("Dlog4j.configurationFile", "c:/ACH/log4j2.xml");
		// System.setProperty("logFilename", "c:/ACH/myApp.log");
		// PropertyConfigurator.configure("log4j.properties");

		RouteACH rt2cbo = new RouteACH();
		CamelContext ctx = new DefaultCamelContext();

		// *************** ( connection ) ***************
		// String keystoreName = "alyusr4.jks";
		// String pass = "password";
		if (args.length < 3) {
			// print use
			System.out.println("Using : java -jar a.jar keystoreName pass url time sicretStr");
			// exit
			log.error("************************************************************");
			log.error("Using : java -jar a.jar keystoreName pass url time sicretStr");
			log.error("************************************************************");

			System.exit(4);

		} else {
			String keystoreName = args[0];
			filePathToStore = keystoreName;
			String pass = args[1];
			passwordForKeyCharArray = pass;
			keystorePasswordCharArray = pass;
			String url = args[2];
			if (args.length > 3) {
				String time = args[3];
				intTime = Integer.valueOf(time);
				if (args.length > 4){
					sicretStr=args[4];
				}
			}
				// test link
				// String url = "ssl://achmqtest.cbonet.cboroot.om:4443";
				// production link
				// String url = "ssl://achmq.cbonet.cboroot.om:4443";
				System.setProperty("javax.net.ssl.keyStore", keystoreName); // keyStore
																			// //
																			// file
				System.setProperty("javax.net.ssl.keyStorePassword", pass); // keyStore
																			// //
																			// Password
				ctx.addComponent("activemq", ActiveMQComponent.activeMQComponent(url));
				log.info("keystore Name = " + keystoreName);
				log.info("Password = " + pass);
				log.info("URL : " + url);
			

			try {

				ctx.addRoutes(rt2cbo);
				log.info("**************** AMRA Started *************** ");
				ctx.start();
				/*******************************/
				Thread.sleep(intTime * 60 * 1000);
				/*******************************/
				ctx.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	
}
