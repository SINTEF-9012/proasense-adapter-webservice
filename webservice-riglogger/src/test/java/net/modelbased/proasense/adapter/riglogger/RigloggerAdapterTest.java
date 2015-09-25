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

import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


public class RigloggerAdapterTest extends AbstractWebServiceAdapter {
    public final static Logger logger = Logger.getLogger(RigloggerAdapterTest.class);

    public RigloggerAdapterTest() {
        // Get specific adapter properties
        String S_WSDL_URL = adapterProperties.getProperty("proasense.adapter.webservice.wsdl.url");
        boolean B_KAFKA_PUBLISH = new Boolean(adapterProperties.getProperty("proasense.adapter.riglogger.kafka.publish")).booleanValue();
        int I_CONFIG_TIMEDELAY = new Integer(adapterProperties.getProperty("proasense.adapter.riglogger.config.timedelay")).intValue();
        String[] S_CONFIG_POINTS = adapterProperties.getProperty("proasense.adapter.riglogger.config.points").split(",");

        logger.debug("B_KAFKA_PUBLISH = " + B_KAFKA_PUBLISH);

        // Configure symbols
        List<PointConfig> pointConfigs = new ArrayList<PointConfig>();
        if ((S_CONFIG_POINTS.length % 3) == 0) {
            int i = 0;
            while (i < S_CONFIG_POINTS.length) {
                String point = S_CONFIG_POINTS[i];
                String sensorId = S_CONFIG_POINTS[i + 1];
                int pollInterval = new Integer(S_CONFIG_POINTS[i + 2]).intValue();
                pointConfigs.add(new PointConfig(point, sensorId, pollInterval));
                i = i + 3;
            }
        } else {
            logger.error("The 'proasense.adapter.riglogger.config.points' configuration parameter was not properly set.");
            System.exit(-1);
        }

        // Set initial start date (adjusted with timedelay)
        int subtractMinutes = 0 - I_CONFIG_TIMEDELAY;
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        startDate.add(Calendar.MINUTE, subtractMinutes);
        logger.debug("startDate = " + new DateTime(startDate).toString());

        // Run test threads using the generated test measurements
        for (PointConfig pc : pointConfigs) {
            new PointTestReaderKafkaWriterStream(pc, startDate, null, this.outputPort, B_KAFKA_PUBLISH);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new RigloggerAdapterTest();
    }

}
