package com.example.marketobserver3;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebsiteConnectionManager
{
    public static class PostData
    {
        public String name;
        public String value;
        public PostData(String name, String value)
        {
            this.name = name;
            this.value = value;
        }
    }
    Document website;

   public void requestAndStore(String url, PostData...postedData) throws IOException
   {
       Connection connection = Jsoup.connect(url);
       for (PostData element : postedData)
       {
           connection.data(element.name, element.value);
       }
       website = connection.post();
   }
   public Elements getInaraTable()
   {
       if(website == null)
       {
           throw new IllegalStateException("Website have to be successfully initialized firstly");
       }
       return website.getElementsByTag("tr");
   }
   public String GetAttributeValueFromItsPart(String tag, String attributeValuePart)
   {
       if(website == null)
       {
           throw new IllegalStateException("Website have to be successfully initialized firstly");
       }
       return website.getElementsByAttributeValueContaining(tag, attributeValuePart).attr(tag);
   }
}
