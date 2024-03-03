package me.kyren223.kapi.particles;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleData {
    private Particle particle;
    private int count;
    private double spreadX, spreadY, spreadZ;
    private double extra;
    private Object data;
    
    public ParticleData(Particle particle, int count, double spreadX, double spreadY, double spreadZ, double extra, Object data) {
        this.particle = particle;
        this.count = count;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
        this.extra = extra;
        this.data = data;
    }
    
    public Particle getParticle() {
        return particle;
    }
    
    public void setParticle(Particle particle) {
        this.particle = particle;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public double getSpreadX() {
        return spreadX;
    }
    
    public void setSpreadX(double spreadX) {
        this.spreadX = spreadX;
    }
    
    public double getSpreadY() {
        return spreadY;
    }
    
    public void setSpreadY(double spreadY) {
        this.spreadY = spreadY;
    }
    
    public double getSpreadZ() {
        return spreadZ;
    }
    
    public void setSpreadZ(double spreadZ) {
        this.spreadZ = spreadZ;
    }
    
    public Vector getSpread() {
        return new Vector(spreadX, spreadY, spreadZ);
    }
    
    public void setSpread(Vector vector) {
        this.spreadX = vector.getX();
        this.spreadY = vector.getY();
        this.spreadZ = vector.getZ();
    }
    
    public void setSpread(double spread) {
        this.spreadX = spread;
        this.spreadY = spread;
        this.spreadZ = spread;
    }
    
    public double getExtra() {
        return extra;
    }
    
    public void setExtra(double extra) {
        this.extra = extra;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public void setRedstoneData(Color color, float size) {
        if (particle != Particle.REDSTONE) {
            throw new IllegalStateException("Particle is not REDSTONE");
        }
        this.data = new Particle.DustOptions(color, size);
    }
    
    public Particle.DustOptions getRedstoneData() {
        if (particle != Particle.REDSTONE) {
            throw new IllegalStateException("Particle is not REDSTONE");
        }
        return (Particle.DustOptions) data;
    }
    
    public void setRedstoneColor(Color color) {
        if (particle != Particle.REDSTONE) {
            throw new IllegalStateException("Particle is not REDSTONE");
        }
        this.data = new Particle.DustOptions(color, getRedstoneData().getSize());
    }
    
    public void setRedstoneSize(float size) {
        if (particle != Particle.REDSTONE) {
            throw new IllegalStateException("Particle is not REDSTONE");
        }
        this.data = new Particle.DustOptions(getRedstoneData().getColor(), size);
    }
}
