package org.mongodb.scratch;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class AtlasConnection {


    public static void main(String[] args) {

        String user = "markbm";
        String password = "sadfs";

        MongoClient mongoClient = MongoClients.create("mongodb+srv://" + user + ":" + password + "@free-cluster.bj7ub.mongodb.net/?readPreference=primary");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb+srv://free-cluster.bj7ub.mongodb.net/"))
                .credential(MongoCredential.createScramSha256Credential(user, "admin", password.toCharArray()))
                .build();

        MongoClients.create(settings);

        MongoCollection<Document> collection = mongoClient
                .getDatabase("bt")
                .getCollection("civ")
                .withReadPreference(ReadPreference.secondaryPreferred());

        System.out.println(collection.find().limit(1).first());
    }
}

