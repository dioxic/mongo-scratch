package org.mongodb.scratch.repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class DocumentRepository extends AbstractRepository<Document> {


    @Override
    protected MongoCollection<Document> getCollection() {
        return mongoClient
                .getDatabase("test")
                .getCollection("person");

    }
}
