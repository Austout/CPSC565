/**
 * Austin Eaton austin.eaton@ucalgary.ca
 * CPSC 565, Taught By: Arkady (Eric) Eidelberg
 * The following project is the implementation component
 * to a semester long project. For an overview and explination
 * of each component please read the accompinying paper titled
 * "Procedural Level Generation Using an Adversarial Algorithm"
 * 
 * most of the setup code for LWJGL in this was adapted from:
 * https://www.lwjgl.org/guide
 */

package levelDisplay;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import adversary.GAadversary;
import fileReader.FileReader;
import levelAI.LevelGen;
import levelAI.LevelObject;
import util.KeyboardHandler;

public class Main {
	private static final int FLOATSIZE = 4;
	static LevelObject myLevel;
	// The window handle
	private long window;
	private final static float[] colorLookUpTable = {0,0,1, 
								0,1,0,
								1,0,0,
								1f,0.6f,0};
	public void run() {

		init();

		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		int xSize = 1500;
		int ySize = 1500;
		myLevel = FileReader.readFile("res/level3.txt");
		if(myLevel.rows > myLevel.columns) {
			xSize = (int) (((float)xSize) * ((float) myLevel.columns /(float)myLevel.rows));
		}else {
			ySize = (int) (((float)ySize) * ((float)myLevel.rows /(float)myLevel.columns));
		}
		advasary = new GAadversary(myLevel, 10);
		myLevel = advasary.runAdvasary(300, 2);// number of times to run and the number of movements to (possibly) add each itteration
		for(int i = 0; i < 20 ; i++) {
			myLevel = LevelGen.mutateLevel(myLevel,advasary);
			advasary.level = myLevel;//set the level the GA uses to train on to the best level.
			myLevel = advasary.runAdvasary(90, 2);
			System.out.println(i);
		}
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		// Create the window
		window = glfwCreateWindow(xSize, ySize, "CPSC 565 Project!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);
	}
	private GLFWKeyCallback keyCallback;
	public static void rightKeyDown() {
		myLevel = advasary.runAdvasary(3, 2);
		Geometry temp = new Geometry();
		generateLevel(temp);
		map = temp;
	}
	static Geometry map;
	static GAadversary advasary;
	private void loop() {
		
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		// Set the clear color
		glClearColor(0.2f, 0.2f, 0.2f, 0.0f);
		
		glfwSetKeyCallback(window, keyCallback = new KeyboardHandler());		
		
		
		map = new Geometry();
		generateLevel(map);
		int program = 0;
		try {
			program = InitializeShaders("shaders/vertex.glsl","shaders/fragment.glsl");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
			
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

			GL20.glUseProgram(program);
			
			advasary.render();

			glBindVertexArray(map.vertexArray);

			glDrawArrays(GL_TRIANGLES, 0, map.elementCount);
			// reset state to default (no shader or geometry bound)
			// glActiveTexture(0);
			
			glBindVertexArray(0);
			GL20.glUseProgram(0);

			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
		}
	}
	private static void generateLevel(Geometry map) {
		float verticalSize = 2.0f / myLevel.rows;
		float horizontalSize = 2.0f / myLevel.columns;
		ArrayList<Float> points = new ArrayList<>();
		ArrayList<Float> colors = new ArrayList<>();
		for(int i = 0; i < myLevel.columns; i++) {
			for(int j = 0; j < myLevel.rows; j++) {
				int temp = j;
				j = i;
				i=temp;
				int type = myLevel.getBlockAt(i, j).getType() * 3 -3;
				if(myLevel.getBlockAt(i, j).getTimesSteppedOn() != 0 && myLevel.getBlockAt(i, j).getType() == 0) {
//					type = 3*3;
//					float steps = myLevel.getBlockAt(i, j).getTimesSteppedOn();
//					float mut = 0.0002f;
//					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize));
//					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize) - verticalSize);
//					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize) - verticalSize);
//
//					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize) - verticalSize);
//					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize));
//					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize));
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					colors.add(colorLookUpTable[type]/(mut * steps));colors.add(colorLookUpTable[type+1]/(mut * steps));colors.add(colorLookUpTable[type+2]/(mut * steps));	
//					type = -3;
				}
				if(myLevel.getBlockAt(i, j).getType() != 0) {
					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize));
					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize) - verticalSize);
					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize) - verticalSize);

					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize) - verticalSize);
					points.add(1 - (j*horizontalSize) - horizontalSize);points.add(1 - (i*verticalSize));
					points.add(1 - (j*horizontalSize));points.add(1 - (i*verticalSize));
					
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
					
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
					colors.add(colorLookUpTable[type]);colors.add(colorLookUpTable[type+1]);colors.add(colorLookUpTable[type+2]);	
				}
				temp = j;
				j = i;
				i=temp;
			}
		}
		advasary.init();
		float[] vertices = new float[points.size() + advasary.vertices.length];
		float[] colorsArray = new float[colors.size() + advasary.colorsArray.length];
