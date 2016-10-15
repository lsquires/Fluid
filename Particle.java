package com.mynameislaurence.fluid;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Home on 23/06/2015.
 */
public class Particle {

    private final Simulation sim;
    public Vector2 pos;
    public Vector2 vel;
    public Vector2 beforevel = Vector2.Zero;
    public Vector2 aftervel = Vector2.Zero;


    public float density;
    public float pressure;
    public Vector2 Fpressure;
    public Vector2 Fviscosity;
    public Vector2 Fsurface;
    public float mass;
    public boolean solid;
    public int num;
    public ArrayList<Particle> particleneighbour = new ArrayList<Particle>();
    public float redness;
    public float changec;

    public Particle(Simulation s,float x,float y,float xv,float yv,boolean sol,float m,int n)
    {
        sim = s;
        pos = new Vector2(x,y);
        vel = new Vector2(xv,yv);
        beforevel = aftervel = vel;
        mass = m;
        solid = sol;

        num =n;
    }

    public Vector2 getPos()
    {
        return pos.cpy();
    }

    public Vector2 getVel()
    {
        return vel.cpy();
    }



}
