package com.mynameislaurence.fluid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by Home on 23/06/2015.
 */
public class Simulation {

    public ArrayList<Particle> particles = new ArrayList<Particle>();

    public double visocityConstant;
    public double densityConstant;
    public double smoothingWidth;

    public double calcDensityConstant;
    public double calcPressureConstant;
    public double calcViscConstant;
    public double stiffnessConstant;
    public double restDensityConstant;

    public void init() {
        visocityConstant = 0.001f;
        densityConstant = 1;
        smoothingWidth = 2;
        stiffnessConstant = 500f;

        calcDensityConstant = (315d) / (64d * Math.PI * Math.pow(smoothingWidth, 9));
        calcPressureConstant = (45d) / (Math.PI * Math.pow(smoothingWidth, 6));
        calcViscConstant = (45d) / (Math.PI * Math.pow(smoothingWidth, 6));

        restDensityConstant = 1000;

        for (double x = 0; x < 50d; x++) {
            for (double y = 0; y < 10d; y++) {
                particles.add(new Particle(this, 1 + x, 1.1 + y, 0, 0, false, 0.01, (int) (x * 100 + y)));
            }
        }
    }

    public void update(float deltaTime) {
        System.out.println("Updating d:" + deltaTime);
        double checkingDistance = smoothingWidth * smoothingWidth;

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            Particle p2;
            p.particleneighbour.clear();
            p.particleneighbour = new ArrayList<Particle>();
            for (int j = 0; j < particles.size(); j++) {
                p2 = particles.get(j);


                if ((j != i) && (p.num != p2.num) && (distSq(p, p2) <= checkingDistance)) {

                    p.particleneighbour.add(particles.get(j));
                }
            }

            double density = 0;
            for (int j = 0; j < p.particleneighbour.size(); j++) {
                p2 = p.particleneighbour.get(j);
                double ds = Math.pow((smoothingWidth * smoothingWidth - distSq(p, p2)), 3);
                density = density + (p2.mass * (calcDensityConstant) * ds);
            }
            p2 = p;
            double ds = Math.pow((smoothingWidth * smoothingWidth), 3);
            density = density + (p2.mass * (calcDensityConstant) * ds);

            p.density = density;
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.pressure = stiffnessConstant * stiffnessConstant * (p.density - restDensityConstant);

        }

        for (int i = 0; i < particles.size(); i++) {
            //Pressure
            double pressurex = 0;
            double pressurey = 0;
            Particle p = particles.get(i);
            Particle p2;


            for (int j = 0; j < p.particleneighbour.size(); j++) {
                p2 = p.particleneighbour.get(j);
                double dis = dist(p, p2);
                double dsx = Math.pow(smoothingWidth - Math.abs(p2.xpos - p.xpos), 2) * ((p2.xpos - p.xpos) / (dis));
                double dsy = Math.pow(smoothingWidth - Math.abs(p2.ypos - p.ypos), 2) * ((p2.ypos - p.ypos) / (dis));
                pressurex = pressurex + ((p2.mass * ((p.pressure + p2.pressure) / (2 * p2.density * p.density))) * (calcPressureConstant) * dsx);
                pressurey = pressurey + ((p2.mass * ((p.pressure + p2.pressure) / (2 * p2.density * p.density))) * (calcPressureConstant) * dsy);

            }

            p.pressureoverdensityx = -pressurex;
            p.pressureoverdensityy = -pressurey;

        }

        for (int i = 0; i < particles.size(); i++) {
            //Viscosity
            double visx = 0;
            double visy = 0;
            Particle p = particles.get(i);
            Particle p2;
            for (int j = 0; j < p.particleneighbour.size(); j++) {
                p2 = p.particleneighbour.get(j);
                double dsx = smoothingWidth - Math.abs(p2.xpos - p.xpos);
                double dsy = smoothingWidth - Math.abs(p2.ypos - p.ypos);

                visx = visx + (p2.mass * ((p2.xvel - p.xvel) / p2.density) * (calcViscConstant) * dsx);
                visy = visy + (p2.mass * ((p2.yvel - p.yvel) / p2.density) * (calcViscConstant) * dsy);
            }
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            if (!p.solid) {

                //final calc for movement
                double accelerationx = (p.pressureoverdensityx + p.visoelasticflowoverdensityx);
                double accelerationy = -10 + (p.pressureoverdensityy + p.visoelasticflowoverdensityy);

                p.xvel = p.xvel + accelerationx * deltaTime;
                p.yvel = p.yvel + accelerationy * deltaTime;


                p.xpos = p.xpos + p.xvel * deltaTime;
                p.ypos = p.ypos + p.yvel * deltaTime;

                //added collisions with side of container
                if (p.ypos <= 1 && p.yvel < 0) {
                    //p.ypos = 1;
                    p.yvel = -0.9 * p.yvel;
                    p.xvel = 0.9 * p.xvel;
                    p.ypos = 1 + (1 - p.ypos);
                }
                if (p.xpos <= 0 && p.xvel < 0) {
                    // p.xpos = 0;
                    p.xvel = -0.9 * p.xvel;
                    p.yvel = 0.9 * p.yvel;
                    p.xpos = -p.xpos;
                }
                if (p.xpos >= 7 && p.xvel > 0) {
                    // p.xpos = 7;
                    p.xvel = -0.9 * p.xvel;
                    p.yvel = 0.9 * p.yvel;
                    p.xpos = 7 - (p.xpos - 7);
                }
            }


        }


    }

    private double distSq(Particle p1, Particle p2) {
        return Math.pow(p1.xpos - p2.xpos, 2) + Math.pow(p1.ypos - p2.ypos, 2);
    }

    private double dist(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p1.xpos - p2.xpos, 2) + Math.pow(p1.ypos - p2.ypos, 2));
    }

    public void render(ShapeRenderer shapes) {

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            shapes.setColor(new Color(0f, 0f, 1, 1f));
            shapes.circle((float) (p.xpos * 10), (float) (p.ypos * 10), 5);
        }

    }
}
