package fileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import levelAI.LevelObject;

public class FileReader {
	/* readFile(String fileName)
	 * purpose: the purpose of this method is to take in a file
	 * and output a level object that can be rendered using lwjgl
	 * the file is in the format:
	 * #,#,# ... n
	 * #,#,#
	 * #,#,#
	 * .
	 * .
	 * .
	 * m
	 * where # is a number greater than or equal to zero that represents
	 * the type of block located at said location.
	 * n is the number of columns the level has and 
	 * m is the number of rows 
	 */
	public static LevelObject readFile(String fileName) {
		Scanner file;
		try {
			file = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		String currLine;
		ArrayList<ArrayList<Block>> levelBlocks = new ArrayList<>();
		while(file.hasNextLine()) {
			currLine = file.nextLine();
			ArrayList<Block> numbers = new ArrayList<>();
			String[] blocks = currLine.split(",");
			for(String block : blocks) {
				numbers.add(new Block(Integer.parseInt(block)));
			}
			levelBlocks.add(numbers);
		}
		file.close();
		return new LevelObject(levelBlocks);
	}
}
