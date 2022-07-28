package org.mongodb.scratch.encryption

import com.mongodb.AutoEncryptionSettings
import com.mongodb.ClientEncryptionSettings
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.vault.DataKeyOptions
import com.mongodb.client.vault.ClientEncryption
import com.mongodb.client.vault.ClientEncryptions
import org.bson.BsonDocument
import java.security.SecureRandom
import java.util.*

/**
 * generates a new master key
 */
fun generateMasterKey(): ByteArray {
    val localMasterKey = ByteArray(96)
    SecureRandom().nextBytes(localMasterKey)
    return localMasterKey
}

/**
 * generates a new data key and returns the UUID of the new key
 */
fun generateDataKey(clientEncryption: ClientEncryption): UUID =
        clientEncryption.createDataKey("local", DataKeyOptions()).asUuid()

fun getKmsProviders(localMasterKey: ByteArray): Map<String, Map<String, Any>> = mapOf(
        "local" to mapOf(
                "key" to localMasterKey
        )
)

fun getClientEncryption(kmsProviders: Map<String, Map<String, Any>>,
                        keyVaultNamespace: String,
                        connString: ConnectionString): ClientEncryption {
    val clientEncryptionSettings = ClientEncryptionSettings.builder()
            .keyVaultMongoClientSettings(MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .build())
            .keyVaultNamespace(keyVaultNamespace)
            .kmsProviders(kmsProviders)
            .build()

    return ClientEncryptions.create(clientEncryptionSettings)
}

fun getAutoEncryptionSettings(kmsProviders: Map<String, Map<String, Any>>,
                              keyVaultNamespace: String,
                              schemaMap: Map<String, BsonDocument>): AutoEncryptionSettings =
        AutoEncryptionSettings.builder()
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .schemaMap(schemaMap)
                .build()