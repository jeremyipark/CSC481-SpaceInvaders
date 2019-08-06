function update() {
	// set the x speed
	if (xSpeed < 0) {
		xSpeed = -7;
	} else {
		xSpeed = 7;
	}
	
	// set the obj location
	obj.y = 600;
	
	// keep within the bounds
    if (obj.x <= 0) {
        obj.x = 0;
    } else if (obj.x + obj.width >= screen_width) {
        obj.x = screen_width - obj.width;
    }

    // update position
    obj.x += xSpeed;
}
