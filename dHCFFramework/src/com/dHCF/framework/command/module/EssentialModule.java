package com.dHCF.framework.command.module;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommandModule;
import com.dHCF.framework.command.module.essential.*;

public class EssentialModule extends BaseCommandModule {
    public EssentialModule(BasePlugin plugin) {
        if (!BasePlugin.PRACTICE) {
            this.commands.add(new BiomeCommand());
            this.commands.add(new CraftCommand());
            this.commands.add(new RenameCommand());
            this.commands.add(new RepairCommand());
            this.commands.add(new AmivisCommand(plugin));
            this.commands.add(new VanishCommand(plugin));
        }
        this.commands.add(new SettingsCommand(plugin));
        this.commands.add(new CreateWorldCommand());
        this.commands.add(new ListCommand());
        this.commands.add(new EnchantCommand());
        this.commands.add(new NoteCommand());
        this.commands.add(new EntitiesCommand());
        this.commands.add(new FeedCommand());
        this.commands.add(new FlyCommand());
        this.commands.add(new FreezeCommand(plugin));
        this.commands.add(new GamemodeCommand());
        this.commands.add(new HatCommand());
        this.commands.add(new HealCommand());
        this.commands.add(new IpHistoryCommand(plugin));
        this.commands.add(new KillCommand());
        this.commands.add(new LagCommand());
        this.commands.add(new NameHistoryCommand(plugin));
        this.commands.add(new PingCommand());
        this.commands.add(new PositionCommand());
        this.commands.add(new ProxycommandCommand(plugin));
        this.commands.add(new RemoveEntityCommand());
        this.commands.add(new RulesCommand(plugin));
        this.commands.add(new SetMaxPlayersCommand());
        this.commands.add(new ToggleSoundsCommand(plugin));
        this.commands.add(new SpeedCommand());
        this.commands.add(new StopLagCommand(plugin));
        this.commands.add(new SudoCommand());
        this.commands.add(new UptimeCommand());
        this.commands.add(new WhoisCommand(plugin));
        this.commands.add(new ReportCommand(plugin));
        this.commands.add(new HelpopCommand(plugin));
    }
}

