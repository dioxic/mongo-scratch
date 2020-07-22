package org.mongodb.scratch.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class DocumentRepository extends AbstractRepository<Document> {


    @Override
    protected MongoCollection<Document> getCollection() {
        return mongoClient
                .getDatabase("test")
                .getCollection("person");
    }
}
