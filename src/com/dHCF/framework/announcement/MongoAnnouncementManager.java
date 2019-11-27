package com.dHCF.framework.announcement;

import com.dHCF.framework.BasePlugin;
import com.mongodb.CursorType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.logging.Level;

public class MongoAnnouncementManager extends AnnouncementManager implements Runnable {
    private final MongoCollection<Document> mongoCollection;
    private final UUID serverUID = UUID.randomUUID(); private final MongoCollection<Document> cursorCollection;

    public MongoAnnouncementManager(BasePlugin plugin) {
        super(plugin);
        MongoDatabase mongoDatabase = plugin.getMongoClient().getDatabase(plugin.getDatabaseName());
        this.mongoCollection = mongoDatabase.getCollection("announcements");
        this.cursorCollection = mongoDatabase.getCollection("announcementMessages");
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, this, 20L);
    }

    public void run() {
        for (MongoCursor mongoCursor = this.mongoCollection.find().iterator(); mongoCursor.hasNext(); ) { Document announcementDocument = (Document)mongoCursor.next();
            Announcement announcement = new Announcement(announcementDocument);
            this.announcementConcurrentMap.put(announcement.getName(), announcement); }

        Document query = new Document();
        Document projection = new Document();
        MongoCursor<Document> cursor = this.cursorCollection.find(query).projection(projection).cursorType(CursorType.TailableAwait).iterator();
        try {
            while (cursor.hasNext()) {
                Document document = (Document)cursor.next();
                UUID serverID = (UUID)document.get("serverId", UUID.class);
                if (!Objects.equals(serverID, this.serverUID)) {
                    String announcementName = document.getString("announcement");
                    Document announcementDocument = (Document)this.mongoCollection.find(new Document("name", announcementName)).first();
                    if (announcementDocument != null) {
                        Announcement announcement = new Announcement(announcementDocument);
                        this.announcementConcurrentMap.put(announcementName, announcement);
                        continue;
                    }
                    this.announcementConcurrentMap.remove(announcementName);
                }

            }

        } catch (IllegalStateException ex) {
            this.plugin.getLogger().log(Level.INFO, "Announcment Cursor Thread closing ");
        }
    }

    public void createUpdate(Announcement announcement) {
        Document insert = new Document();
        insert.put("serverId", this.serverUID);
        insert.put("announcement", announcement.getName());
        this.cursorCollection.insertOne(insert);
    }

    public Announcement getAnnouncement(String name) {
        Document document = (Document)this.mongoCollection.find(new Document("name", name)).first();
        if (document != null) {
            return new Announcement(document);
        }
        return null;
    }


    public void saveAnnouncement(Announcement announcement) {
        super.saveAnnouncement(announcement);
        String name = announcement.getName();
        Document document = (Document)this.mongoCollection.find(new Document("name", name)).first();
        if (document == null) {
            this.mongoCollection.insertOne(new Document(announcement.serialize()));
        } else {

            this.mongoCollection.updateOne(Filters.eq("name", name), Filters.and(new Bson[] { Updates.set("lines", Arrays.asList(announcement.getLines())), Updates.set("delay", Integer.valueOf(announcement.getDelay())) }));
        }
        createUpdate(announcement);
    }



    public void removeAnnouncement(Announcement announcement) {
        super.removeAnnouncement(announcement);
        String name = announcement.getName();
        this.mongoCollection.deleteMany(new Document("name", name));
        createUpdate(announcement);
    }

    public List<Announcement> getAllAnnouncemens() {
        List<Announcement> announcements = new ArrayList<Announcement>();
        for (MongoCursor mongoCursor = this.mongoCollection.find().iterator(); mongoCursor.hasNext(); ) { Document document = (Document)mongoCursor.next();
            announcements.add(new Announcement(document)); }

        return announcements;
    }
}
