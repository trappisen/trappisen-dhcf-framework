package com.dHCF.util.chat;

import com.google.common.base.MoreObjects;
import net.minecraft.server.v1_8_R3.Enchantment;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.potion.CraftPotionEffectType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.enchantments.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lang
{
    public static void initialize(String lang) throws IOException {
        translations = new HashMap();
        if (lang == null) {
            lang = "en_US";
        }
        if (!lang.equals(language)) {
            InputStream stream = null;
            BufferedReader reader = null;
            try {
                language = lang;
                String resourcePath = "/assets/minecraft/lang/" + language + ".lang";
                stream = Item.class.getResourceAsStream(resourcePath);
                reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.contains("=")) {
                        Matcher matcher = PAT.matcher(line);
                        if (!matcher.matches()) {
                            continue;
                        }
                        translations.put(matcher.group(1), matcher.group(2));
                    }
                }
            } finally {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }


    public static String getLanguage() { return language; }


    public static String translatableFromStack(ItemStack stack) {
        ItemStack nms = CraftItemStack.asNMSCopy(stack);
        Item item = nms.getItem();
        return item.a(nms) + ".name";
    }

    public static String fromStack(ItemStack stack) {
        String node = translatableFromStack(stack);
        return (String)MoreObjects.firstNonNull(translations.get(node), node);
    }

    public static String translatableFromEnchantment(Enchantment ench) {
        Enchantment nms = Enchantment.byId[ench.getId()];
        final String s = (nms == null) ? ench.getName() : nms.a();
        return s;
    }

    public static String fromEnchantment(Enchantment ench) {
        String node = translatableFromEnchantment(ench);
        return (String)MoreObjects.firstNonNull(translations.get(node), node);
    }

    public static String translatableFromPotionEffectType(PotionEffectType effectType) {
        CraftPotionEffectType craftType = (CraftPotionEffectType)PotionEffectType.getById(effectType.getId());
        return craftType.getHandle().a();
    }

    public static String fromPotionEffectType(PotionEffectType effectType) {
        String node = translatableFromPotionEffectType(effectType);
        String val = (String)translations.get(node);
        return (val == null) ? node : val;
    }


    public static String translate(String key, Object... args) { return String.format((String)translations.get(key), args); }





    private static final Pattern PAT = Pattern.compile("^\\s*([\\w\\d\\.]+)\\s*=\\s*(.*)\\s*$");
    private static String language = null;
    private static Map<String, String> translations;
}

