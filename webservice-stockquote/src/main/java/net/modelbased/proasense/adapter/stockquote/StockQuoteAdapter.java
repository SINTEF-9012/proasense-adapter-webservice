/**
 * Copyright (C) 2014-2015 SINTEF
 *
 *     Brian Elvesæter <brian.elvesater@sintef.no>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.modelbased.proasense.adapter.stockquote;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;
import net.webservicex.StockQuote;
import net.webservicex.StockQuoteSoap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class StockQuoteAdapter extends AbstractWebServiceAdapter {
    public final static Logger logger = Logger.getLogger(StockQuoteAdapter.class);

    private String S_TOPIC;
    private String S_SENSORID;
    private String S_QUERY_TICKERS;

    public StockQuoteAdapter() {
        // Get common adapter properties
        this.S_TOPIC    = adapterProperties.getProperty("proasense.adapter.base.topic");
        this.S_SENSORID = adapterProperties.getProperty("proasense.adapter.base.sensorid");

        // Get specific adapter properties
        int I_POLL_INTERVAL     = new Integer(adapterProperties.getProperty("proasense.adapter.stock.poll.interval")).intValue();
        this.S_QUERY_TICKERS    = adapterProperties.getProperty("proasense.adapter.stock.query.tickers");

        // Get Web service
        StockQuote stockService = new StockQuote();
        logger.debug("WSDL = " + stockService.getWSDLDocumentLocation().toString());

        StockQuoteSoap stockSoap = stockService.getStockQuoteSoap();
        logger.debug("stockSoap = " + stockSoap);

        while (true) {
            try {
                // 1. Read data from Web service
                String result = stockSoap.getQuote("MSFT");
                logger.debug("result = " + result.toString());

                // 2. Convert read data to simple event
                SimpleEvent event = convertToSimpleEvent(result);
                logger.debug("SimpleEvent = " + event.toString());

                // 3. Publish simple event to the output port
                this.outputPort.publishSimpleEvent(event);

                TimeUnit.MINUTES.sleep(I_POLL_INTERVAL);
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }


    private SimpleEvent convertToSimpleEvent(String input) throws Exception {
        // Convert XML input to DOM object
        Document doc = loadXMLFromString(input);

        // Use XPath to parse XML document
        XPath xPath = XPathFactory.newInstance().newXPath();

        // Define event properties and add complex values
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();

        // Add name
        String name = xPath.compile("/StockQuotes/Stock/Name").evaluate(doc);
        logger.debug("name = " + name);
        ComplexValue value = new ComplexValue();
        value.setValue(name);
        value.setType(VariableType.STRING);
        properties.put("name", value);

        // Add symbol
        String symbol = xPath.compile("/StockQuotes/Stock/Symbol").evaluate(doc);
        logger.debug("symbol = " + symbol);
        value = new ComplexValue();
        value.setValue(symbol);
        value.setType(VariableType.STRING);
        properties.put("symbol", value);

        // Add last
        String last = xPath.compile("/StockQuotes/Stock/Last").evaluate(doc);
        logger.debug("last = " + last);
        value = new ComplexValue();
        value.setValue(last);
        value.setType(VariableType.DOUBLE);
        properties.put("last", value);

        // Add date
        String date = xPath.compile("/StockQuotes/Stock/Date").evaluate(doc);
        logger.debug("date = " + date);
        value = new ComplexValue();
        value.setValue(date);
        value.setType(VariableType.STRING);
        properties.put("date", value);

        // Add time
        String time = xPath.compile("/StockQuotes/Stock/Time").evaluate(doc);
        logger.debug("time = " + time);
        value = new ComplexValue();
        value.setValue(time);
        value.setType(VariableType.STRING);
        properties.put("time", value);

        // Add change
        String change = xPath.compile("/StockQuotes/Stock/Change").evaluate(doc);
        logger.debug("change = " + change);
        value = new ComplexValue();
        value.setValue(change);
        value.setType(VariableType.DOUBLE);
        properties.put("change", value);

        // Add percentage change
        String percentageChange = xPath.compile("/StockQuotes/Stock/PercentageChange").evaluate(doc);
        logger.debug("percentageChange = " + percentageChange);
        value = new ComplexValue();
        value.setValue(percentageChange);
        value.setType(VariableType.DOUBLE);
        properties.put("percentageChange", value);

        // Create simple event
        SimpleEvent event = new SimpleEvent();
        event.setSensorId(this.S_SENSORID);
        event.setTimestamp(System.currentTimeMillis());
        event.setEventProperties(properties);

        return event;
    }


    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new StockQuoteAdapter();
    }


}
