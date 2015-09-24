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


import net.modelbased.proasense.adapter.base.KafkaProducerOutput;
import net.webservicex.StockQuoteSoap;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockQuoteStream {

    public StockQuoteStream(SymbolConfig symbolConfig, long startTime, StockQuoteSoap stockSoap, KafkaProducerOutput outputPort) {
        // Blocking queue for multi-threaded application
        int NO_BLOCKINGQUEUE_SIZE = 1000000;
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(NO_BLOCKINGQUEUE_SIZE);

        // Total number of threads
        int NO_TOTAL_THREADS = 2;

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        // Create thread for sensor reader
        workers.add(new SymbolSoapReader(queue, symbolConfig, startTime, stockSoap));

        // Create thread for sensor writer
        workers.add(new SymbolKafkaWriter(queue, symbolConfig, startTime, outputPort));

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
