package om.alyusr.outward;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyProcess implements Processor {

	public void process(Exchange arg0) throws Exception {
		System.out.println("Now Processing message: " + arg0.getIn().getBody(String.class));
	}

}

