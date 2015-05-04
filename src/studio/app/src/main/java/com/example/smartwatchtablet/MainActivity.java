package com.example.smartwatchtablet;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{

	TextView tvDate,tvTemp,tvGmail,tvWhatsapp,tvMissedcall,tvHangouts,tvTicker;
	private CustomNotificationReceiver notificationReceiver;
	private Packages packagesInfo;
	String tickerText;
	BTInterface bt;
	StringBuffer str;
	Date date;
	DateFormat dateFormat;
	DigitalClock dc;
	Calendar c;
	int hours,mins;
	static final String baseURL = "http://api.openweathermap.org/data/2.5/weather?q=";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);


		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


		setContentView(R.layout.activity_main);
		
		str  = new StringBuffer("");
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		date = new Date();
		tvDate = (TextView)findViewById(R.id.tvDate);
		tvDate.setText(dateFormat.format(date));
		tvTemp = (TextView)findViewById(R.id.tvTemp);
		//weather();
		tvGmail = (TextView)findViewById(R.id.tvGmail);
		tvWhatsapp = (TextView)findViewById(R.id.tvWhatsapp);
		tvMissedcall = (TextView)findViewById(R.id.tvMissedCall);
		tvHangouts = (TextView)findViewById(R.id.tvHangouts);
		tvTicker = (TextView)findViewById(R.id.tvTicker);
		tvTicker.setSelected(true);
		dc = (DigitalClock)findViewById(R.id.digitalClock1);
		notificationReceiver = new CustomNotificationReceiver();
		packagesInfo = new Packages();
		packagesInfo.gmailCount = 0;
		packagesInfo.messageCount = 0;
		packagesInfo.missedCallCount = 0;
		packagesInfo.whatsappCount = 0;
		notificationReceiver = new CustomNotificationReceiver();

		packagesInfo = new Packages();

		bt = new BTInterface();
		bt.connect();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
		registerReceiver(notificationReceiver,filter);

		Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		startActivity(intent);
		sendToPhone();
		timeupdate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
void timeupdate(){
	Thread t= new Thread(){
		public void run() {
			try{
				sleep(60*1000);
				sendToPhone();
			}catch(Exception e)
			{
				
				e.printStackTrace();
			}
		};
	};
	t.start();
}
	void weather()
	{
		
		final Handler handler = new Handler(); 
		Thread t = new Thread(){
			public void run()
			{
				System.out.println("Running");
				String c = "Bangalore";
				StringBuilder url = new StringBuilder(baseURL);
				url.append(c + "&mode=xml");
				String fullURL = url.toString();
				try
				{
					System.out.println("Trying");
					URL website = new URL(fullURL);
					System.out.println("Went to URL");
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					XMLReader xr = sp.getXMLReader();
					HandlingXMLStuff work = new HandlingXMLStuff();
					xr.setContentHandler(work);
					xr.parse(new InputSource(website.openStream()));
					String information = work.getInfo();
					str.append(information);
					System.out.println(str);
					System.out.println("Got info");
					sendToPhone();
					handler.post(new Runnable()
					{
						public void run()
						{
							try
							{
								tvTemp.setText(str.toString()+"�");
							}
							catch(NumberFormatException NFE)
							{
								NFE.printStackTrace();
							}
						}
					});
				}
				catch(Exception e)
				{
					str.append("25");
					sendToPhone();
					e.printStackTrace();
				}
			}
		};
		t.start();

	}




	class CustomNotificationReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String packageName = intent.getStringExtra("package_name"); 
			tickerText = intent.getStringExtra("ticker_text");
			String type = intent.getStringExtra("type");

			if(packageName.equals(Packages.WHATSAPP))
			{
				if(type.equals("posted"))
				{
					packagesInfo.whatsappCount++;
				}
				else
				{
					packagesInfo.whatsappCount = 0;
				}
			}
			else if(packageName.equals(Packages.GMAIL))
			{
				if(type.equals("posted"))
				{
					packagesInfo.gmailCount++;
				}
				else
				{
					packagesInfo.gmailCount = 0;
				}
			}
			else if(packageName.equals(Packages.MISSEDCALL))
			{
				if(type.equals("posted"))
				{
					packagesInfo.missedCallCount++;
				}
				else
				{
					packagesInfo.missedCallCount = 0;
				}
			}
			else if(packageName.equals(Packages.MESSAGE))
			{
				if(type.equals("posted"))
				{
					packagesInfo.messageCount++;
				}
				else
				{
					packagesInfo.messageCount = 0;
				}
			}
			else if(packageName.equals(Packages.DURINGCALL))
			{
				//VIBRATE
			}

			String countInfo = "Whatsapp = "+packagesInfo.whatsappCount+
					"\nEmail = "+packagesInfo.gmailCount+
					"\nMessage = "+packagesInfo.messageCount+
					"\nMissed Call = "+packagesInfo.missedCallCount+"\n";

			String temp = type + "\n" +  packageName + "\n" + tickerText + "\n" + countInfo + "\n\n";
			System.out.println(temp);
			updateCount();
		}
		void updateCount()
		{
			tvTicker.setText(tickerText);
			tvGmail.setText(Integer.toString(packagesInfo.gmailCount));
			tvWhatsapp.setText(Integer.toString(packagesInfo.whatsappCount));
			tvMissedcall.setText(Integer.toString(packagesInfo.missedCallCount));
			tvHangouts.setText(Integer.toString(packagesInfo.messageCount));
			
			sendToPhone();
		}
	}

	void sendToPhone()
	{	
		
		
		try 
		{	
			Calendar c = Calendar.getInstance(); 
			int seconds = c.get(Calendar.SECOND);
			int minutes = c.get(Calendar.MINUTE);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			String toSend = "";
			toSend += "#"+packagesInfo.whatsappCount;
			toSend += "#";
			toSend += ""+packagesInfo.gmailCount;
			toSend += "#";
            toSend += ""+packagesInfo.missedCallCount;
            toSend += "#";
            toSend += ""+packagesInfo.messageCount;
            toSend += "#";
			toSend+=""+hours+"#";
			toSend+=""+minutes+"#";
			if(tickerText==null)
					tickerText="asfdsaf";
			toSend += tickerText+"#)";
			bt.sendData(toSend);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		/*try
		{
			
			System.out.println("hello world : "+seconds);
			String line1 = "1";
			line1 += dc.getText()+ " W"+Integer.toString(packagesInfo.whatsappCount) + " C" +
					Integer.toString(packagesInfo.missedCallCount) + " T"+str.toString()+"�C";
			bt.sendData(line1);
			if(tickerText == null || tickerText.equals("null"))
			{
				//tickerText = "";
			}
			if(tickerText.indexOf("Missed call") != -1)
			{
				tickerText = "Miss Call "+tickerText.substring(17, tickerText.length());
			}
			if(tickerText.indexOf("Message from") != -1)
			{
				tickerText = "Message "+tickerText.substring(13, tickerText.length());
			}
				bt.sendData("2"+tickerText);
			
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}*/

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			bt.closeBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
