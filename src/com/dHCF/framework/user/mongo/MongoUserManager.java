package com.dHCF.framework.user.mongo;

import com.avaje.ebean.Query;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.UserManager;
import org.mongodb.morphia.Datastore;

import java.util.UUID;

public class MongoUserManager extends UserManager {
    private Datastore datastore;

    public MongoUserManager(BasePlugin plugin) {
        super(plugin);
        this.datastore = plugin.getDatastore();
    }


    public void save(BaseUser baseUser) { this.datastore.save(baseUser); }



    public BaseUser load(UUID uuid) { return (BaseUser)((Query)this.datastore.find(BaseUser.class).field("_id").equal(uuid)).get(); }


    public void mongoFetch(UUID uuid) {
        if (this.onlinePlayers.containsKey(uuid)) {
            BaseUser newUser = load(uuid);
            BaseUser old = (BaseUser)this.onlinePlayers.put(uuid, newUser);
            if (old != null)
                old.merge(newUser);
        }
    }
}
