package com.dHCF.framework.command;

public interface CommandManager {
    boolean containsCommand(BaseCommand paramBaseCommand);

    void registerAll(BaseCommandModule paramBaseCommandModule);

    void registerCommand(BaseCommand paramBaseCommand);

    void registerCommands(BaseCommand[] paramArrayOfBaseCommand);

    void unregisterCommand(BaseCommand paramBaseCommand);

    BaseCommand getCommand(String paramString);
}

