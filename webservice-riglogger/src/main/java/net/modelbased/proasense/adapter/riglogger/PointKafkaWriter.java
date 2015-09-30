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
package net.modelbased.proasense.adapter.riglogger;

import com.mhwirth.riglogger.proasenseadapter.Measurement;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;
import net.modelbased.proasense.adapter.base.KafkaProducerOutput;
import org.apache.log4j.Logger;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class PointKafkaWriter implements Runnable {
    public final static Logger logger = Logger.getLogger(PointKafkaWriter.class);

    private BlockingQueue<Measurement> queue;
    private PointConfig pointConfig;
    private long startTime;
    private KafkaProducerOutput outputPort;

    long lastTimestamp;
    private boolean kafkaPublish;

    public PointKafkaWriter(BlockingQueue<Measurement> queue, PointConfig pointConfig, GregorianCalendar startDate, KafkaProducerOutput outputPort, boolean kafkaPublish) {
        this.queue = queue;
        this.pointConfig = pointConfig;
        this.startTime = startDate.getTimeInMillis();
        this.outputPort = outputPort;

        this.lastTimestamp = this.startTime;
        this.kafkaPublish = kafkaPublish;
    }


    public void run() {
        while (true) {
            try {
                // Read measurement from queue
                Measurement measurement = queue.take();

                // Convert measurement to simple event
                SimpleEvent event = convertToSimpleEvent(measurement);

                // Check timestamp
                long eventTimestamp = event.getTimestamp();
                long timeDiff = eventTimestamp - this.lastTimestamp;
                if (timeDiff > 0) {
                    TimeUnit.MILLISECONDS.sleep(timeDiff);
                }

                // Publish simple event
                if (this.kafkaPublish) {
                    this.outputPort.publishSimpleEvent(event);
                }
                logger.debug("simpleEvent = " + event.toString());

                // Update timestamps
                this.lastTimestamp = eventTimestamp;
            }
            catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }


    private SimpleEvent convertToSimpleEvent(Measurement measurement) {
        // Define event properties and add complex values
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();

        // Add value
        ComplexValue complexValue = new ComplexValue();
        String value = String.valueOf(new Float(measurement.getValue().toString()));
        complexValue.setValue(value);
        complexValue.setType(VariableType.DOUBLE);
        properties.put("value", complexValue);

        SimpleEvent event = new SimpleEvent();
        event.setTimestamp(convertXMLDateToMillis(measurement.getTimeStamp()));
        event.setSensorId(this.pointConfig.getSensorId());
        event.setEventProperties(properties);

        return event;
    }


    private long convertXMLDateToMillis(XMLGregorianCalendar xmlDate) {
        return xmlDate.toGregorianCalendar().getTimeInMillis();
    }


}