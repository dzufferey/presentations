$fn=180;

difference() {
    intersection() {
        cube(1.8, true);
        sphere(1.2);
    }
    translate([0,0,-2]) cylinder(r = 0.7, h=4);
    rotate([90,0,0]) translate([0,0,-2]) cylinder(r = 0.7, h=4);
    rotate([0,90,0]) translate([0,0,-2]) cylinder(r = 0.7, h=4);
}