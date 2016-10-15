package com.mynameislaurence.fluid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Home on 23/06/2015.
 */
public class Simulation {

    public ArrayList<Particle> particles = new ArrayList<Particle>();


    public float calcDensityConstant;
    public float calcPressureConstant;
    public float calcViscConstant;
    public float calcSurfConstant;


    public Vector2 gravity;
    public float timestep;
    public float restDensity;
    public float mass;
    public float viscosity;
    public float surfaceTension;
    public float cr;
    public float hwidth;
    public float kstiffness;

    public float simTime;
    public float realTime;
    public int num;
    public float thresholdsqr;


    public void init() {

        gravity = new Vector2(0f, -9.82f);
        timestep = 0.005f;
        restDensity = 99.89f;
        mass = 0.02f;
        viscosity = 1.5f;
        surfaceTension = 0.2728f;
        cr = 1f;
        hwidth = 0.0457f;
        kstiffness = 14f;
        thresholdsqr = 9.5f;
        simTime = 0;
        realTime = 0;
        gravity.scl(restDensity);

        surfaceTension = 0.5728f;
        hwidth = 0.1557f;
        // hwidth = 0.2857f;


        calcDensityConstant = (float) ((315d) / (64d * Math.PI * Math.pow(hwidth, 9)));
        calcPressureConstant = (float) ((-45d) / (Math.PI * Math.pow(hwidth, 6)));
        calcViscConstant = (float) ((45d) / (Math.PI * Math.pow(hwidth, 6)));
        calcSurfConstant = (float) ((-945d) / (32d * Math.PI * Math.pow(hwidth, 9)));

        num = 0;

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                num++;
                particles.add(new Particle(this, 0.5f + x / 35f + (float) (Math.random()) / 150f, 0.5f + y / 35f + (float) (Math.random()) / 150f, 0, 0, false, mass, num));
            }
        }

    }

    public void update(float deltaTime) {

        realTime += deltaTime;
        simTime += timestep;
        deltaTime = timestep;
        System.out.println("Updating   t=" + realTime + "      st=" + simTime);


        double checkingDistance = hwidth * hwidth;

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            Particle p2;
            p.particleneighbour.clear();
            p.particleneighbour = new ArrayList<Particle>();
            for (int j = 0; j < particles.size(); j++) {
                p2 = particles.get(j);
                if ((p.num != p2.num) && (p.pos.dst2(p2.pos) <= checkingDistance)) {
                    p.particleneighbour.add(particles.get(j));
                }
            }


            float density = 0;
            for (int j = 0; j < p.particleneighbour.size(); j++) {
                p2 = p.particleneighbour.get(j);
                density += p2.mass * densityKernel(dis(p, p2), hwidth);
            }
            density += p.mass * densityKernel(dis(p, p), hwidth);
            p.density = density;


            p.pressure = kstiffness * (p.density - restDensity);
        }

        for (int i = 0; i < particles.size(); i++) {
            //Pressure
            Particle p = particles.get(i);
            Particle p2;

            Vector2 pressureForce = new Vector2(0, 0);
            Vector2 viscoForce = new Vector2(0, 0);
            Vector2 surfaceForcen = new Vector2(0, 0);
            float surfaceForcedn = 0;
            for (int j = 0; j < p.particleneighbour.size(); j++) {
                p2 = p.particleneighbour.get(j);

                pressureForce.sub(pressureKernel(dis(p, p2), hwidth).scl((float) (p2.mass * (p.pressure + p2.pressure) * (1 / (2 * p2.density)))));

                viscoForce.add(p2.getVel().sub(p.vel).scl(p2.mass * (1 / p2.density) * viscoKernel(dis(p, p2), hwidth)));

                surfaceForcen.add(getSurfacenKernel(dis(p, p2), hwidth).scl(p2.mass / p2.density));
                surfaceForcedn += (getSurfacekKernel(dis(p, p2), hwidth) * (p2.mass / p2.density));
            }

            surfaceForcen.add(getSurfacenKernel(dis(p, p), hwidth).scl(p.mass / p.density));
            surfaceForcedn += (getSurfacekKernel(dis(p, p), hwidth) * (p.mass / p.density));

            float surfacel = surfaceForcen.len2();
            p.redness = surfacel / (thresholdsqr * 16f);
            if (surfacel > thresholdsqr) {
                p.Fsurface = surfaceForcen.scl(surfaceTension * (-surfaceForcedn / (float) Math.sqrt(surfacel)));
            } else {
                p.Fsurface = Vector2.Zero;
            }


            viscoForce.scl(viscosity);
            p.Fviscosity = viscoForce;

            p.Fpressure = pressureForce;


        }


        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            if (!p.solid) {

                Vector2 totalForce = new Vector2(0, 0);


                totalForce.add(p.Fpressure);
                totalForce.add(p.Fviscosity);
                totalForce.add(p.Fsurface);
                totalForce.add(gravity);
                totalForce.scl(1 / p.density);


                p.aftervel = p.beforevel.cpy().add(totalForce.scl(deltaTime));
                p.vel = (p.beforevel.cpy().add(p.aftervel)).scl(0.5f);
                p.beforevel = p.aftervel;
                p.pos.add(p.aftervel.cpy().scl(deltaTime));


                if (p.pos.y <= 0) {
                    if (p.aftervel.y < 0) {
                        p.aftervel.scl(cr, -cr);
                    }
                    p.vel = p.aftervel;
                    p.beforevel = p.aftervel;
                    p.pos.set(p.pos.x, -p.pos.y);
                }
                if (p.pos.x <= 0) {
                    if (p.aftervel.x < 0) {
                        p.aftervel.scl(-cr, cr);
                    }
                    p.vel = p.aftervel;
                    p.beforevel = p.aftervel;
                    p.pos.set(-p.pos.x, p.pos.y);
                }
                if (p.pos.x >= 2) {
                    if (p.aftervel.x > 0) {
                        p.aftervel.scl(-cr, cr);
                    }
                    p.vel = p.aftervel;
                    p.beforevel = p.aftervel;
                    p.pos.set(2 - (p.pos.x - 2), p.pos.y);
                }
            }


        }


    }


    private Vector2 getSurfacenKernel(Vector2 r, float h) {
        float rmagsqr = r.len2();
        float hsqr = h * h;
        if (rmagsqr >= 0 && rmagsqr <= hsqr) {
            return (r.scl((float) (calcSurfConstant * Math.pow(hsqr - rmagsqr, 2))));
        } else {
            return Vector2.Zero;
        }

    }

    private float getSurfacekKernel(Vector2 r, float h) {
        float rmagsqr = r.len2();
        float hsqr = h * h;
        if (rmagsqr >= 0 && rmagsqr <= hsqr) {
            return (calcSurfConstant * (hsqr - rmagsqr) * (3 * hsqr - 7 * rmagsqr));
        } else {
            return 0;
        }

    }

    private float viscoKernel(Vector2 r, float h) {
        float rmag = r.len();
        if (rmag >= 0 && rmag <= h) {
            return (h - rmag) * calcViscConstant;
        } else {
            return 0f;
        }
    }

    private float densityKernel(Vector2 r, float h) {
        float rmagsqr = r.len2();
        float hsqr = h * h;
        if (rmagsqr >= 0 && rmagsqr <= hsqr) {
            return (float) Math.pow(hsqr - rmagsqr, 3) * calcDensityConstant;
        } else {
            return 0f;
        }

    }

    private Vector2 pressureKernel(Vector2 r, float h) {
        float rmag = r.len();
        if (rmag >= 0 && rmag <= h) {
            return (r.scl((float) (calcPressureConstant * Math.pow(h - rmag, 2) * (1 / rmag))));
        } else {
            return Vector2.Zero;
        }

    }


    private Vector2 dis(Particle p1, Particle p2) {
        return p1.getPos().sub(p2.pos);
    }


    public void render(ShapeRenderer batch, Sprite circle) {

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);


            batch.setColor(new Color(p.redness, 0f, (float) (1f), (float) (1)));
            batch.circle(100 + p.pos.x * 400f, 100 + p.pos.y * 400f, 10);
            //batch.draw(circle,100 + p.pos.x * 400f, 100 + p.pos.y * 400f);
        }

    }

    public void overlay(SpriteBatch batch, BitmapFont font) {
        font.draw(batch, shorten(new StringBuilder("t=" + simTime), 8), 5, 20);
        font.draw(batch, "fps=" + Gdx.graphics.getFramesPerSecond(), 5, 40);
    }

    private String shorten(StringBuilder s, int i) {

        if (s.length() < i) {
            for (int b = 0; b < i - s.length(); b++) {
                s.append("0");
            }
        } else {
            s.delete(i, s.length());
        }

        return s.toString();
    }

    public void spawnParticles(int x, int y) {

        float a1 = (float) (2 * Math.random());
        float a2 = (float) (-2 - 1 * Math.random());
        for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 4; i2++) {
                num++;
                particles.add(new Particle(this, (x + i * 10 - 100) / 400f, ((900 - y + i2 * 10) - 100) / 400f, a1, a2, false, mass, num));
            }
        }
    }
}
