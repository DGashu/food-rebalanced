package com.elgachu.foodrebalanced.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FoodConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FMLPaths.CONFIGDIR.get().resolve("foodrebalanced-foods.json");

    private static JsonObject configData = new JsonObject();

    public static void loadConfig() {
        try {

            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                configData = GSON.fromJson(reader, JsonObject.class);

                if (configData == null) configData = new JsonObject();
            }
            System.out.println("Food config loaded.");
        } catch (IOException e) {
            System.err.println("FoodRebalanced: Failed to load config file.");
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() throws IOException {

        JsonObject root = new JsonObject();

        JsonArray 
            stewEffects = effects(createEffect("minecraft:regeneration", 600, 0, 1.0f)), 
            cookieEffects = effects(createEffect("minecraft:speed", 100, 0, 1.0f)),
            pumpkinPieEffects = effects(createEffect("minecraft:haste", 400, 0, 1.0f)),
            goldenCarrotEffects = effects(createEffect("minecraft:night_vision", 300, 0, 1.0f)),
            glowBerryEffects = effects(createEffect("minecraft:glowing", 200, 0, 1.0f)),
            pufferfishEffects = effects(createEffect("minecraft:water_breathing", 400, 0, 1.0f)),
            honeyBottleEffects = effects(createEffect("minecraft:instant_health", 0, 0, 1.0f)),
            driedKelpRemoveEffects = removeEffects("minecraft:poison", "minecraft:nausea"),
            beetrootSoupEffects = effects(createEffect("minecraft:resistance", 3600, 0, 1.0f)),
            rabbitStewEffects = effects(createEffect("minecraft:speed", 6000, 1, 1.0f), createEffect("minecraft:jump_boost", 6000, 0, 1.0f));
        food(root, "minecraft:mushroom_stew",
            createFood(true, 6, 0.5f, true, 40, stewEffects, null));

        food(root, "minecraft:honey_bottle",
            createFood(false, 6, 0.1f, null, 16, honeyBottleEffects, null));

        food(root, "minecraft:cookie",
            createFood(false, 2, 0.2f, null, 16, cookieEffects, null));

        food(root, "minecraft:pumpkin_pie",
            createFood(true, 8, 0.8f, null, 32, pumpkinPieEffects, null));

        food(root, "minecraft:golden_carrot",
            createFood(false, 6, 0.8f, null, 20, goldenCarrotEffects, null));
        
        food(root, "minecraft:glow_berries",
            createFood(false, 2, 0.2f, null, 16, glowBerryEffects, null));

        food(root, "minecraft:pufferfish",
            createFood(false, 1, 0.1f, null, 32, pufferfishEffects, null));

        food(root, "minecraft:beetroot_soup",
            createFood(true, 6, 0.5f, true, 40, beetrootSoupEffects, null));

        food(root, "minecraft:rabbit_stew",
            createFood(true, 10, 0.6f, true, 40, rabbitStewEffects, null));

        food(root, "minecraft:cake",
            createFood(true, 2, 0.1f, null, 0, null, null));

        food(root, "minecraft:dried_kelp",
            createFood(false, 1, 0.2f, null, 16, null, driedKelpRemoveEffects));

        food(root, "minecraft:apple",
            createFood(true, 4, 0.6f, null, 20, null, null));
        food(root, "minecraft:carrot",
            createFood(true, 3, 1.f, null, 20, null, null));
        food(root, "minecraft:sweet_berries",
            createFood(true, 2, 0.2f, null, 16, null, null));

        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(root, writer);
        }
    }

    
    private static void food(JsonObject root, String itemId, JsonObject foodData) {

    root.add(itemId, foodData);

    }


    private static JsonObject createFood(Boolean requiresHunger, Integer nutrition, Float saturation, Boolean replaceVanillaEffects, Integer eatSpeed, JsonArray effects, JsonArray removeEffects) {

        JsonObject food = new JsonObject();
        
        if (requiresHunger != null)
        food.addProperty("requiresHunger", requiresHunger);

        if (nutrition != null)
            food.addProperty("nutrition", nutrition);

        if (saturation != null)
            food.addProperty("saturation", saturation);

        if (replaceVanillaEffects != null)
            food.addProperty("replaceVanillaEffects", replaceVanillaEffects);

        if (eatSpeed != null)
            food.addProperty("eatSpeed", eatSpeed);

        if (effects != null && effects.size() > 0)
            food.add("effects", effects);

        if (removeEffects != null && removeEffects.size() > 0)
            food.add("removeEffects", removeEffects);

        return food;
    }

    private static JsonArray effects(JsonObject... effects) {

        JsonArray array = new JsonArray();

        for (JsonObject e : effects)
            array.add(e);

        return array;
    }
    private static JsonObject createEffect(String effectId, int duration, int amplifier, float chance) {

        JsonObject effect = new JsonObject();

        effect.addProperty("effect", effectId);
        effect.addProperty("duration", duration);
        effect.addProperty("amplifier", amplifier);
        effect.addProperty("chance", chance);

        return effect;
    }
    private static JsonArray removeEffects(String... effects) {

        JsonArray array = new JsonArray();

        for (String e : effects)
            array.add(e);

        return array;
    }

    public static JsonObject getConfig() {
        return configData;
    }
}