package om.alyusr;

import org.apache.camel.builder.RouteBuilder;

import om.alyusr.inward.ProcessIncomingMsg;
import om.alyusr.inward.TransformeIncomeMsg;
import om.alyusr.outward.MyProcess;
import om.alyusr.outward.OutTransformer;

import om.alyusr.outward.OutTransformer102RTND;
import om.alyusr.outward.OutTransformer104;
import om.alyusr.outward.OutTransformer104Reversal;
import om.alyusr.outward.OutTransformerANSWERS;
import om.alyusr.outward.OutTransformerCN;

public class RouteACH extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("file:C:/ACH/Outward")
        .process(new MyProcess())
        .bean(new OutTransformer(), "signMsg")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("file:C:/ACH/Outward104")
        .process(new MyProcess())
        .bean(new OutTransformer104(), "signMsg104")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("file:C:/ACH/Outward104Reversal")
        .process(new MyProcess())
        .bean(new OutTransformer104Reversal(), "signMsg104Reversal")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("file:C:/ACH/Outward102RTND")
        .process(new MyProcess())
        .bean(new OutTransformer102RTND(), "signMsg102RTND")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("file:C:/ACH/OutwardANSWERS")
        .process(new MyProcess())
        .bean(new OutTransformerANSWERS(), "signMsgANSWERS")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("file:C:/ACH/OutwardCN")
        .process(new MyProcess())
        .bean(new OutTransformerCN(), "signMsgCN")
        .to("activemq:queue:ach_cbs_yusr_outward")
        .to("file:C:/ACH/Send2CBO");
		
		from("activemq:queue:ach_cbs_yusr_inward")
	      .process(new ProcessIncomingMsg())	
	      .bean(new TransformeIncomeMsg(), "virMsg")
	      .to("file:C:/ACH/Inward");
		
		// regulated Entity
		from("file:C:/ACH/yusr_wps_regEntity_out")
		.to("activemq:queue:yusr_wps_regEntity_out")
        .to("file:C:/ACH/Send2CBO");

		
		from("activemq:yusr_wps_regEntity_out_ack")      
	      .to("file:C:/ACH/yusr_wps_regEntity_out_ack");
		
		// sif files
		from("file:C:/ACH/yusr_wps_sif_process_out")
		.to("activemq:queue:yusr_wps_sif_process_out")
        .to("file:C:/ACH/Send2CBO");

		
		from("activemq:yusr_wps_sif_process_out_ack")      
	      .to("file:C:/ACH/yusr_wps_sif_process_out_ack");
		
		//
		from("file:C:/ACH/yusr_wps_sif_reply_out")
		.to("activemq:queue:yusr_wps_sif_reply_out")
        .to("file:C:/ACH/Send2CBO");

		
		from("activemq:yusr_wps_sif_reply_out_ack")      
	      .to("file:C:/ACH/yusr_wps_sif_reply_out_ack");
		
		//end of session status report
		
		from("activemq:yusr_wps_sync_in")      
	      .to("file:C:/ACH/yusr_wps_sync_in");
		
		
	}

}
