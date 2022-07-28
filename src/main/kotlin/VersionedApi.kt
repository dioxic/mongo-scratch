import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document

fun main(args: Array<String>) {

    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .strict(true)
        .deprecationErrors(true)
        .build()

    val settings = MongoClientSettings.builder()
//        .applyConnectionString(ConnectionString("mongodb+srv://charles:tJX3xNh4Fx8XmR0b@cluster0.bj7ub.mongodb.net"))
        .applyConnectionString(ConnectionString("mongodb+srv://Charlie:sMKYFNH9chFodNSG@cluster1.cefxq.mongodb.net/Address-Book"))
        .serverApi(serverApi)
        .build()

    val client = MongoClients.create(settings)

    val collection = client
        .getDatabase("Address-Book")
        .getCollection("markTest")


    collection.insertOne(Document("name", "Halibut"))
    collection.updateOne(Filters.eq("name", "Halibut"), Updates.set("name", "Fish"))

    collection.find().first()

    try {
        // non-versioned command
        client
            .getDatabase("test")
            .runCommand(Document("serverStatus", 1))
    } catch (e: Throwable) {
        println(e.message)
    }

}
