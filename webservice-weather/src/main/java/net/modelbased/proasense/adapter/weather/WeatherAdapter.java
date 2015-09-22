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

import com.cdyne.ws.weatherws.ArrayOfForecast;
import com.cdyne.ws.weatherws.ArrayOfWeatherDescription;
import com.cdyne.ws.weatherws.Forecast;
import com.cdyne.ws.weatherws.ForecastReturn;
import com.cdyne.ws.weatherws.Weather;
import com.cdyne.ws.weatherws.WeatherReturn;
import com.cdyne.ws.weatherws.WeatherSoap;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;
import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WeatherAdapter extends AbstractWebServiceAdapter {
    private String S_TOPIC;
    private String S_SENSORID;


    public WeatherAdapter() {
        // Get common adapter properties
        this.S_TOPIC      = adapterProperties.getProperty("proasense.adapter.base.topic");
        this.S_SENSORID   = adapterProperties.getProperty("proasense.adapter.base.sensorid");

        // Get specific adapter properties
        String S_ZIP        = adapterProperties.getProperty("proasense.adapter.weather.zip");
        int I_POLLTIME      = new Integer(adapterProperties.getProperty("proasense.adapter.weather.polltime")).intValue();

        // Get Web service
        Weather weatherService = new Weather();
        logger.debug("WSDL = " + weatherService.getWSDLDocumentLocation().toString());

        WeatherSoap weatherSoap = weatherService.getWeatherSoap();
        logger.debug("weatherSoap = " + weatherService);

        while (true) {
            try {
                // 1. Read data from Web service
                WeatherReturn result = weatherSoap.getCityWeatherByZIP("94025");
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


    private SimpleEvent convertToSimpleEvent(WeatherReturn input) {
        // Define event properties and add complex values
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();

        // Add city
        String city = input.getCity();
        logger.debug("city = " + city);
        ComplexValue value = new ComplexValue();
        value.setValue(city);
        value.setType(VariableType.STRING);
        properties.put("city", value);

        // Add temperature
        String temperature = input.getTemperature();
        logger.debug("temperature = " + temperature);
        value = new ComplexValue();
        value.setValue(temperature);
        value.setType(VariableType.DOUBLE);
        properties.put("temperature", value);

        // Create simple event
        SimpleEvent event = new SimpleEvent();
        event.setSensorId(this.S_SENSORID);
        event.setTimestamp(System.currentTimeMillis());
        event.setEventProperties(properties);

        return event;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new WeatherAdapter();
    }


}
