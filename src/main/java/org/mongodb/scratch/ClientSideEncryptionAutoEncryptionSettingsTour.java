package org.mongodb.scratch;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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

public class ClientSideEncryptionAutoEncryptionSettingsTour {

    /**
     * Run this main method to see the output of this quick example.
     *
     * Requires the mongodb-crypt library in the class path and mongocryptd on the system path.
     *
     * @param args ignored args
     */
    public static void main(final String[] args) {

        // This would have to be the same master key as was used to create the encryption key
        final byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>() {{
            put("local", new HashMap<>() {{
                put("key", localMasterKey);
            }});
        }};

        String keyVaultNamespace = "admin.datakeys";
        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://localhost"))
                        .build())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();

        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
        BsonBinary dataKeyId = clientEncryption.createDataKey("local", new DataKeyOptions());
        final String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());

        final String dbName = "test";
        final String collName = "coll";
        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .schemaMap(new HashMap<>() {{
                    put(dbName + "." + collName,
                            // Need a schema that references the new data key
                            BsonDocument.parse("{"
                                    + "  properties: {"
                                    + "    encryptedField: {"
                                    + "      encrypt: {"
                                    + "        keyId: [{"
                                    + "          \"$binary\": {"
                                    + "            \"base64\": \"" + base64DataKeyId + "\","
                                    + "            \"subType\": \"04\""
                                    + "          }"
                                    + "        }],"
                                    + "        bsonType: \"string\","
                                    + "        algorithm: \"AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic\""
                                    + "      }"
                                    + "    }"
                                    + "  },"
                                    + "  \"bsonType\": \"object\""
                                    + "}"));
                }}).build();

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .autoEncryptionSettings(autoEncryptionSettings)
                .build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoCollection<Document> collection = mongoClient.getDatabase("test").getCollection("coll");
        collection.drop(); // Clear old data

        collection.insertOne(new Document("encryptedField", "123456789"));

        System.out.println(collection.find(eq("encryptedField", "123456789"))
                .first()
                .toJson());

        // release resources
        mongoClient.close();
    }
}