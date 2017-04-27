$fn=60;

module hexagon(r, h) {
    cylinder(r,r,h,$fn=6);
}



module rounded_cylinder(r1, r2, h) {
    translate([0,0,r2])
    minkowski() {
        cylinder(r = r1 - r2, h = h - 2* r2);
        sphere(r2);
    }
}

module body(n) {
    difference() {
        hull() {
            for (i = [1:n]) {
                rotate([0,0,i*360/n]) translate([30,0,0]) rounded_cylinder(11, 2, 7);
            }
        }
        cylinder(r=11.1, h=7);
        for (i = [1:n]) {
                rotate([0,0,i*360/n]) translate([30,0,0]) hexagon(7.5, 7);
        }
    }
}

body(3);