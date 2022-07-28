import org.bson.*
import org.bson.codecs.BinaryCodec
import org.bson.codecs.DocumentCodec
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistries.fromCodecs
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.jsr310.LocalDateCodec
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.Decimal128
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun Document.toJsonPretty(): String {
    val jws = JsonWriterSettings.builder()
            .indent(true)
            .outputMode(JsonMode.RELAXED)
            .build()

    return toJson(jws)
}

fun Document.toJsonPretty(codecRegistry: CodecRegistry): String {
    val jws = JsonWriterSettings.builder()
            .indent(true)
            .outputMode(JsonMode.RELAXED)
            .build()

    return toJson(jws, DocumentCodec(codecRegistry))
}

fun String.toBson() = BsonString(this)

fun Int.toBson() = BsonInt32(this)

fun Long.toBson() = BsonInt64(this)

fun Double.toBson() = BsonDouble(this)

fun <T : BsonValue> List<T>.toBson() = BsonArray(this)

fun UUID.toBson() = BsonBinary(this)

fun Boolean.toBson() = BsonBoolean(this)

fun Date.toBson() = BsonDateTime(this.time)

fun LocalDateTime.toBson() = BsonDateTime(this.toInstant(ZoneOffset.UTC).toEpochMilli())

fun BigDecimal.toBson() = BsonDecimal128(Decimal128(this))

val BsonType.typeName: String
    get() =
        when (this) {
            BsonType.DOUBLE -> "double"
            BsonType.STRING -> "string"
            BsonType.DOCUMENT -> "object"
            BsonType.ARRAY -> "array"
            BsonType.BINARY -> "binData"
            BsonType.UNDEFINED -> "undefined"
            BsonType.OBJECT_ID -> "objectId"
            BsonType.BOOLEAN -> "bool"
            BsonType.DATE_TIME -> "date"
            BsonType.NULL -> "null"
            BsonType.REGULAR_EXPRESSION -> "regex"
            BsonType.DB_POINTER -> "dbPointer"
            BsonType.JAVASCRIPT -> "javascript"
            BsonType.SYMBOL -> "symbol"
            BsonType.JAVASCRIPT_WITH_SCOPE -> "javascriptWithScope"
            BsonType.INT32 -> "int"
            BsonType.TIMESTAMP -> "timestamp"
            BsonType.INT64 -> "long"
            BsonType.DECIMAL128 -> "decimal"
            BsonType.MIN_KEY -> "minKey"
            BsonType.MAX_KEY -> "maxKey"
            else -> throw IllegalArgumentException("$this doesn't have a type name")
        }

fun bsonDocOf(vararg pairs: Pair<String, BsonValue>) =
        if (pairs.isNotEmpty()) pairs.toMap(BsonDocument()) else BsonDocument()

fun BsonDocument.of(vararg pairs: Pair<String, BsonValue>) =
        if (pairs.isNotEmpty()) pairs.toMap(this) else this

fun docOf(vararg pairs: Pair<String, Any>) =
        if (pairs.isNotEmpty()) pairs.toMap(Document()) else Document()

fun Document.of(vararg pairs: Pair<String, Any>) =
        if (pairs.isNotEmpty()) pairs.toMap(this) else this