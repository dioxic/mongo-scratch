package org.mongodb.scratch.repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Updates.set;

public abstract class AbstractRepository<T> {

    protected final MongoClient mongoClient;

    public AbstractRepository() {
        mongoClient = MongoClients.create(getMongoClientSettings());
    }

    protected abstract MongoCollection<T> getCollection();

    protected MongoClientSettings getMongoClientSettings() {
        return MongoClientSettings.builder()
                .build();
    }

    public void dropCollection() {
        getCollection().drop();
    }

//    public InsertOneResult insertOnePerson(T person) {
//        return getCollection().insertOne(person);
//    }

    public void insertManyPerson(List<T> persons) {
        getCollection().insertMany(persons);
    }

    public T findById(ObjectId id) {
        return getCollection().find(eq("_id", id)).first();
    }

    public FindIterable<T> findByName(String firstName, String lastName) {
        return getCollection().find(and(
                eq("firstName", firstName),
                eq("lastName", lastName)
        )).sort(ascending("lastName"));
    }

    public FindIterable<T> findOlderThan(int age) {
        LocalDateTime date = LocalDateTime.now().minusYears(age);

        return getCollection().find(gte("birthday", date));
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

}
