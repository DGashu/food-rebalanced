package com.elgachu.foodrebalanced;

import com.elgachu.foodrebalanced.config.FoodConfig;
import com.elgachu.foodrebalanced.config.FoodConfigManager;
import com.elgachu.foodrebalanced.event.FoodEvents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;


@Mod(FoodRebalanced.MODID)
public class FoodRebalanced {

    public static final String MODID = "foodrebalanced";

    public FoodRebalanced() {
        FoodConfigManager.loadConfig();
        FoodConfig.load(FMLPaths.CONFIGDIR.get().resolve("foodrebalanced-foods.json").toFile());


        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(FoodEvents.class); // ← ADD THIS
    }

}