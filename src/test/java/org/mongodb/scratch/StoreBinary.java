package org.mongodb.scratch;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreBinary {

    private static MongoCollection<Document> collection;

    @BeforeAll
    static void before() {
        collection = MongoClients.create()
                .getDatabase("test")
                .getCollection("testCollection");
    }

    @BeforeEach
    void beforeEach() {
        collection.drop();
    }

    @Test
    public void save() {

        Base64.Encoder encoder = Base64.getEncoder();

        // raw XML as a string
        String xml = "<tag><subtag>contents</subtag></tag>";
//        String xml = "<tag><subtag>£££££££££££££££££££££££££££££££££££££££</subtag></tag>";
//        String xml = "<tag>\u03FF</tag>".repeat(10);
//        String xml = "<tag>f</tag>".repeat(10);
        byte[] binaryXml = xml.getBytes(StandardCharsets.UTF_8);
        String base64Xml = Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)); // "PHRhZz48c3VidGFnPmNvbnRlbnRzPC9zdWJ0YWc+PC90YWc+"
        byte[] binaryBase64Xml =  base64Xml.getBytes(StandardCharsets.UTF_8);

        System.out.println(xml);

        Document document = new Document();
        document.put("_id", 1234);
        document.put("xml", xml);
        document.put("base64Xml", base64Xml);
        document.put("binaryXml", new Binary(binaryXml));
        document.put("binaryBase64Xml", new Binary(binaryBase64Xml));

        collection.insertOne(document);

        document = collection.find(eq("_id", 1234)).first();

        assertThat(document).isNotNull();
        assertThat(document.getString("xml")).isEqualTo(xml);
        assertThat(document.getString("base64Xml")).isEqualTo(base64Xml);
        assertThat(document.get("binaryXml", Binary.class).getData()).isEqualTo(binaryXml);

    }

    @Test
    public void find() {
        String base64Xml = "PHRhZz48c3VidGFnPmNvbnRlbnRzPC9zdWJ0YWc+PC90YWc+";
        String xml = new String(Base64.getDecoder().decode(base64Xml)); // <tag><subtag>contents</subtag></tag>

        // create MongoDB document
        Document document = new Document();
        document.put("_id", 1234);
        document.put("xml", xml);

        // insert document
        collection.insertOne(document);

        // find document
        document = collection.find(eq("_id", 1234)).first();

        // check we found a document
        assertThat(document).isNotNull();

        // get the xml field
        String xmlField = document.getString("xml");

        // encode the xml to base64
        String b64EncodedXml = Base64.getEncoder().encodeToString(xmlField.getBytes());

        assertThat(xmlField).isEqualTo(xml);
        assertThat(b64EncodedXml).isEqualTo(base64Xml);
    }

    public void something() {
        Document document = collection.find(eq("_id", 1234)).first();

        byte[] xmlByteArray = document.get("xml", Binary.class).getData();
        String base64xml = Base64.getEncoder().encodeToString(xmlByteArray);
    }

}
