// Superquadrics and Bézier Surfaces
// Tyler Law and UMN CSCI 4611 staff
// 2017

// Grid dimension for superquadric: (size x size) number of wire lines.
var size = 50;

var n1 = 2;
var n2 = 2;
var a = 1.0;
var b = 1.0;
var c = 1.0;
var fColor;
var fov = 1;

// Which of the 2 objects is being displayed currently.
var isTeaspoon = false;

// Used for rotating object with mouse.
var lastX;
var lastY;
var deltaY = 0;
var deltaX = 0;

// Matrices m and mt used for bezier surface.
var m = mat4 (-1,  3, -3,  1,
						   3, -6,  3,  0,
					 	  -3,  3,  0,  0,
					  	 1,  0,  0,  0);

var mt = transpose(m);

var canvas;
var gl;
var programId;

//Global array for the points in the superquadric
var points = [];

//Transformation matrix for camera
var viewMatrix;
var viewMatrixLoc;

//Translation matrix for camera
var transCamera = mult(scalem(0.3,0.3,0.3), mat4(1));

// Binds "on-change" events for the controls on the web page
function initControlEvents() {
		var panSensitivity = 0.05;
		var zoomSensitivity = 0.1;

		// Record mouse location when mouse is pressed down.
		canvas.onmousedown =
				function(e) {
						lastX = e.clientX;
						lastY = e.clientY;
				};

		// Change rotation offset value based on the change in mouse position from
		// when the mouse button is pressed down to when the mouse button is released.
		canvas.onmouseup =
				function(e) {
						deltaY = e.clientY - lastY + deltaY;
						deltaX = e.clientX - lastX + deltaX;
						if (!isTeaspoon) {
								updateSuperquadric();
						} else {
								updateTeaspoon();
						}
						display();
				};

		// Update parameters of superquadric.
		// Some elements have 2 event handlers just to make the
		// interface a little more responsive.
    document.getElementById("superquadric-constant-n1").onchange =
    		function(e) {
						n1 = getSuperquadricConstants().n1;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-n2").onchange =
        function(e) {
						n2 = getSuperquadricConstants().n2;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-a").onclick =
        function(e) {
						a = getSuperquadricConstants().a;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-a").onchange =
				function(e) {
						a = getSuperquadricConstants().a;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
				};

		document.getElementById("superquadric-constant-b").onclick =
        function(e) {
						b = getSuperquadricConstants().b;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-b").onchange =
        function(e) {
						b = getSuperquadricConstants().b;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-c").onclick =
        function(e) {
						c = getSuperquadricConstants().c;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("superquadric-constant-c").onchange =
        function(e) {
						c = getSuperquadricConstants().c;
						if (!isTeaspoon) {
								updateSuperquadric();
								display();
						}
        };

		document.getElementById("foreground-color").onchange =
        function(e) {
						if (!isTeaspoon) {
								updateSuperquadric();
						} else {
								updateTeaspoon();
						}
						display();
        };

		document.getElementById("fov").onclick =
        function(e) {
						fov = getSuperquadricConstants().fov;
						if (!isTeaspoon) {
								updateSuperquadric();
						} else {
								updateTeaspoon();
						}
						display();
        };

		document.getElementById("fov").onchange =
        function(e) {
						fov = getSuperquadricConstants().fov;
						if (!isTeaspoon) {
								updateSuperquadric();
						} else {
								updateTeaspoon();
						}
						display();
        };

		document.getElementById("type").onchange =
        function(e) {
						isTeaspoon = getSuperquadricConstants().type;
						deltaX = 0;
						deltaY = 0;
						if (!isTeaspoon) {
								updateSuperquadric();
						} else {
								updateTeaspoon();
						}
						display();
        };

		//If the user presses the left arrow key, shift camera to the left
		window.onkeydown = function (e) {
				var key = String.fromCharCode(e.keyCode);
	  				switch(e.keyCode) {

				// < > keys provide a zoom effect by scaling the camera matrix.
				case 190:
								transCamera = mult(scalem(1+zoomSensitivity,1+zoomSensitivity,1+zoomSensitivity),transCamera);
						display();
								break;
				case 188:
								transCamera = mult(scalem(1-zoomSensitivity,1-zoomSensitivity,1-zoomSensitivity),transCamera);
						display();
								break;

				// Arrow keys translate the camera in the specified direction.
				case 37:
	            	transCamera = mult(translate(panSensitivity,0,0),transCamera);
						display();
	            	break;
				case 39:
	            	transCamera = mult(translate(-1*panSensitivity,0,0),transCamera);
						display();
	            	break;
				case 38:
								transCamera = mult(translate(0,-1*panSensitivity,0),transCamera);
						display();
								break;
				case 40:
								transCamera = mult(translate(0,panSensitivity,0),transCamera);
						display();
								break;
				}
		}
}

// Function for querying the current superquadric constants: a, b, c, d, n1, n2
function getSuperquadricConstants() {
    return {
        n1: parseFloat(document.getElementById("superquadric-constant-n1").value),
        n2: parseFloat(document.getElementById("superquadric-constant-n2").value),
        a: parseFloat(document.getElementById("superquadric-constant-a").value),
				b: parseFloat(document.getElementById("superquadric-constant-b").value),
				c: parseFloat(document.getElementById("superquadric-constant-c").value),
				fov: parseFloat(document.getElementById("fov").value),
				type: parseFloat(document.getElementById("type").value)
    }
}

// Applies rotations to a mesh.
function rotateObject() {

		// Apply user-specified azimuth rotation by rotating each point.
		for (var i = 0; i < points.length; i++) {
				var axis = vec3(0.0, 1.0, 0.0);
				var rot = rotate(deltaX/3, axis);
				points[i] = mult(rot, points[i]);
		}

		// Apply user-specified altitude rotation by rotating each point.
		for (var i = 0; i < points.length; i++) {
				var axis = vec3(1.0, 0.0, 0.0);
				var rot = rotate(deltaY/3, axis);
				points[i] = mult(rot, points[i]);
		}
}

// Function for computing the parametric points in the superquadric
// and then loading them into a gl buffer
function updateSuperquadric(){

		points = [];

		// Calculate points for one of the two mesh directions for the superquadric.
		for (var i = 0; i < size; i++) {
				var u = 2 * (((Math.PI/size) * i) - Math.PI);
				for (var j = 0; j < size; j++) {
						var v = 2 * (((Math.PI/size) * j) - Math.PI/2.0);
						var x = a * Math.sign(Math.cos(v)) * Math.pow(Math.abs(Math.cos(v)), 2/n1) * Math.sign(Math.cos(u)) * Math.pow(Math.abs(Math.cos(u)), 2/n2);
						var y = b * Math.sign(Math.cos(v)) * Math.pow(Math.abs(Math.cos(v)), 2/n1) * Math.sign(Math.sin(u)) * Math.pow(Math.abs(Math.sin(u)), 2/n2);
						var z = c * Math.sign(Math.sin(v)) * Math.pow(Math.abs(Math.sin(v)), 2/n1);
						points.push(vec4(x, y, z, 1.0));
				}
		}

		// Calculate points for the other mesh direction for the superquadric by
		// switching the order in which parameters u and v are sampled.
		for (var i = 0; i < size; i++) {
				var v = 2 * (((Math.PI/size) * i) - Math.PI/2.0);
		  	for (var j = 0; j < size; j++) {
						var u = 2 * (((Math.PI/size) * j) - Math.PI);
						var x = a * Math.sign(Math.cos(v)) * Math.pow(Math.abs(Math.cos(v)), 2/n1) * Math.sign(Math.cos(u)) * Math.pow(Math.abs(Math.cos(u)), 2/n2);
						var y = b * Math.sign(Math.cos(v)) * Math.pow(Math.abs(Math.cos(v)), 2/n1) * Math.sign(Math.sin(u)) * Math.pow(Math.abs(Math.sin(u)), 2/n2);
						var z = c * Math.sign(Math.sin(v)) * Math.pow(Math.abs(Math.sin(v)), 2/n1);
						points.push(vec4(x, y, z, 1.0));
				}
		}

		// Apply rotations.
		rotateObject();

		// Apply user-specified fov by scaling each point.
		for (var i = 0; i < points.length; i++) {
				var scale = scalem(3/fov, 3/fov, 1);
				points[i] = mult(scale, points[i]);
		}

		// Bind vertex buffer.
		var vBuffer = gl.createBuffer();
	  gl.bindBuffer( gl.ARRAY_BUFFER, vBuffer );
	  gl.bufferData( gl.ARRAY_BUFFER, flatten(points), gl.STATIC_DRAW );

	  var vPosition = gl.getAttribLocation( programId, "vPosition" );
	  gl.vertexAttribPointer( vPosition, 4, gl.FLOAT, false, 0, 0 );
	  gl.enableVertexAttribArray( vPosition );
}

function updateTeaspoon() {

		points = [];

		// Calculate points for all 16 patches of the teaspoon.
		for (var i = 0; i < 16; i++) {

				var indices = teaspoonIndices[i];
				var x = [];
				var y = [];
				var z = [];

				// For all control points in a patch, store
				// x, y and z data in temporary arrays.
				for (var j = 0; j < 4; j++) {
						var indexRow = indices[j];

						for (var k = 0; k < 4; k++) {

								var index = indexRow[k];
								var point = teaspoonPoints[index];
								x.push(point[0]);
								y.push(point[1]);
								z.push(point[2]);
						}
				}

				// Create matrices out of x, y and z arrays.
				var xmat = mat4 (x[0],  x[1],  x[2],  x[3],
												 x[4],  x[5],  x[6],  x[7],
											   x[8],  x[9],  x[10], x[11],
											   x[12], x[13], x[14], x[15]);

			  var ymat = mat4 (y[0],  y[1],  y[2],  y[3],
											   y[4],  y[5],  y[6],  y[7],
											   y[8],  y[9],  y[10], y[11],
												 y[12], y[13], y[14], y[15]);

			  var zmat = mat4 (z[0],  z[1],  z[2],  z[3],
						 						 z[4],  z[5],  z[6],  z[7],
							 	 	 			 z[8],  z[9],  z[10], z[11],
								 				 z[12], z[13], z[14], z[15]);

				// Calculate points of bezier surface patch by
				// varying parameters s and t.

				// Calculate points for one mesh direction.
				// This allows the patch to be drawn as
				// a grid of quadrilaterals.
				for (var j = 0; j < 4; j++) {
						var s = j/3;
						var sVec = vec4(Math.pow(s, 3), Math.pow(s, 2), s, 1);
						for (var k = 0; k < 4; k++) {
								var t = k/3;
								var tVec = vec4(Math.pow(t, 3), Math.pow(t, 2), t, 1);
								var x = dot(sVec, mult(sVec, mult(mult(mult(mt, xmat), m), tVec)));
								var y = dot(sVec, mult(sVec, mult(mult(mult(mt, ymat), m), tVec)));
								var z = dot(sVec, mult(sVec, mult(mult(mult(mt, zmat), m), tVec)));
								var vec = vec4(x, y+1, z, 1.0);
								points.push(vec);
						}
				}

				// Calculate points for the other mesh direction.
				for (var j = 0; j < 4; j++) {
						var t = j/3;
						var tVec = vec4(Math.pow(t, 3), Math.pow(t, 2), t, 1);
						for (var k = 0; k < 4; k++) {
								var s = k/3;
								var sVec = vec4(Math.pow(s, 3), Math.pow(s, 2), s, 1);
								var x = dot(sVec, mult(sVec, mult(mult(mult(mt, xmat), m), tVec)));
								var y = dot(sVec, mult(sVec, mult(mult(mult(mt, ymat), m), tVec)));
								var z = dot(sVec, mult(sVec, mult(mult(mult(mt, zmat), m), tVec)));
								var vec = vec4(x, y+1, z, 1.0);
								points.push(vec);
						}
				}
		}

		// Apply rotations.
		rotateObject();

		// Apply user-specified fov by scaling each point.
		for (var i = 0; i < points.length; i++) {
				var scale = scalem(1/fov, 1/fov, 1);
				points[i] = mult(scale, points[i]);
		}

		// Bind vertex buffer.
		var vBuffer = gl.createBuffer();
		gl.bindBuffer( gl.ARRAY_BUFFER, vBuffer );
		gl.bufferData( gl.ARRAY_BUFFER, flatten(points), gl.STATIC_DRAW );

		var vPosition = gl.getAttribLocation( programId, "vPosition" );
		gl.vertexAttribPointer( vPosition, 4, gl.FLOAT, false, 0, 0 );
		gl.enableVertexAttribArray( vPosition );
}

// Function for querying the current wireframe color
function getWireframeColor() {
    var hex = document.getElementById("foreground-color").value;
    var red = parseInt(hex.substring(1, 3), 16);
    var green = parseInt(hex.substring(3, 5), 16);
    var blue = parseInt(hex.substring(5, 7), 16);
    return vec3(red / 255.0, green / 255.0, blue / 255.0);
}

window.onload = function() {
    // Find the canvas on the page
    canvas = document.getElementById("gl-canvas");

    // Initialize a WebGL context
    gl = WebGLUtils.setupWebGL(canvas);
    if (!gl) {
        alert("WebGL isn't available");
    }

    // Load shaders
    programId = initShaders(gl, "vertex-shader", "fragment-shader");
    gl.useProgram(programId);

    // Set up events for the HTML controls
    initControlEvents();

		//Compute superquadric points and put them in gl Buffers
		updateSuperquadric();

		//Draw the superquadric
		display();
};

//This function updates the view matrix and then draws the superquadric
function display() {
	  gl.clear( gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT );
		viewMatrixLoc = gl.getUniformLocation( programId, "viewMatrix" );
		viewMatrix = transCamera;
		gl.uniformMatrix4fv( viewMatrixLoc, false, flatten(viewMatrix) );

		var r = getWireframeColor()[0];
		var g = getWireframeColor()[1];
		var b = getWireframeColor()[2];
		var colorVec = vec4(r, g, b, 1);

		fColor = gl.getUniformLocation(programId, "fColor");
		gl.uniform4fv(fColor, colorVec);

		// Draw line strips for teaspoon.
		if (isTeaspoon) {

				// 16 total patches.
				for (var i = 0; i < 16; i++) {

						// Draw one direction of mesh.
						for (var j = 0; j < 4; j++) {
								gl.drawArrays(gl.LINE_STRIP, j*4+(32*i), 4);
						}

						// Draw the other direction of mesh.
						for (var j = 0; j < 4; j++) {
								gl.drawArrays(gl.LINE_STRIP, j*4+16+(32*i), 4);
						}
				}

		// Draw line strips for superquadric.
		} else {

			  // Draw one direction of mesh.
				for (var i = 0; i < size; i++) {
						gl.drawArrays(gl.LINE_STRIP, i*size, size);
				}

				// Draw the other direction of mesh.
				for (var i = 0; i < size; i++) {
						gl.drawArrays(gl.LINE_STRIP, i*size+points.length/2, size);
				}
		}
}
