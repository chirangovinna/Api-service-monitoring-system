package com.sms.monitoring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sms.monitoring.API.CampaignList;
import com.sms.monitoring.API.ChargeCampaign;
import com.sms.monitoring.API.CheckBalance;
import com.sms.monitoring.API.CheckcreatedcampaignApi;
import com.sms.monitoring.API.CreateCampaign;
import com.sms.monitoring.API.LoginRequest;
import com.sms.monitoring.API.SMSapi;
import com.sms.monitoring.API.SmsViaUrlCampaign;
import com.sms.monitoring.API.ViewAllTemplates;
import com.sms.monitoring.MailService.EmailSender;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MonitoringApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(MonitoringApplication.class, args);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		

        Runnable codeToRun = () -> {

			try {
				// trantraction id
				String transactionId =  Integer.toString(((int) java.time.Instant.now().getEpochSecond()));
				String accessToken="";

				System.out.println("--------------------------------------------");
				// login to the system and get the access token
				LoginRequest loginuser = new LoginRequest();
				accessToken = loginuser.getAccessToken();

				// calling send sms API
				SMSapi smsUser = new SMSapi();
				smsUser.setAccessToken(accessToken);
				smsUser.setTransactionId(transactionId);
				System.out.println("--------------------------------------------");	
				smsUser.send();

				//Check created campaign status for a transaction id 
				CheckcreatedcampaignApi campainUser = new CheckcreatedcampaignApi();
				campainUser.setAccessToken(accessToken);
				campainUser.setTransactionId(transactionId);
				System.out.println("--------------------------------------------");	
				campainUser.checkTransaction();

				// send sms via url
				SmsViaUrlCampaign urlUser = new SmsViaUrlCampaign();
				System.out.println("--------------------------------------------");	
				urlUser.sendMessageViaUrlCampaign("0714551682,763625800,94777337045", "TestAdeona", "Welcome", "https://xx/xx");

				// check balance
				CheckBalance checkBalanceUser = new CheckBalance();
				System.out.println("--------------------------------------------");	
				checkBalanceUser.checkBalance();

				ViewAllTemplates templatesViewUser = new ViewAllTemplates();
				templatesViewUser.setAccessToken(accessToken);

				// campaign lists
				CampaignList campaignlistuser = new CampaignList();
				campaignlistuser.setAccessToken(accessToken);
				System.out.println("--------------------------------------------");
				campaignlistuser.getCampaignList();

				// Createing Campaigns
				CreateCampaign campaignCreateUser = new CreateCampaign();
				campaignCreateUser.setAccessToken(accessToken);
				System.out.println("--------------------------------------------");
				int campaignId = campaignCreateUser.CreatingCampaigns();

				ChargeCampaign campaignUserCharge = new ChargeCampaign();
				campaignUserCharge.setAccessToken(accessToken);
				// // System.out.println("--------------------------------------------");
				// // campaignUserCharge.chargingForCampaign(campaignId);	
				
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Get the EmailUser bean and call the initiateEmailSending method
			EmailSender emailUser = context.getBean(EmailSender.class);
			if(Massage.mailBody!=""){
				System.out.println("Body: "+Massage.mailBody);
				emailUser.sendCustomEmail();
			}
			Massage.mailBody="";
		};
		// Schedule the code to run every 2 minutes
        scheduler.scheduleAtFixedRate(codeToRun, 0, 2, TimeUnit.MINUTES);
		/**
		 * There is a limitation in the email sending 
		 * ther only 2000 email per day 
		 */


	}

}
