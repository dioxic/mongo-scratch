package org.mongodb.scratch.repository;

import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.mongodb.scratch.model.Address;
import org.mongodb.scratch.model.Person;

import java.time.LocalDateTime;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class PojoRepository extends AbstractRepository<Person> {

    @Override
    protected MongoClientSettings getMongoClientSettings() {
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        return MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .build();
    }

    @Override
    protected MongoCollection<Person> getCollection() {
        return mongoClient
                .getDatabase("test")
                .getCollection("person", Person.class);
    }

//    public InsertOneResult insertOnePerson(Person person) {
//        InsertOneResult result = super.insertOnePerson(person);
//
//        if (result.getInsertedId() != null) {
//            person.setId(result.getInsertedId().asObjectId().getValue());
//        }
//
//        return result;
//    }

    public void insertManyPerson(List<Person> persons) {
        getCollection().insertMany(persons);
    }

    public Person findById(ObjectId id) {
        return getCollection().find(eq("_id", id)).first();
    }

    public FindIterable<Person> findByName(String firstName, String lastName) {
        return getCollection().find(and(
                eq("firstName", firstName),
                eq("lastName", lastName)
        )).sort(ascending("lastName"));
    }

    public FindIterable<Person> findOlderThan(int age) {
        LocalDateTime date = LocalDateTime.now().minusYears(age);

        return getCollection().find(gte("birthday", date));
    }

    public void addAddress(ObjectId personId, Address address) {
        getCollection().updateOne(
                eq("_id", personId),
                push("addresses", address)
        );
    }

    public void changeEmail(String oldEmail, String newEmail) {
        UpdateOptions options = new UpdateOptions()
                .arrayFilters(List.of(new Document("emailFilter", oldEmail)));

        getCollection().updateOne(
                eq("emails", oldEmail),
                set("emails.$[emailFilter]", newEmail),
                options
        );
    }

    public void insertWithTransaction(Person person1, Person person2) {
        ClientSession session = mongoClient.startSession();

        session.startTransaction();

        getCollection().insertOne(session, person1);
        getCollection().insertOne(session, person2);

        session.commitTransaction();

    }

    public void insertWithTransactionAlternative(Person person1, Person person2) {
        ClientSession session = mongoClient.startSession();

        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();

        TransactionBody<String> txnBody = () -> {
            getCollection().insertOne(session, person1);
            getCollection().insertOne(session, person2);
            return "Inserted some people";
        };

        try (session) {
            session.withTransaction(txnBody, txnOptions);
        } catch (RuntimeException e) {
            // some error handling
        }
    }

}
