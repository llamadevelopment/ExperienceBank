package net.lldv.experiencebank.components.managers;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.managers.provider.Provider;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.CompletableFuture;

public class MongoDBProvider extends Provider {

    Config config = ExperienceBank.getInstance().getConfig();

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> xpCollection;

    @Override
    public void connect(ExperienceBank server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(config.getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(config.getString("MongoDB.Database"));
            xpCollection = mongoDatabase.getCollection("xp_data");
            server.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(ExperienceBank server) {
        mongoClient.close();
        server.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public boolean userExists(String player) {
        Document document = xpCollection.find(new Document("player", player)).first();
        return document == null;
    }

    @Override
    public void createUserData(Player player) {
        Document document = new Document("player", player)
                .append("xp", player.getExperienceLevel());
        xpCollection.insertOne(document);
        ExperienceBank.getInstance().xpMap.put(player.getName(), getBankXp(player.getName()));
    }

    @Override
    public void setBankXp(String player, int xp) {
        int newXp = ExperienceBank.getInstance().xpMap.get(player) + xp;
        Document document = new Document("player", player);
        Document found = xpCollection.find(document).first();
        Bson newEntry = new Document("xp", newXp);
        Bson newEntrySet = new Document("$set", newEntry);
        assert found != null;
        xpCollection.updateOne(found, newEntrySet);
        ExperienceBank.getInstance().xpMap.remove(player);
        ExperienceBank.getInstance().xpMap.put(player, newXp);
    }

    @Override
    public int getBankXp(String player) {
        Document document = xpCollection.find(new Document("player", player)).first();
        if (document != null) return document.getInteger("xp");
        return -1;
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }
}
