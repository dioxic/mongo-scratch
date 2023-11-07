package org.mongodb.scratch;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.*;
import org.bson.codecs.DocumentCodec;
import org.bson.types.ObjectId;

public class RawBsonSizeSize {

    public static void main(String[] args) {
        MongoClient client = MongoClients.create();
        MongoCollection<RawBsonDocument> collection = client.getDatabase("test")
                .getCollection("myCollection", RawBsonDocument.class);

        // starting off with a basic Doc
        Document doc = new Document()
                .append( "_id", ObjectId.get())
                .append( "name", "alice");

        // converting to rawbson
        RawBsonDocument rawDoc = new RawBsonDocument(doc, new DocumentCodec());

        System.out.println("Size: " + rawDoc.getByteBuffer().limit());

        // inserting using the raw bson doc (because we don't want the driver converting the doc to raw format a 2nd time)
        collection.insertOne(rawDoc);

    }
}
