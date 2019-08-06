function update() {
	lower_bound = 0;
	upper_bound = 300;
	
	//print(moveComponent.distanceTravelled);
	
    if (distanceTravelled < lower_bound) {
    	moveComponent.distanceTravelled = lower_bound;
        
        moveComponent.xSpeed = 5;
        
        if (hasStarted)
        obj.y += 10;
    }
    else if (distanceTravelled > upper_bound) {
    	moveComponent.distanceTravelled = upper_bound;

        moveComponent.xSpeed = -5;
    
        if (hasStarted)
        obj.y += 10;
    }

    // update position
    obj.x += moveComponent.xSpeed;
    moveComponent.distanceTravelled += moveComponent.xSpeed;
}
