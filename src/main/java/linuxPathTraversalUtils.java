
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import linux.path.traversal.enums.Type;

/**
 * Currently unused ,but some code arguably could be refactored and moved here
 * 
 * @author jibrankhan
 *
 */

public class linuxPathTraversalUtils {

	/**
	 * Used to remove the last unwanted slash in a command argument.
	 * 
	 * cd a/b/c/ -----> a/b/c becomes the argument that is used for processing
	 * 
	 * @param args
	 */
	public static void removeLastSlash(List<String> args) {

		for (int i = 0; i < args.size(); i++) {

			String argument = args.get(i);

			if ((("/").equals(argument) == false) && argument.charAt(argument.length() - 1) == '/') {

				args.set(i, argument.substring(0, argument.length() - 1));
			}
		}
	}

	public static File getParentDirectory(File directory) {

		File parentDirectory;

		List<String> destPathFoldersList = Arrays.asList(directory.getAbsolutePath().split("/"));

		String parentDirAbsPath = "";// String.join("/",
										// destPathFoldersList.subList(0,
										// destPathFoldersList.size() - 1));

		if (destPathFoldersList.size() == 2 && destPathFoldersList.get(0).equals("")) {
			// This means the parent directory has got to
			return File.getRootDirectory();

		}

		String parentDirName = destPathFoldersList.get(destPathFoldersList.size() - 2);

		parentDirAbsPath = String.join("/", destPathFoldersList.subList(0, destPathFoldersList.size() - 1));
		parentDirectory = new File(parentDirName, Type.DIRECTORY.toString(), parentDirAbsPath);

		return parentDirectory;
	}

	public static void makeDirectory(File destDirectory, File parentDirectoryOfDestDirectory) {

		if (FileSystem.currentFS.get(parentDirectoryOfDestDirectory).contains(destDirectory)) {
			System.out.println("ERR: DIRECTORY ALREADY EXISTS");
			return;
		}

		FileSystem.currentFS.get(parentDirectoryOfDestDirectory).add(destDirectory);
		FileSystem.currentFS.put(destDirectory, new ArrayList<File>());
		System.out.println("SUCC : " + destDirectory.getAbsolutePath() + " CREATED SUCCESSFULLY");
	}

	public static void displayAllSubDirectories(File directory) {

		if (FileSystem.currentFS.containsKey(directory)) {

			List<File> subDirectories = FileSystem.currentFS.get(directory);

			String subDir = "";
			for (int i = 0; i < subDirectories.size(); i++) {
				subDir = subDir + subDirectories.get(i).getName() + "  ";
			}

			System.out.println(subDir);

		} else {
			System.out.println(
					"ERR: CAN'T DISPLAY SUBDIRECTORIES AS  , DIR" + directory.getAbsolutePath() + " NOT FOUND");
		}

	}

}
