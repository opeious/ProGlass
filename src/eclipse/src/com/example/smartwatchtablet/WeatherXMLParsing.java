package com.example.smartwatchtablet;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Ishan Kothari on 13/12/13.
 */
public class WeatherXMLParsing extends Activity implements View.OnClickListener {


    static final String baseURL = "http://api.openweathermap.org/data/2.5/weather?q=";
    TextView tv;
    EditText city;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view)
    {
        System.out.println("Hello");
        final StringBuffer str = new StringBuffer("");
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
                    System.out.println("Got info");
                }
                catch(Exception e)
                {
                    str.append("City Not found");
                    e.printStackTrace();
                }
            }
        };
        try
        {
            System.out.println("Joining");
            t.start();
         t.join();
        }
        catch(InterruptedException e)
        {
            System.out.println("Interrupted Exception");
            e.printStackTrace();
        }
        //finally
        //{

            System.out.println("Finally");
          //  try
            //{
              //  Thread.sleep(1000);
                tv.setText(str.toString());

            //}
           // catch (InterruptedException e)
            //{
              //  e.printStackTrace();
            //}
        //}
    }
}
