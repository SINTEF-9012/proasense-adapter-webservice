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

import net.modelbased.proasense.adapter.base.KafkaProducerOutput;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


public class SymbolKafkaWriter implements Runnable {
    public final static Logger logger = Logger.getLogger(SymbolKafkaWriter.class);

    private BlockingQueue<String> queue;
    private SymbolConfig symbolConfig;
    private long startTime;
    private KafkaProducerOutput outputPort;


    public SymbolKafkaWriter(BlockingQueue<String> queue, SymbolConfig symbolConfig, long startTime, KafkaProducerOutput outputPort) {
        this.queue = queue;
        this.symbolConfig = symbolConfig;
        this.startTime = startTime;
        this.outputPort = outputPort;
    }


    @Override
    public void run() {
        while (true) {
            try {
                // Read stock quote from queue
                String quote = queue.take();

                // Convert stock quote to simple event
                SimpleEvent event = convertToSimpleEvent(quote);

                // Publish simple event
                this.outputPort.publishSimpleEvent(event);
                logger.debug("simpleEvent = " + event.toString());
            }
            catch (Exception e) {
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
//        logger.debug("name = " + name);
        ComplexValue value = new ComplexValue();
        value.setValue(name);
        value.setType(VariableType.STRING);
        properties.put("name", value);

        // Add symbol
        String symbol = xPath.compile("/StockQuotes/Stock/Symbol").evaluate(doc);
//        logger.debug("symbol = " + symbol);
        value = new ComplexValue();
        value.setValue(symbol);
        value.setType(VariableType.STRING);
        properties.put("symbol", value);

        // Add last
        String last = xPath.compile("/StockQuotes/Stock/Last").evaluate(doc);
//        logger.debug("last = " + last);
        value = new ComplexValue();
        value.setValue(last);
        value.setType(VariableType.DOUBLE);
        properties.put("last", value);

        // Add date
        String date = xPath.compile("/StockQuotes/Stock/Date").evaluate(doc);
//        logger.debug("date = " + date);
        value = new ComplexValue();
        value.setValue(date);
        value.setType(VariableType.STRING);
        properties.put("date", value);

        // Add time
        String time = xPath.compile("/StockQuotes/Stock/Time").evaluate(doc);
//        logger.debug("time = " + time);
        value = new ComplexValue();
        value.setValue(time);
        value.setType(VariableType.STRING);
        properties.put("time", value);

        // Add change
        String change = xPath.compile("/StockQuotes/Stock/Change").evaluate(doc);
//        logger.debug("change = " + change);
        value = new ComplexValue();
        value.setValue(change.replace("+", ""));
        value.setType(VariableType.DOUBLE);
        properties.put("change", value);

        // Add percentage change
        String percentageChange = xPath.compile("/StockQuotes/Stock/PercentageChange").evaluate(doc);
//        logger.debug("percentageChange = " + percentageChange);
        value = new ComplexValue();
        value.setValue(percentageChange.replace("+", "").replace("%", ""));
        value.setType(VariableType.DOUBLE);
        properties.put("percentageChange", value);

        // Create simple event
        SimpleEvent event = new SimpleEvent();
        event.setSensorId(this.symbolConfig.getSensorId());
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

}
