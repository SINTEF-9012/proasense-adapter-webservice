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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class PointSoapReader implements Runnable {
    public final static Logger logger = Logger.getLogger(PointSoapReader.class);

    private BlockingQueue<Measurement> queue;
    private PointConfig pointConfig;
    private GregorianCalendar startDate;
    private Service1Soap service1Soap;


    public PointSoapReader(BlockingQueue<Measurement> queue, PointConfig pointConfig, GregorianCalendar startDate, Service1Soap service1Soap) {
        this.queue = queue;
        this.pointConfig = pointConfig;
        this.startDate = startDate;
        this.service1Soap = service1Soap;
    }


    public void run() {
        // Set initial polling end date (start date)
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        endDate.setTime(startDate.getTime());

        // Set initial polling start date (start date - poll interval)
        int subtractMinutes = 0 - this.pointConfig.getPollInterval();
        this.startDate.add(Calendar.MINUTE, subtractMinutes);

        while (true) {
            try {
                // Set XML dates
                XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate);
                XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate);

                // Start timer
                long startTime = System.currentTimeMillis();

                // Read data from Web service
                ArrayOfMeasurement measurementArray = service1Soap.getArchivedValues(this.pointConfig.getPoint(), xmlStartDate, xmlEndDate);
                for (int i = 0; i < measurementArray.getMeasurement().size(); i++) {
                    Measurement measurement = measurementArray.getMeasurement().get(i);
                    queue.put(measurement);
                }

                // Update start date (last retrieval date is the end date)
                startDate.setTime(endDate.getTime());

                // Update end date (add new poll interval)
                endDate.add(Calendar.MINUTE, this.pointConfig.getPollInterval());

                // Check time
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                long waitTime = this.pointConfig.getPollInterval()*60*1000 - elapsedTime;
                logger.debug("waitTime = " + waitTime);

                // Wait the remaining milliseconds of the poll interval
                if (waitTime > 0)
                    TimeUnit.MILLISECONDS.sleep(waitTime);
            }
            catch (DatatypeConfigurationException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
            catch (InterruptedException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

}