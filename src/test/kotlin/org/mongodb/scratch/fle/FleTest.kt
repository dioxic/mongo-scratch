package org.mongodb.scratch.fle

import bsonDocOf
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import docOf
import org.assertj.core.api.Assertions.assertThat
import org.bson.*
import org.bson.BsonType.*
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.mongodb.scratch.encryption.*
import org.mongodb.scratch.encryption.EncryptionAlgorithm.DETERMINISTIC
import org.mongodb.scratch.encryption.EncryptionAlgorithm.RANDOM


class FleTest {

    private val kmsProviders = getKmsProviders(generateMasterKey())
    private val connString = ConnectionString("mongodb://localhost")
    private val keyVaultDb = "admin"
    private val keyVaultCollection = "keyVault"
    private val keyVaultNamespace = "$keyVaultDb.$keyVaultCollection"
    private val mcs: MongoClientSettings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(connString)
            .build()

    @Test
    fun generateDataKey() {
        val keyVault = MongoClients.create(mcs)
                .getDatabase(keyVaultDb)
                .getCollection(keyVaultCollection)
                .withDocumentClass(BsonDocument::class.java)

        val clientEncryption = getClientEncryption(kmsProviders, keyVaultNamespace, connString)
        val dataKeyId = generateDataKey(clientEncryption)

        val doc = keyVault
                .find(eq("_id", dataKeyId))
                .first()

        assertThat(doc).isNotNull

        println(doc)
    }

    @Test
    fun testEncryption() {
//        System.setProperty("javax.net.ssl.keyStore", "c:/tmp/cacerts")
//        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePwd)
        System.setProperty("javax.net.ssl.trustStore", "c:/tmp/cacerts")
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit")


        val clientEncryption = getClientEncryption(kmsProviders, keyVaultNamespace, connString)
        val dataKeyId = generateDataKey(clientEncryption)
        val dataKeyId2 = generateDataKey(clientEncryption)

        val dbName = "test"
        val collName = "coll"
        val ns = "$dbName.$collName"

        val schema = schemaField(dataKeyId, bsonDocOf(
                "ssn" to encryptedField(INT32, DETERMINISTIC),
                "bloodType" to encryptedField(STRING, RANDOM),
                "medicalRecords" to encryptedField(ARRAY, RANDOM),
                "insurance" to schemaField(dataKeyId2, bsonDocOf(
                        "policyNumber" to encryptedField(INT32, DETERMINISTIC)
                ))
        ))

        val schemaMap = mapOf(ns to schema)

        val client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(connString)
                .autoEncryptionSettings(getAutoEncryptionSettings(kmsProviders, keyVaultNamespace, schemaMap))
                .build()
        )

        val collection = client
                .getDatabase(dbName)
                .getCollection(collName)

        collection.drop()

        val sampleDoc = docOf(
                "ssn" to 1234556,
                "bloodType" to "AB-",
                "medicalRecords" to listOf(
                        docOf("weight" to "180"),
                        docOf("bloodPressure" to "120/80")
                ),
                "insurance" to docOf(
                        "policyNumber" to 123456,
                        "provider" to "MongoCare"
                )
        )

        collection.insertOne(sampleDoc.apply { put("_id", ObjectId.get()) })
        collection.insertOne(sampleDoc.apply { put("_id", ObjectId.get()) })
        collection.insertOne(sampleDoc.apply { put("_id", ObjectId.get()) })

        val resultsDoc = collection
                .find(eq(sampleDoc.getObjectId("_id")))
                .first()

        println("query result: $resultsDoc")

        assertThat(resultsDoc).isEqualTo(sampleDoc)

    }

}