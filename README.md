# Fluid
A 2d fluid simulation using libgdx


Infomation, formulas and illustrations are available on my blog post https://mynameislaurence.com/2015/08/16/realtime-fluid-simulation-in-java/
 
Please read the above links as they have the correct pictures and formatting.


The Basics

The most common approach to simulating fluids is to use SPH (Smoothed Particle Hydrodynamics) This approach uses lots of particles to represent the fluid as they interact with each other by exerting a range of different forces on each other, all of which can be derived from the original navier-stokes equations. The forces are pressure, viscosity, external (boundaries, gravity or use interactions) and surface tension.
In every new frame these forces are calculated for each particle based on formulas and a smoothing kernel, which is a function that scales the force according to how close the particles are, so the forces stay local. The forces are then summed and divided by the mass to calculate acceleration (F=ma) which is integrated to calculate the velocity and the new position.
Each particle is defined to have a set mass, position, velocity. As well as universal constants for the entire fluid such as viscosity, pressure stiffness, smoothing width.

 

Implementation

The implementation I used worked in java libgdx, and simulated a 2d fluid of any parameters, the defaults are chosen to mimic water.
The algorithm used is as follows:

For each particle:

  Compute the list of neighbours (in range of the smoothing width) to that particle (1)

For each particle:

  Calculate the density of the particle (2)
  
  Calculate the pressure of the particle (3)

For each particle:

  Calculate the forces of the particle (4)

For each particle:

  Calculate the acceleration of the particle and integrate (5)

