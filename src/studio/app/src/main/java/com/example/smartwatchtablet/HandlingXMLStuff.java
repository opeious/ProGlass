package com.example.smartwatchtablet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class HandlingXMLStuff extends DefaultHandler
{
    XMLDataCollected info = new XMLDataCollected();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(localName.equals("city"))
        {
            String city = attributes.getValue("name");
            info.setCity(city);
        }
        else if(localName.equals("temperature"))
        {
            String t = attributes.getValue("min");
            int temp = (int)Double.parseDouble(t);
            temp = temp - 273;
            info.setTemperature(temp);
        }
        if(localName.equals("speed"))
        {
            String wind = attributes.getValue("name");
            info.setWind(wind);
        }

    }

    public String getInfo()
    {
        return info.dataToString();
    }
}
