package util;
import org.lwjgl.glfw.GLFWKeyCallback;

import levelDisplay.Geometry;
import levelDisplay.Main;

import static org.lwjgl.glfw.GLFW.*;

// code used from : https://tutorialedge.net/java/lwjgl3/lwjgl-3-keyboard-input-handler-tutorial/
public class KeyboardHandler extends GLFWKeyCallback{

  public static boolean[] keys = new boolean[65536];

  // The GLFWKeyCallback class is an abstract method that
  // can't be instantiated by itself and must instead be extended
  // 
  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    // TODO Auto-generated method stub
    keys[key] = action != GLFW_RELEASE;
	if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)) {
		Main.rightKeyDown();
	}
  }

  // boolean method that returns true if a given key
  // is pressed.
  public static boolean isKeyDown(int keycode) {
    return keys[keycode];
  }
	
}