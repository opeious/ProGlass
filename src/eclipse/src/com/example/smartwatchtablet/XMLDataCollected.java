package com.example.smartwatchtablet;

/**
 * Created by Ishan Kothari on 13/12/13.
 */
public class XMLDataCollected {
    int temp = 0;
    String city= null,wind = null;

    public void setCity(String c)
    {
        city = c;
    }

    public void setTemperature(int t)
    {
        temp = t;
    }

    public void setWind(String w)
    {
        wind = w;
    }


    public String dataToString()
    {
        String data = temp+"";
        return data;
    }
}
