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

import com.mhwirth.riglogger.proasenseadapter.ArrayOfMeasurement;
import com.mhwirth.riglogger.proasenseadapter.Measurement;
import com.mhwirth.riglogger.proasenseadapter.Service1Soap;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class PointTestReader implements Runnable {
    public final static Logger logger = Logger.getLogger(PointTestReader.class);

    private BlockingQueue<Measurement> queue;
    private PointConfig pointConfig;
    private long startTime;
    private Service1Soap service1Soap;


    public PointTestReader(BlockingQueue<Measurement> queue, PointConfig pointConfig, long startTime, Service1Soap service1Soap) {
        this.queue = queue;
        this.pointConfig = pointConfig;
        this.startTime = startTime;
        this.service1Soap = service1Soap;
    }


    public void run() {
        logger.debug("PointTestReader thread started.");

        // Set initial end date (current time)
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Set initial start date (current time - pollInterval)
        int subtractMinutes = 0 - this.pointConfig.getPollInterval();
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        startDate.setTime(endDate.getTime());
        startDate.add(Calendar.MINUTE, subtractMinutes);

        while (true) {
            try {
                // Set XML dates
                XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate);
                XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate);

                // Start timer
                long startTime = System.currentTimeMillis() / 1000;

                // Read data from generate test measurements method
                List<Measurement> measurements = getGeneratedTestMeasurements(this.pointConfig.getPoint(), xmlStartDate, xmlEndDate);
                for (int i = 0; i < measurements.size(); i++) {
                    Measurement measurement = measurements.get(i);
                    queue.put(measurement);
                }

                // Update start date (last retrieval date)
                startDate.setTime(endDate.getTime());

                // Update end date (current time)
                endDate = new GregorianCalendar();

                // Check time
                long endTime = System.currentTimeMillis() / 1000;
                long elapsedTime = endTime - startTime;
                long waitTime = this.pointConfig.getPollInterval()*60 - elapsedTime;
                logger.debug("waitTime = " + waitTime);

                if (waitTime > 0)
                    TimeUnit.SECONDS.sleep(waitTime);
            }
            catch (DatatypeConfigurationException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
            catch (InterruptedException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }


    private List<Measurement> getGeneratedTestMeasurements(String point, XMLGregorianCalendar xmlStartDate, XMLGregorianCalendar xmlEndDate) {
        List<Measurement> result = new ArrayList<Measurement>();

        Measurement measurement;
        GregorianCalendar startDate = xmlStartDate.toGregorianCalendar();
        startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        GregorianCalendar endDate = xmlEndDate.toGregorianCalendar();
        endDate.setTimeZone(TimeZone.getTimeZone("GMT"));

        boolean is_generate = true;
        while (is_generate) {
            try {
                // Generate random measurement
                measurement = new Measurement();
                measurement.setPoint(point);
                Random random = new Random();
                Double value = random.nextDouble();
                measurement.setValue(value.toString());
                int wait = random.nextInt(5);
                startDate.add(Calendar.SECOND, wait);
                measurement.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate));
                result.add(measurement);

                // Check times
                if (startDate.getTimeInMillis() > endDate.getTimeInMillis()) {
                    is_generate = false;
                }
//                logger.debug("startDate = " + new DateTime(startDate).toString());
//                logger.debug("endDate   = " + new DateTime(endDate).toString());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }


        return result;
    }

}