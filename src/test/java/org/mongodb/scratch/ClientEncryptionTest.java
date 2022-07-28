package org.mongodb.scratch;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ClientEncryptionTest {

    private static Map<String, Map<String, Object>> kmsProviders;

    @BeforeAll
    static void before() {
        byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);
        kmsProviders = new HashMap<>() {{
            put("local", new HashMap<>() {{
                put("key", localMasterKey);
            }});
        }};

        String keyVaultNamespace = "admin.datakeys";
        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                        .build())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();

        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
        BsonBinary dataKeyId = clientEncryption.createDataKey("local", new DataKeyOptions());
        final String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());
    }

    void loadKey() {
    }

    @Test
    void encryption() {
//        String connectionString = "mongodb://localhost:27017";
//        String keyVaultNamespace = "admin.datakeys";
//
//        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
//                .keyVaultNamespace(keyVaultNamespace)
//                .kmsProviders(kmsProviders)
//                .build();
//
//        MongoClientSettings clientSettings = MongoClientSettings.builder()
//                .autoEncryptionSettings(autoEncryptionSettings)
//                .build();
//
//        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
//        BsonBinary dataKeyId = clientEncryption.createDataKey("local", new DataKeyOptions());
//        System.out.println("DataKeyId [UUID]: " + dataKeyId.asUuid());
//
//        String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());
//        System.out.println("DataKeyId [base64]: " + base64DataKeyId);
    }


}
