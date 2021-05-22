package com.example.marketobserver3;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

public class ResultRecord
{
    private static int getNumberFromUpdatedTime(String updated)
    {
        return Integer.parseInt(updated.replaceAll("\\D+", ""));
    }
    public String station;
    public String system;
    public char pad;
    public float distance;
    public int demand;
    public int price;
    public String updated;
    public String suffix;

    public ResultRecord(Element element)
    {
        station = element.getElementsByClass("normal").text();
        system = element.getElementsByClass("uppercase").text();
        pad = element.getElementsByClass("minor").text().charAt(0);
        distance = new Float(element.child(3).attr("data-order"));
        demand = new Integer(element.child(4).attr("data-order"));
        price = new Integer(element.child(5).attr("data-order"));
        updated = element.child(7).html();

        if(updated.equals("now"))
        {
            suffix = "(+++)";
        }
        else if(updated.contains("sec") || updated.contains("minute "))
        {
            suffix = "(++)";
        }
        else if(updated.contains("day"))
        {
            suffix = "(---)";
        }
        else if(updated.contains("hours"))
        {
            suffix = "(--)";
        }
        else if(updated.contains("hour"))
        {
            suffix = "(-)";
        }
        else
        {
            int numberFromUpdated = getNumberFromUpdatedTime(updated);
            if(numberFromUpdated >= 30 && updated.contains("minutes"))
            {
                suffix = "(-)";
            }
            else if(numberFromUpdated < 30 && updated.contains("minutes"))
            {
                suffix = "(+)";
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return price + " Cr on " + station + " in " + system + suffix;
    }
}
