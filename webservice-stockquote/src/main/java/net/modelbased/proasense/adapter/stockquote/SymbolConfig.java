/**
 * Copyright (C) 2014-2015 SINTEF
 *
 *     Brian Elves�ter <brian.elvesater@sintef.no>
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


public class SymbolConfig {
    private String symbol;
    private String sensorId;
    private int pollInterval;


    public SymbolConfig(String symbol, String sensorId, int pollInterval) {
        this.symbol = symbol;
        this.sensorId = sensorId;
        this.pollInterval = pollInterval;
    }


    public String getSymbol() {
        return this.symbol;
    }


    public String getSensorId() {
        return this.sensorId;
    }


    public int getPollInterval() {
        return this.pollInterval;
    }

}
