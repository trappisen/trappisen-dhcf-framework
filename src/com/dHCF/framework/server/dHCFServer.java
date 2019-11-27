package com.dHCF.framework.server;

import com.dHCF.framework.BasePlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;

public class dHCFServer {
    private final BasePlugin plugin;
    private final ObjectId objectId;

    public dHCFServer(BasePlugin plugin) {
        this.objectId = new ObjectId();




        this.plugin = plugin;
        this.database = plugin.getMongoClient().getDatabase(plugin.getDatabaseName());
        this.collection = this.database.getCollection("servers", Document.class);
        insert();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::update, 10L, 10L);
    }
    private MongoDatabase database; private MongoCollection<Document> collection;
    public void insert() {
        Document document = new Document();
        document.put("_id", this.objectId);
        document.put("online", Boolean.valueOf(true));
        document.put("name", ServerSettings.NAME);
        document.put("whitelisted", Boolean.valueOf(Bukkit.hasWhitelist()));
        document.put("onlinePlayers", Integer.valueOf(Bukkit.getOnlinePlayers().size()));
        document.put("maxPlayers", Integer.valueOf(Bukkit.getMaxPlayers()));
        this.collection.deleteMany(new Document("name", ServerSettings.NAME));
        this.collection.insertOne(document);
    }

    public void update() {
        this.collection.updateOne(Filters.eq("_id", this.objectId),
                Filters.and(new Bson[] {
                        Updates.set("whitelisted", Boolean.valueOf(Bukkit.hasWhitelist())),
                        Updates.set("onlinePlayers", Integer.valueOf(Bukkit.getOnlinePlayers().size())),
                        Updates.set("maxPlayers", Integer.valueOf(Bukkit.getMaxPlayers()))
                }));
    }

    public void close() {
        this.collection.updateOne(Filters.eq("_id", this.objectId),
                Filters.and(new Bson[] {
                        Updates.set("online", Boolean.valueOf(false)),
                        Updates.set("onlinePlayers", Integer.valueOf(0)),
                        Updates.set("maxPlayers", Integer.valueOf(Bukkit.getMaxPlayers()))
                }));
    }
}

