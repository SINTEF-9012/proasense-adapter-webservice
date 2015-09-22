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
package net.modelbased.proasense.adapter.weather;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;

import net.webservicex.GlobalWeather;
import net.webservicex.GlobalWeatherSoap;

import java.io.StringReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class GlobalWeatherAdapter extends AbstractWebServiceAdapter {
    private String S_TOPIC;
    private String S_SENSORID;
    private String S_CITY;
    private String S_COUNTRY;

    public GlobalWeatherAdapter() {
        // Get common adapter properties
        this.S_TOPIC    = adapterProperties.getProperty("proasense.adapter.base.topic");
        this.S_SENSORID = adapterProperties.getProperty("proasense.adapter.base.sensorid");

        // Get specific adapter properties
        this.S_CITY     = adapterProperties.getProperty("proasense.adapter.weather.city");
        this.S_COUNTRY  = adapterProperties.getProperty("proasense.adapter.weather.country");
        int I_POLLTIME  = new Integer(adapterProperties.getProperty("proasense.adapter.weather.polltime")).intValue();

        // Get Web service
        GlobalWeather weatherService = new GlobalWeather();
        logger.debug("WSDL = " + weatherService.getWSDLDocumentLocation().toString());

        GlobalWeatherSoap weatherSoap = weatherService.getGlobalWeatherSoap();
        logger.debug("weatherSoap = " + weatherService);

        while (true) {
            try {
                // 1. Read data from Web service
                String result = weatherSoap.getWeather(S_CITY, S_COUNTRY);
                logger.debug("result = " + result.toString());

                // 2. Convert read data to simple event
                SimpleEvent event = convertToSimpleEvent(result);
                logger.debug("SimpleEvent = " + event.toString());

                // 3. Publish simple event to the output port
                this.outputPort.publishSimpleEvent(event);

                TimeUnit.MINUTES.sleep(I_POLLTIME);
            } catch (Exception e) {
                logger.debug(e.getClass().getName() + ": " + e.getMessage());
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

        // Add location
        String location = xPath.compile("/CurrentWeather/Location").evaluate(doc);
        logger.debug("location = " + location);
        ComplexValue value = new ComplexValue();
        value.setValue(location);
        value.setType(VariableType.STRING);
        properties.put("location", value);

        // Add time
        String[] dates = xPath.compile("/CurrentWeather/Time").evaluate(doc).split("/");
        String date = dates[0];
        logger.debug("date = " + date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM D, YYYY - hh:mm a z");
        logger.debug("parsed date = " + dateFormat.parse(date).toString());
//        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(date);
        value = new ComplexValue();
        value.setValue(date);
        value.setType(VariableType.STRING);
        properties.put("time", value);

        // Add wind
        String wind = xPath.compile("/CurrentWeather/Wind").evaluate(doc);
        logger.debug("wind = " + wind);
        value = new ComplexValue();
        value.setValue(wind);
        value.setType(VariableType.STRING);
        properties.put("wind", value);

        // Add visibility
        String visibility = xPath.compile("/CurrentWeather/Visibility").evaluate(doc);
        logger.debug("visibility = " + visibility);
        value = new ComplexValue();
        value.setValue(visibility);
        value.setType(VariableType.STRING);
        properties.put("visibility", value);

        // Add sky conditions
        String skyConditions = xPath.compile("/CurrentWeather/SkyConditions").evaluate(doc);
        logger.debug("skyConditions = " + skyConditions);
        value = new ComplexValue();
        value.setValue(skyConditions);
        value.setType(VariableType.STRING);
        properties.put("skyConditions", value);

        // Add temperature
        String temperature = xPath.compile("/CurrentWeather/Temperature").evaluate(doc);
        logger.debug("temperature = " + temperature);
        value = new ComplexValue();
        value.setValue(temperature);
        value.setType(VariableType.STRING);
        properties.put("temperature", value);

        // Add dew point
        String dewPoint = xPath.compile("/CurrentWeather/DewPoint").evaluate(doc);
        logger.debug("dewPoint = " + dewPoint);
        value = new ComplexValue();
        value.setValue(dewPoint);
        value.setType(VariableType.STRING);
        properties.put("dewPoint", value);

        // Add relative humidity
        String relativeHumidity = xPath.compile("/CurrentWeather/RelativeHumidity").evaluate(doc);
        logger.debug("relativeHumidity = " + relativeHumidity);
        value = new ComplexValue();
        value.setValue(relativeHumidity);
        value.setType(VariableType.STRING);
        properties.put("relativeHumidity", value);

        // Add pressure
        String pressure = xPath.compile("/CurrentWeather/Pressure").evaluate(doc);
        logger.debug("pressure = " + pressure);
        value = new ComplexValue();
        value.setValue(pressure);
        value.setType(VariableType.STRING);
        properties.put("pressure", value);

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
        new GlobalWeatherAdapter();
    }


}
