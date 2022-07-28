package org.mongodb.scratch.encryption

import bsonDocOf
import org.bson.BsonDocument
import org.bson.BsonType
import toBson
import typeName
import java.util.*

enum class EncryptionAlgorithm(val algorithm: String) {
    DETERMINISTIC("AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic"),
    RANDOM("AEAD_AES_256_CBC_HMAC_SHA_512-Random")
}

fun encryptedField(type: BsonType, algorithm: EncryptionAlgorithm, keyId: UUID): BsonDocument {
    val field = encryptedField(type, algorithm)
    field.getDocument("encrypt")["keyId"] = listOf(keyId.toBson()).toBson()
    return field
}

fun encryptedField(type: BsonType, algorithm: EncryptionAlgorithm) =
        bsonDocOf(
                "encrypt" to bsonDocOf(
                        "bsonType" to type.typeName.toBson(),
                        "algorithm" to algorithm.algorithm.toBson()
                )
        )

fun schemaField(keyId: UUID, properties: BsonDocument): BsonDocument {
    val schema = schemaField(properties)
    schema["encryptMetadata"] = bsonDocOf(
            "keyId" to listOf(keyId.toBson()).toBson()
    )
    return schema

}

fun schemaField(properties: BsonDocument) =
        bsonDocOf(
                "bsonType" to BsonType.DOCUMENT.typeName.toBson(),
                "properties" to properties
        )