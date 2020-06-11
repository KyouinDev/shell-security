package io.kyouin.shellsec.utils;

import io.kyouin.shellsec.ShellSecurity;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Messages {

    private final ShellSecurity shellSec;
    private final FileConfiguration defaultConfig;
    private FileConfiguration config;

    public Messages(ShellSecurity shellSec) {
        this.shellSec = shellSec;

        InputStream is = shellSec.getResource("messages.yml");

        if (is == null) throw new IllegalArgumentException("Couldn't find messages.yml");

        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

        shellSec.saveResource("messages.yml", false);
        defaultConfig = YamlConfiguration.loadConfiguration(reader);
        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(shellSec.getDataFolder(), "messages.yml"));
    }

    public void sendMessage(CommandSender to, String message) {
        message = getMessageOrDefault(message).replaceAll("&", "§").replace("{name}", to.getName());

        to.sendMessage(Constants.PREFIX + " " + message);
    }

    public void send(Player p, String message) {
        message = getMessageOrDefault(message).replaceAll("&", "§").replace("{name}", p.getName());

        if (shellSec.getConfig().getBoolean("messages-as-titles", false)) {
            p.sendTitle(Constants.PREFIX, message, 10, 60, 10);
        } else {
            p.sendMessage(Constants.PREFIX + " " + message);
        }
    }

    public String getMessageOrDefault(String key) {
        return config.getString(key, defaultConfig.getString(key));
    }
}
