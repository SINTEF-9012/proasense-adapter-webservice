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

import com.mhwirth.riglogger.proasenseadapter.Service1;
import com.mhwirth.riglogger.proasenseadapter.Service1Soap;
import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


public class RigloggerAdapterTest extends AbstractWebServiceAdapter {
    public final static Logger logger = Logger.getLogger(RigloggerAdapterTest.class);

    public RigloggerAdapterTest() {
        // Get specific adapter properties
        String S_WSDL_URL = adapterProperties.getProperty("proasense.adapter.webservice.wsdl.url");
        String[] S_CONFIG_POINTS = adapterProperties.getProperty("proasense.adapter.riglogger.config.points").split(",");

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

        // Set current date as start date
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        logger.debug("startDate = " + new DateTime(startDate).toString());

        // Run test threads using the genereated test measurements
        for (PointConfig pc : pointConfigs) {
            new RigloggerStreamTest(pc, startDate.getTimeInMillis(), null, this.outputPort);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new RigloggerAdapterTest();
    }

}
