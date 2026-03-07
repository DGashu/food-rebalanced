package com.elgachu.foodrebalanced.config;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class FoodConfig {

    private static final Map<ResourceLocation, FoodConfigEntry> FOOD_MAP = new HashMap<>();

    public static FoodConfigEntry get(Item item) {

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        return FOOD_MAP.get(id);
    }
    public static void load(File configFile) {

        FOOD_MAP.clear();

        try {
            Gson gson = new Gson();

            JsonObject root = gson.fromJson(new FileReader(configFile), JsonObject.class);

            for (String key : root.keySet()) {

                ResourceLocation itemId = new ResourceLocation(key);

                JsonObject entryObject = root.getAsJsonObject(key);

                FoodConfigEntry entry = gson.fromJson(entryObject, FoodConfigEntry.class);

                FOOD_MAP.put(itemId, entry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}