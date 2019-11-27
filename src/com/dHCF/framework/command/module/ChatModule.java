package com.dHCF.framework.command.module;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommandModule;
import com.dHCF.framework.command.module.chat.*;
import com.dHCF.framework.command.module.chat.announcement.AnnouncementCommand;

public class ChatModule extends BaseCommandModule {
    public ChatModule(BasePlugin plugin) {
        this.commands.add(new AnnouncementCommand(plugin));
        this.commands.add(new BroadcastCommand(plugin));
        this.commands.add(new BroadcastRawCommand());
        this.commands.add(new ClearChatCommand());
        this.commands.add(new DisableChatCommand(plugin));
        this.commands.add(new SlowChatCommand(plugin));
        this.commands.add(new StaffChatCommand(plugin));
        this.commands.add(new IgnoreCommand(plugin));
        this.commands.add(new MessageCommand(plugin));
        this.commands.add(new MessageSpyCommand(plugin));
        this.commands.add(new ReplyCommand(plugin));
        this.commands.add(new ToggleChatCommand(plugin));
        this.commands.add(new ToggleMessagesCommand(plugin));
        this.commands.add(new ToggleStaffChatCommand(plugin));
    }
}
