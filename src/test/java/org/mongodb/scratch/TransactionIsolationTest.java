package org.mongodb.scratch;

import com.mongodb.*;
import com.mongodb.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionIsolationTest {

    private final Logger LOG = LogManager.getLogger(this.getClass());
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;

    @BeforeEach
    public void before() {
        mongoClient = MongoClients.create();
        collection = mongoClient
                .getDatabase("test")
                .getCollection("lock");
    }

    @Test
    void isolationTest() throws InterruptedException, ExecutionException {
        collection.drop();
        Document initialDoc = new Document("_id", 1).append("count", 0);
        collection.insertOne(initialDoc);

        LOG.info("Initial state: {}", collection.find().first());

        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Future<Document>> futures = executor.invokeAll(
                List.of(lockIncrement, lockIncrement, lockCheck),
                1,
                TimeUnit.SECONDS
        );

        assertThat(futures.get(2).get())
                .extractingByKey("count")
                .as("Checking for dirty reads")
                .isEqualTo(0);

        assertThat(collection.find(eq("_id", 1)).first())
                .extractingByKey("count")
                .as("Checking final state")
                .isEqualTo(2);

        LOG.info("Final state: {}", collection.find().first());

        executor.shutdown();

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    Callable<Document> lockCheck = () -> {
        sleep(150);
        Document doc = collection.find(eq("_id", 1)).first();
        LOG.info("Lock state: {}", doc);
        return doc;
    };

    Callable<Document> lockIncrement = () -> {
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();


        try (ClientSession session = mongoClient.startSession()) {
            TransactionBody<Document> txnBody = () -> {
                MongoCollection<Document> collection = mongoClient
                        .getDatabase("test")
                        .getCollection("lock");

                LOG.info("TxBody begin");

                try {
                    sleep(100);
                    LOG.info("TxBody running operation");
                    Document doc = collection.findOneAndUpdate(
                            session,
                            eq("_id", 1),
                            inc("count", 1)
                    );
                    sleep(100);
                    LOG.info("Lock state: {}", doc);
                    LOG.info("TxBody returning");
                    return Objects.requireNonNull(doc);
                } catch (MongoCommandException e) {
                    LOG.error(e);
                    throw e;
                }
            };
            return session.withTransaction(txnBody, txnOptions);
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    };


}
