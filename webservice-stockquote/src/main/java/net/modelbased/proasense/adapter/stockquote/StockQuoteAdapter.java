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

import net.modelbased.proasense.adapter.webservice.AbstractWebServiceAdapter;

import net.webservicex.StockQuote;
import net.webservicex.StockQuoteSoap;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class StockQuoteAdapter extends AbstractWebServiceAdapter {
    public final static Logger logger = Logger.getLogger(StockQuoteAdapter.class);

    public StockQuoteAdapter() {
        // Get specific adapter properties
        String S_WSDL_URL = adapterProperties.getProperty("proasense.adapter.webservice.wsdl.url");
        String[] S_CONFIG_SYMBOLS = adapterProperties.getProperty("proasense.adapter.stock.config.symbols").split(",");

        // Configure symbols
        List<SymbolConfig> symbolConfigs = new ArrayList<SymbolConfig>();
        if ((S_CONFIG_SYMBOLS.length % 3) == 0) {

            int i = 0;
            while (i < S_CONFIG_SYMBOLS.length) {
                String symbol = S_CONFIG_SYMBOLS[i];
                String sensorId = S_CONFIG_SYMBOLS[i + 1];
                int pollInterval = new Integer(S_CONFIG_SYMBOLS[i + 2]).intValue();
                symbolConfigs.add(new SymbolConfig(symbol, sensorId, pollInterval));
                i = i + 3;
            }
        } else {
            logger.error("The 'proasense.adapter.stock.config.symbols' configuration parameter was not properly set.");
            System.exit(-1);
        }

        // Run threads polling the Web service
        try {
            StockQuote stockService = new StockQuote(new URL(S_WSDL_URL));
            logger.debug("WSDL = " + stockService.getWSDLDocumentLocation().toString());

            StockQuoteSoap stockSoap = stockService.getStockQuoteSoap();
            logger.debug("stockSoap = " + stockSoap);

            for (SymbolConfig sc : symbolConfigs) {
                new StockQuoteStream(sc, System.currentTimeMillis(), stockSoap, this.outputPort);
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new StockQuoteAdapter();
    }


}
