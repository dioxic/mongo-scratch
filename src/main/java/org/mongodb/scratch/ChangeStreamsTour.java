package org.mongodb.scratch;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.Document;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class ChangeStreamsTour {

    /**
     * Run this main method to see the output of this quick example.
     *
     * Requires the mongodb-crypt library in the class path and mongocryptd on the system path.
     *
     * @param args ignored args
     */
    public static void main(final String[] args) {


    }
}