package com.courierplus.mobile.Tracking;


import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandlerDetails  extends DefaultHandler {
		private String currentElement = null; //current element name
		public String DecodedHeader = "";
		public String DecodedDetails = "";
		public String DecodedXML = "";
		public String ResponseCode = "";
		public String ResponseMessage = "";



		public void startElement(String uri,String localName,String qName,Attributes attributes) {
			currentElement = localName;

			if( localName.equalsIgnoreCase("POD_Details") ) {
				DecodedXML = "";
			}
			else if( localName.equalsIgnoreCase("Scan_Details") ) {
				DecodedXML = "";
			}
			else if( localName.equalsIgnoreCase("ResponseCode") ) {
				currentElement = "ResponseCode";
			}
			else if( localName.equalsIgnoreCase("ResponseMessage") ) {
				currentElement = "ResponseMessage";
			}
		}
		public void characters(char[] ch, int start, int length) {
			String value = new String( ch , start , length );

			DecodedXML = DecodedXML + currentElement + " : " + "#" + value  + "#";
			//"#"
			if( currentElement.equalsIgnoreCase("ResponseCode") ) {
				ResponseCode = value;
			}
			else if( currentElement.equalsIgnoreCase("ResponseMessage") ) {
				ResponseMessage = value;
			}
		}

		public void endElement(String uri, String localName, String qName){
			if(localName.equalsIgnoreCase("POD_Details") ) {
				DecodedHeader = DecodedXML;
			}
			if(localName.equalsIgnoreCase("Scan_Details") ) {
				DecodedDetails = DecodedXML;
			}

		}

	}
