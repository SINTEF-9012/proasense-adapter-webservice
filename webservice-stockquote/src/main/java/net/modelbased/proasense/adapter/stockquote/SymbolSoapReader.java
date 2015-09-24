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
package net.modelbased.proasense.adapter.stockquote;

import net.webservicex.StockQuoteSoap;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class SymbolSoapReader implements Runnable {
    public final static Logger logger = Logger.getLogger(SymbolSoapReader.class);

    private BlockingQueue<String> queue;
    private StockQuoteSoap stockSoap;
    private SymbolConfig symbolConfig;
    private long startTime;


    public SymbolSoapReader(BlockingQueue<String> queue, SymbolConfig symbolConfig, long startTime, StockQuoteSoap stockSoap) {
        this.queue = queue;
        this.symbolConfig = symbolConfig;
        this.startTime = startTime;
        this.stockSoap = stockSoap;
    }


    @Override
    public void run() {
        while (true) {
            try {
                // Read data from Web service
                String quote = stockSoap.getQuote(symbolConfig.getSymbol());
                queue.put(quote);
                logger.debug("quote = " + quote);

                // Wait poll interval (minutes)
                TimeUnit.MINUTES.sleep(symbolConfig.getPollInterval());
            }
            catch (InterruptedException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

}
