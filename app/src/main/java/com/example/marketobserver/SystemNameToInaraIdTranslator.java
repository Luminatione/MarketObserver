package com.example.marketobserver;

import java.io.IOException;

public class SystemNameToInaraIdTranslator
{
    private static final String server = "https://inara.cz/galaxy-commodity/";
    private static final String systemPostAttributeName = "searchcommoditysystem";
    private static final String systemIDOutputName = "refid2=";
    private static final String commodityPostAttributeName = "searchcommodity";
    private static final String commodityID = "144";
    private static final String attributeName = "href";
    public static String translate(final String systemName) throws IOException
    {
        final String[] hrefValue = new String[1];
        WebsiteConnectionManager connectionManager = new WebsiteConnectionManager();
        connectionManager.requestAndStore(server,
                new WebsiteConnectionManager.PostData(commodityPostAttributeName, commodityID),
                new WebsiteConnectionManager.PostData(systemPostAttributeName, systemName));
        hrefValue[0] = connectionManager.GetAttributeValueFromItsPart(attributeName, systemIDOutputName);
        String[] parameters = hrefValue[0].split("&");
        return parameters[parameters.length-1].substring(systemIDOutputName.length());
    }

}
