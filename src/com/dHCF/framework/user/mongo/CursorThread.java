package com.dHCF.framework.user.mongo;

import com.dHCF.framework.BasePlugin;
import com.mongodb.CursorType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import org.bson.Document;

public class CursorThread implements Runnable {
    private final BasePlugin basePlugin;
    private final MongoDatabase database;

    public CursorThread(BasePlugin basePlugin) {
        this.serverUID = UUID.randomUUID();



        this.basePlugin = basePlugin;
        this.database = basePlugin.getMongoClient().getDatabase(basePlugin.getDatabaseName());
        this.collection = this.database.getCollection("Messages");
        this.mongoUserManager = (MongoUserManager)basePlugin.getUserManager();
        basePlugin.getServer().getScheduler().runTaskLaterAsynchronously(basePlugin, this, 20L);
    }
    private final MongoCollection<Document> collection; private final UUID serverUID; private final MongoUserManager mongoUserManager;
    public void run() {
        Document query = new Document();
        Document projection = new Document();
        MongoCursor<Document> cursor = this.collection.find(query).projection(projection).cursorType(CursorType.TailableAwait).iterator();
        try {
            while (cursor.hasNext()) {
                Document document = (Document)cursor.next();
                UUID uuid = (UUID)document.get("id", UUID.class);
                UUID serverID = (UUID)document.get("serverId", UUID.class);
                if (!Objects.equals(serverID, this.serverUID)) {
                    this.mongoUserManager.mongoFetch(uuid);
                }
            }

        } catch (IllegalStateException ex) {
            this.basePlugin.getLogger().log(Level.INFO, "Cursor Thread closing ");
        }
    }

    public void createUpdate(UUID user) {
        Document insert = new Document();
        insert.put("id", user);
        insert.put("serverId", this.serverUID);
        this.collection.insertOne(insert);
    }
}
