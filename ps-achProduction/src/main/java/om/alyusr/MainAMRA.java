package om.alyusr;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
//import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainAMRA {

	private static Logger log = LogManager.getLogger(MainAMRA.class.getName());

	public static void main(String[] args) {

		System.setProperty("Dlog4j.configurationFile", "c:/ACH/log4j2.xml");
		System.setProperty("logFilename", "c:/ACH/myApp.log");

		RouteACH rt2cbo = new RouteACH();
		CamelContext ctx = new DefaultCamelContext();

		// *************** ( connection ) ***************
		String keystoreName = "alyusr4.jks";
		String pass = "password";
		String url = "ssl://achmqtest.cbonet.cboroot.om:4443";
		System.setProperty("javax.net.ssl.keyStore", keystoreName); // keyStore
																	// file
		System.setProperty("javax.net.ssl.keyStorePassword", pass); // keyStore
																	// Password
		ctx.addComponent("activemq", ActiveMQComponent.activeMQComponent(url));

		log.info("keystore Name = " + keystoreName);
		log.info("Password = " + pass);
		log.info("URL : " + url);

		try {

			ctx.addRoutes(rt2cbo);
			log.info("Starting the Route ");
			ctx.start();
			/*********** 5 Minutes *********/
			Thread.sleep(5 * 60 * 1000);
			/*******************************/
			ctx.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
