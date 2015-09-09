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
package net.modelbased.proasense.adapter.wsklima;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;
import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class WSKlimaAdapter extends AbstractWebServiceAdapter {


    public WSKlimaAdapter() {
        // Write code here that:
        // 1. Reads data from the input port (not provided in the base net.modelbased.proasense.adapter.base)
        String input = "{\"variable_type\":\"1002311\",\"value\":138.2346,\"variable_timestamp\":\"2014-02-14T07:01:24.133Z\"}";
        // 2. Converts read data to simple events
        SimpleEvent event = convertToSimpleEvent(input);
        // 3. Publishes simple events to the output port
        this.outputPort.publishSimpleEvent(event);
    }


    private SimpleEvent convertToSimpleEvent(String input) {
        SimpleEvent event = new SimpleEvent();

        return event;
    }


}
