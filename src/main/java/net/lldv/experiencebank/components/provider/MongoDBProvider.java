package net.lldv.experiencebank.components.provider;

import cn.nukkit.Player;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBProvider extends Provider {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> xpCollection;

    @Override
    public void connect(ExperienceBank server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(server.getConfig().getString("MongoDB.Uri"));
            this.mongoClient = new MongoClient(uri);
            this.mongoDatabase = this.mongoClient.getDatabase(server.getConfig().getString("MongoDB.Database"));
            this.xpCollection = this.mongoDatabase.getCollection("xp_data");
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.OFF);
            server.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(ExperienceBank server) {
        this.mongoClient.close();
        server.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public boolean userExists(String player) {
        Document document = this.xpCollection.find(new Document("player", player)).first();
        return document != null;
    }

    @Override
    public void createUserData(Player player) {
        Document document = new Document("player", player.getName())
                .append("xp", 0);
        this.xpCollection.insertOne(document);
        ExperienceBankAPI.getCachedXp().put(player.getName(), 0);
    }

    @Override
    public void setBankXp(String player, int xp) {
        Document document = new Document("player", player);
        Document found = this.xpCollection.find(document).first();
        Bson newEntry = new Document("xp", xp);
        Bson newEntrySet = new Document("$set", newEntry);
        assert found != null;
        this.xpCollection.updateOne(found, newEntrySet);
        ExperienceBankAPI.getCachedXp().remove(player);
        ExperienceBankAPI.getCachedXp().put(player, xp);
    }

    @Override
    public void getBankXp(String player, Consumer<Integer> xp) {
        CompletableFuture.runAsync(() -> {
            Document document = this.xpCollection.find(new Document("player", player)).first();
            if (document != null) xp.accept(document.getInteger("xp"));
        });
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }

}
