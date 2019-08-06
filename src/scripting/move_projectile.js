function update() {
	if (obj.type.equals("ENEMY_SHIP")) {
		obj.y += 5;
	}
	else if (obj.type.equals("MOTHERSHIP")) {
	    obj.y -= 5;
	}
}
