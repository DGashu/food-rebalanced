package com.elgachu.foodrebalanced.config;

import java.util.List;

public class FoodConfigEntry {

    public Boolean requiresHunger = true;

    public Integer nutrition = 0;

    public Float saturation = 0.0f;

    public Boolean replaceVanillaEffects = false;

    public Integer eatSpeed = 32;

    public List<EffectEntry> effects;

    public List<String> removeEffects;

}