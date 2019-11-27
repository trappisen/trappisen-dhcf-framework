package com.dHCF.framework.user;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.persistence.Entity;
import java.util.Map;
import java.util.UUID;

@Entity("globaluser")
public class ConsoleUser
        extends ServerParticipator
        implements ConfigurationSerializable
{
    public static final UUID CONSOLE_UUID = UUID.fromString("29f26148-4d55-4b4b-8e07-900fda686a67");



    public ConsoleUser() {
        super(CONSOLE_UUID);
        setName("CONSOLE");
    }

    public ConsoleUser(Map map) {
        super(map);
        setName("CONSOLE");
    }
}
