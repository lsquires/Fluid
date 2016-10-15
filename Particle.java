package com.mynameislaurence.fluid;

import java.util.ArrayList;

/**
 * Created by Home on 23/06/2015.
 */
public class Particle {

    private final Simulation sim;
    public double xpos;
    public double ypos;
    public double xvel;
    public double yvel;
    public double density;
    public double pressure;
    public double pressureoverdensityx;
    public double pressureoverdensityy;
    public double visoelasticflowoverdensityx;
    public double visoelasticflowoverdensityy;
    public double mass;
    public boolean solid;
    public int num;
    public ArrayList<Particle> particleneighbour = new ArrayList<Particle>();


    public Particle(Simulation s,double x,double y,double xv,double yv,boolean sol,double m,int n)
    {
        sim = s;
        xpos = x;
        ypos = y;
        xvel = xv;
        yvel = yv;
        mass = m;
        solid = sol;
        num =n;
    }



}
