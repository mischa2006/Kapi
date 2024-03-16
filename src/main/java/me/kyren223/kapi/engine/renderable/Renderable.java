package me.kyren223.kapi.engine.renderable;

import org.bukkit.World;
import org.bukkit.util.Vector;

public interface Renderable {
    void spawn(World world, Vector point);
    void render(World world, Vector point);
    void despawn(World world, Vector point);
    Renderable clone();
}