//		for(int i = 0; i < advasary.vertices.length;i++) {
//			vertices[i] = advasary.vertices[i];
//		}
//		for(int i = 0; i < advasary.colorsArray.length;i++) {
//			colorsArray[i] = advasary.colorsArray[i];
//		}
		for(int i = 0; i < points.size();i++) {
			vertices[i/*+advasary.vertices.length*/] = points.get(i);
		}
		for(int i = 0; i < colors.size();i++) {
			colorsArray[i/*+advasary.colorsArray.length*/] = colors.get(i);
		}
		
		InitializeVAO(map);
		LoadGeometry(map, vertices, colorsArray, (points.size() /*+ advasary.vertices.length*/)/ 2);
	}

	//adapted from https://github.com/mattdesl/lwjgl-basics/wiki/ShaderProgram-Utility
	protected int compileShader(String source, int type) throws Exception {
		//create a shader object
		int shader = glCreateShader(type);
		//pass the source string
		glShaderSource(shader, source);
		//compile the source
		glCompileShader(shader);

		//if info/warnings are found, append it to our shader log
		String infoLog = glGetShaderInfoLog(shader,
				glGetShaderi(shader, GL_INFO_LOG_LENGTH));
		
		//if the compiling was unsuccessful, throw an exception
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
			throw new Exception("Failure in compiling " +type+ ". Error log:\n" + infoLog);

		return shader;
	}
	//adapted from https://github.com/mattdesl/lwjgl-basics/wiki/ShaderProgram-Utility
	int InitializeShaders(String vertexString, String fragmentString) throws Exception
	{
		// load shader source from files
		String vertexSource = LoadSource( vertexString);
		String fragmentSource = LoadSource(fragmentString);
		if (vertexSource.equals("")|| fragmentSource.equals("")) return 0;

		// compile shader source into shader objects
		int vertex = compileShader(vertexSource, GL_VERTEX_SHADER);
		int fragment = compileShader(fragmentSource, GL_FRAGMENT_SHADER);
		int program = glCreateProgram();

		glAttachShader(program, vertex);
		glAttachShader(program, fragment);
		// link shader program
		glLinkProgram(program);
		
		String infoLog = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));
		
		
		//if the link failed, throw some sort of exception
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
			throw new Exception(
					"Failure in linking program. Error log:\n" + infoLog);
		glDeleteShader(vertex);
		glDeleteShader(fragment);

		// check for OpenGL errors and return false if error occurred
		return program;
	}
	private String LoadSource(String vertexString) {
		try {
			Scanner file = new Scanner(new File(vertexString));
			String returnString = "";
			while(file.hasNextLine()) {
				returnString += file.nextLine() + "\n";
			}
			file.close();
			return returnString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) {
		new Main().run();
	}

	public static boolean InitializeVAO(Geometry geometry) {

		final int VERTEX_INDEX = 0;
		final int COLOUR_INDEX = 1;

		// Generate Vertex Buffer Objects
		// create an array buffer object for storing our vertices
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
	    GL15.glGenBuffers(buffer);
	    geometry.vertexBuffer = buffer.get(0);
//	    geometry.vertexBuffer = glGenBuffers();
		// create another one for storing our colours
		// glGenBuffers(1, &geometry->colourBuffer);
	    IntBuffer buffer2 = BufferUtils.createIntBuffer(1);
	    GL15.glGenBuffers(buffer2);
	    geometry.colourBuffer = buffer2.get(0);
//		geometry.colourBuffer = glGenBuffers();

		// Set up Vertex Array Object
		// create a vertex array object encapsulating all our vertex attributes
		geometry.vertexArray = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(geometry.vertexArray);

		// associate the position array with the vertex array object
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, geometry.vertexArray);
		glVertexAttribPointer(VERTEX_INDEX, 2, GL_FLOAT, false, FLOATSIZE*2, 0);

		glEnableVertexAttribArray(VERTEX_INDEX);

		// associate the colour array with the vertex array object
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, geometry.colourBuffer);
		glVertexAttribPointer(COLOUR_INDEX, 3, GL_FLOAT, false, FLOATSIZE*3, 0);

		glEnableVertexAttribArray(COLOUR_INDEX);

		// unbind our buffers, resetting to default state
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		return !CheckGLErrors();
	}

	public static boolean LoadGeometry(Geometry geometry, float[] vertices, float[] colorCoords, int elementCount) {
		geometry.elementCount = elementCount;

		// create an array buffer object for storing our vertices
		glBindBuffer(GL_ARRAY_BUFFER, geometry.vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		// create another one for storing our texture coordinates
		glBindBuffer(GL_ARRAY_BUFFER, geometry.colourBuffer);
		glBufferData(GL_ARRAY_BUFFER, colorCoords, GL_STATIC_DRAW);

		// Unbind buffer to reset to default state
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// check for OpenGL errors and return false if error occurred
		return !CheckGLErrors();
	}

	static boolean CheckGLErrors() {
		boolean error = false;
		glGetError();
		for (int flag = glGetError(); flag != GL_NO_ERROR; flag = glGetError()) {
			System.out.println("OpenGL ERROR:  ");
			switch (flag) {
			case GL_INVALID_ENUM:
				System.out.println("GL_INVALID_ENUM");
				break;
			case GL_INVALID_VALUE:
				System.out.println("GL_INVALID_VALUE");
				break;
			case GL_INVALID_OPERATION:
				System.out.println("GL_INVALID_OPERATION");
				break;
			case GL_INVALID_FRAMEBUFFER_OPERATION:
				System.out.println("GL_INVALID_FRAMEBUFFER_OPERATION");
				break;
			case GL_OUT_OF_MEMORY:
				System.out.println("GL_OUT_OF_MEMORY");
				break;
			default:
				System.out.println("[unknown error code]");
			}
			error = true;
		}
		return error;
	}


}