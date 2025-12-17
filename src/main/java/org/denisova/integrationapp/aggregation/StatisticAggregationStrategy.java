package org.denisova.integrationapp.aggregation;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class StatisticAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Stats stats;

        if (oldExchange == null) {
            stats = new Stats();
            newExchange.getIn().setBody(stats);
            return newExchange;
        } else {
            stats = oldExchange.getIn().getBody(Stats.class);
        }

        String changeType = newExchange.getProperty("changeType", String.class);
        if ("INSERTED".equals(changeType)) {
            stats.inserted++;
        } else if ("UPDATED".equals(changeType)) {
            stats.updated++;
        } else if ("DEACTIVATED".equals(changeType)) {
            stats.deactivated++;
        }

        stats.totalProcessed++;

        return oldExchange;
    }

    public static class Stats {
        public int inserted = 0;
        public int updated = 0;
        public int deactivated = 0;
        public int totalProcessed = 0;

        public int getInserted() {
            return inserted;
        }

        public int getUpdated() {
            return updated;
        }

        public int getDeactivated() {
            return deactivated;
        }

        public int getTotalProcessed() {
            return totalProcessed;
        }
    }
}
