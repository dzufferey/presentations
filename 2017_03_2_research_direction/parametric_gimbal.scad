$fn=50; // mesh resolution

pin_ir = 1.5;  // radius of the pins
pin_or = 1.8; // radius of the pin holes
ring_width=4; // width of a ring
ring_spacing=1; // width of the space between rings
thickness=5; // thickness of the inner-most ring
thickness_increase=.5; // how much each ring grows
num_rings=3; // number of rings not including the first ring
hole_radius=4; // size of the center hole

// translate everything so that it rests on z=0
translate(v=[0, 0, thickness*.5]) union() {

// loop over all rings
for (ring = [0 : num_rings]) {
    
    // compute dimensions
	ring_ir=hole_radius+ring_width*ring;
	ring_or=hole_radius+ring_width-ring_spacing+ring_width*ring;
	odd=(ring%2);
	even=((ring+1)%2);

	// translate each new ring so that the pins get printed resting on the next ring
	translate(v=[0, 0, ring*thickness_increase*.5])  intersection() {
		difference() {
			union() {
				sphere(r=ring_or, center=true);
				// the last ring does not have protruding pins
				if (ring != num_rings)
					rotate(v=[even, odd, 0], a=90)
						cylinder(r=pin_ir, h=(ring_or*2-ring_ir)*2-.5, center=true);
			}
			// the inner-most ring does not have pin holes
			if (ring==0)
				cylinder(r=hole_radius, h=50, center=true);
			else union() {
				sphere(r=ring_ir, center=true);
				rotate(v=[odd, even, 0], a=90)
					cylinder(r=pin_or, h=ring_or*2-2, center=true);
			}
		}
		// make each ring thicker than the last so the pin is still centered even though it is printed
		// resting on the next ring's hole
		cube(size=[500, 500, thickness+(thickness_increase*ring)], center=true);
	}
}
}

