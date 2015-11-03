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


public class PointConfig {
    private String point;
    private String sensorId;
    private String type;
    private int pollInterval;


    public PointConfig(String pointId, String sensorId, String type, int pollInterval) {
        this.point = pointId;
        this.sensorId = sensorId;
        this.type = type;
        this.pollInterval = pollInterval;
    }


    public String getPoint() {
        return this.point;
    }


    public String getSensorId() {
        return this.sensorId;
    }


    public String getType() { return this.type; }


    public int getPollInterval() {
        return this.pollInterval;
    }

}
