
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import linux.path.traversal.enums.Type;

public class Terminal {

	public static File pwd;

	// List of commands that we support
	public static List<Command> validCommands = new ArrayList<Command>();

	// List of commands executed in a session
	public static List<Command> sessionHistory = new ArrayList<Command>();

	void printHistory() {
		// PlaceHolder to print the history of the terminal
	}

	void initialiseCommandList() {

		Command c = new Command("mkdir", null, null);
		validCommands.add(c);

		c = new Command("ls", null, null);
		validCommands.add(c);

		c = new Command("cd", null, null);
		validCommands.add(c);

		c = new Command("rm", null, null);
		validCommands.add(c);

		c = new Command("pwd", null, null);
		validCommands.add(c);

		c = new Command("session", null, null);
		validCommands.add(c);
	}

	public Terminal() {
		pwd = new File("/", Type.DIRECTORY.toString(), "/");
		initialiseCommandList();
	}

	public Command getCommandFromInput(String input) {

		Command c = new Command();

		// Regex to take care of varying length of spaces between commands
		c.setCommand(input.split("\\s+")[0]);

		List<String> arguments = Arrays.asList(Arrays.copyOfRange(input.split("\\s+"), 1, input.split("\\s+").length));

		/*
		 * removing the last slashes '/' Ex: cd a/b/c/ then remove the last
		 * slash for processing and logic has been written that way.
		 */
		linuxPathTraversalUtils.removeLastSlash(arguments);

		c.setArgs(arguments);

		return c;
	}

	void executePwd(Command c) {

		System.out.println("PATH: " + pwd.getAbsolutePath());

	}

	/**
	 * Restoring the session state to a what it was at the start of the session
	 * which in this case is particularly "/" directory state.
	 * 
	 * TBD: An extension of this would involve storing terminal state globally
	 * somewhere , retrieving it at the start of the terminal session and
	 * working on it .
	 * 
	 */
	void executeSessionCommand(Command command) {
		pwd = File.getRootDirectory();
		this.initialiseCommandList();

		FileSystem.restoreFileSystemStateToSessionStart();

	}

	// void executeMkdir(Command c) {
	//
	// // making only in the present working directory
	//
	// File directoryToBeCreated = null;
	//
	// String absolutePathOfDirectoryBeingCreated = "";
	// String nameOfDirectoryToBeCreated = c.getArgs().get(0);
	//
	// if (pwd.equals(File.getRootDirectory())) {
	// absolutePathOfDirectoryBeingCreated = pwd.getAbsolutePath() +
	// nameOfDirectoryToBeCreated;
	// } else {
	// absolutePathOfDirectoryBeingCreated = pwd.getAbsolutePath() + "/" +
	// nameOfDirectoryToBeCreated;
	// }
	//
	// directoryToBeCreated = new File(nameOfDirectoryToBeCreated,
	// Type.DIRECTORY.toString(),
	// absolutePathOfDirectoryBeingCreated);
	//
	// if (FileSystem.currentFS.containsKey(pwd)) {
	//
	// FileSystem.currentFS.get(pwd).add(directoryToBeCreated);
	//
	// } else {
	//
	// List<File> subDirectories = new ArrayList<File>();
	// subDirectories.add(directoryToBeCreated);
	// FileSystem.currentFS.put(pwd, subDirectories);
	// }
	//
	// FileSystem.currentFS.put(directoryToBeCreated, new ArrayList<File>());
	//
	// // printFileSystem();
	// }
	//

	File getParentDirectory(File directory) {

		File parentDirectory;

		List<String> destPathFoldersList = Arrays.asList(directory.getAbsolutePath().split("/"));

		String parentDirAbsPath = "";// String.join("/",
										// destPathFoldersList.subList(0,
										// destPathFoldersList.size() - 1));
		String parentDirName = destPathFoldersList.get(destPathFoldersList.size() - 2);

		if (destPathFoldersList.size() == 2 && destPathFoldersList.get(0).equals("")) {
			// This means the parent directory has got to
			return File.getRootDirectory();

		}

		parentDirAbsPath = String.join("/", destPathFoldersList.subList(0, destPathFoldersList.size() - 1));
		parentDirectory = new File(parentDirName, Type.DIRECTORY.toString(), parentDirAbsPath);

		return parentDirectory;
	}

	void makeDirectory(File directory) {

		if (FileSystem.currentFS.containsKey(directory)) {
			System.out.println("THIS DIRECTORY ALREADY EXISTS");
		}

		else {

			System.out.println("------mkdmn");
			File parentDirectory = getParentDirectory(directory);

			if (FileSystem.currentFS.containsKey(parentDirectory)) {

				FileSystem.currentFS.get(parentDirectory).add(directory);
				FileSystem.currentFS.put(directory, new ArrayList<File>());
			}
		}
	}

	void executeMkdir(Command command) {

		File destinationDirectory = new File("", Type.DIRECTORY.toString(), "");
		String destinationDirectoryName = "";
		String destinationDirectoryAbsPath = "";

		String destinationDirectoryPathFromCommand = "";

		if (command.getArgs().isEmpty()) {
			// print error
			return;
		}

		destinationDirectoryPathFromCommand = command.getArgs().get(0);

		if (("/").equals(destinationDirectoryPathFromCommand)) {

			// Print error that you can't create a root directory
			return;
		}

		// absolute path
		if (("/").equals(Character.toString(destinationDirectoryPathFromCommand.charAt(0)))) {

			List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

			String absolutePathToDestinationDirectory = destPathFoldersList.get(destPathFoldersList.size() - 1);
			String pathToDestExcludingDestinationDirectory = null;

			if (destPathFoldersList.size() == 1) {
				pathToDestExcludingDestinationDirectory = "/";
			} else {
				pathToDestExcludingDestinationDirectory = String.join("/",
						destPathFoldersList.subList(0, destPathFoldersList.size()));
			}

			destinationDirectory.setAbsolutePath(pathToDestExcludingDestinationDirectory);
			destinationDirectory.setType(Type.DIRECTORY.toString());
			destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));

			if (FileSystem.currentFS.containsKey(destinationDirectory)) {
				// Then make the directoruy
				makeDirectory(destinationDirectory);
				return;
			} else {
				System.out.println("ERR: " + destinationDirectory.getAbsolutePath() + " does not exist");
				return;
			}

		}

		// relative path
		else {

			System.out.println("HEEEEREEE");

			List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

			if (pwd.equals(File.getRootDirectory())) {
				destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + destinationDirectoryPathFromCommand);
			} else {
				destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + "/" + destinationDirectoryPathFromCommand);
			}

			// case: ls xyz
			if (destPathFoldersList.size() == 1) {

				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destinationDirectoryPathFromCommand);

			} else {

				/**
				 * command : ls a/b/c then destinationDirectoryName = c
				 */
				destinationDirectoryName = destinationDirectoryPathFromCommand
						.split("/")[destinationDirectoryPathFromCommand.split("/").length - 1];
				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destinationDirectoryName);
			}

			// make the directory
			makeDirectory(destinationDirectory);

			return;

		}

	}

	void displayAllSubDirectories(File directory) {

		if (FileSystem.currentFS.containsKey(directory)) {

			List<File> subDirectories = FileSystem.currentFS.get(directory);

			String subDir = "";
			for (int i = 0; i < subDirectories.size(); i++) {
				subDir = subDir + subDirectories.get(i).getName() + "  ";
			}

			System.out.println(subDir);

		} else {
			System.out.println("ERR: Can't ls , dir not found");
		}

	}

	/**
	 * This command is used to print all the subDirectories in a directory
	 * 
	 * @param command
	 */
	void executeLs(Command command) {

		// Directory whose first level sub directories are to be shown
		File destinationDirectory = new File("", Type.DIRECTORY.toString(), "");
		String destinationDirectoryName = "";
		String destinationDirectoryAbsPath = "";

		String destinationDirectoryPathFromCommand = "";

		if (command.getArgs().isEmpty()) {
			displayAllSubDirectories(pwd);
			return;
		}

		destinationDirectoryPathFromCommand = command.getArgs().get(0);

		// If root
		if (("/").equals(destinationDirectoryPathFromCommand)) {

			destinationDirectory = File.getRootDirectory();
			displayAllSubDirectories(destinationDirectory);

			return;
		}

		// CASE: Absolute Path
		if (("/").equals(Character.toString(destinationDirectoryPathFromCommand.charAt(0)))) {

			List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

			String absolutePathToDestinationDirectory = destPathFoldersList.get(destPathFoldersList.size() - 1);
			String pathToDestinationDirectory = null;

			if (destPathFoldersList.size() == 1) {
				pathToDestinationDirectory = "/";
			} else {
				pathToDestinationDirectory = String.join("/",
						destPathFoldersList.subList(0, destPathFoldersList.size()));
			}

			destinationDirectory.setAbsolutePath(pathToDestinationDirectory);
			destinationDirectory.setType(Type.DIRECTORY.toString());
			destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));

			displayAllSubDirectories(destinationDirectory);

			return;
		}

		// CASE: Relative Path
		else {

			System.out.println("HEEEEREEE");
			List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

			if (pwd.equals(File.getRootDirectory())) {
				destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + destinationDirectoryPathFromCommand);
			} else {
				destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + "/" + destinationDirectoryPathFromCommand);
			}

			// case: ls xyz
			if (destPathFoldersList.size() == 1) {

				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destinationDirectoryPathFromCommand);

			} else {

				/**
				 * command : ls a/b/c then destinationDirectoryName = c
				 */
				destinationDirectoryName = destinationDirectoryPathFromCommand
						.split("/")[destinationDirectoryPathFromCommand.split("/").length - 1];
				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destinationDirectoryName);
			}

			displayAllSubDirectories(destinationDirectory);

			return;

		}

	}

	void executeCd(Command command) {

		File startingDirectory = new File("", Type.DIRECTORY.toString(), "");
		File destinationDirectory = new File("", Type.DIRECTORY.toString(), "");

		String destinationDirectoryName = "";
		String destinationDirectoryAbsPath = "";

		if (command.getArgs().size() == 0) {
			// No support for changing to ~ . This would happen in a normal
			// terminal.
			// Can be extended as per use case

		}

		else {

			// This path includes the destination directory as well
			String destinationDirectoryPathFromCommand = command.getArgs().get(0);

			// case: cd ..
			if (("..").equals(destinationDirectoryPathFromCommand)) {

				// can't go beyond root
				if (pwd.equals(File.getRootDirectory())) {

				} else {
					List<String> directoriesFromRootToPwd = Arrays.asList(pwd.getAbsolutePath().trim().split("/"));

					// CASE: "/a".split("/")
					if (directoriesFromRootToPwd.size() == 2 && ("").equals(directoriesFromRootToPwd.get(0))) {
						pwd = File.getRootDirectory();

					}

					else {
						// going level up in directory heirarchy
						directoriesFromRootToPwd = Arrays.asList(pwd.getAbsolutePath().split("/"));
						destinationDirectoryName = pwd.getAbsolutePath().split("/")[directoriesFromRootToPwd.size()
								- 2];
						destinationDirectoryAbsPath = String.join("/",
								directoriesFromRootToPwd.subList(0, directoriesFromRootToPwd.size() - 1));
						pwd = new File(destinationDirectoryName, Type.DIRECTORY.toString(),
								destinationDirectoryAbsPath);
					}

				}

				return;

			}

			// Destination directory is root directory
			if (("/").equals(destinationDirectoryPathFromCommand)) {
				pwd = File.getRootDirectory();
				return;
			}

			if (("/").equals(Character.toString(destinationDirectoryPathFromCommand.charAt(0)))) {

				// operate in root directory
				// String destDirectory = pathToDirectory.sp
				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

				String absolutePathToDestinationDirectory = destPathFoldersList.get(destPathFoldersList.size() - 1);
				String pathToDestExcludingDestinationDirectory = null;

				if (destPathFoldersList.size() == 1) {
					pathToDestExcludingDestinationDirectory = "/";
				} else {
					pathToDestExcludingDestinationDirectory = String.join("/",
							destPathFoldersList.subList(0, destPathFoldersList.size()));
				}

				destinationDirectory.setAbsolutePath(pathToDestExcludingDestinationDirectory);
				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));

				if (FileSystem.currentFS.containsKey(destinationDirectory)) {
					pwd = destinationDirectory;
					return;
				} else {
					System.out.println("ERR:");
					return;
				}

			}

			else {

				// operate in root directory
				// String destDirectory = pathToDirectory.sp

				System.out.println("HEEEEREEE");
				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

				if (pwd.equals(File.getRootDirectory())) {
					destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + destinationDirectoryPathFromCommand);
				} else {
					destinationDirectory
							.setAbsolutePath(pwd.getAbsolutePath() + "/" + destinationDirectoryPathFromCommand);
				}

				// case: cd xyz
				if (destPathFoldersList.size() == 1) {

					destinationDirectory.setType(Type.DIRECTORY.toString());
					destinationDirectory.setName(destinationDirectoryPathFromCommand);

				} else {

					/**
					 * command : cd a/b/c then destinationDirectoryName = c
					 */
					destinationDirectoryName = destinationDirectoryPathFromCommand
							.split("/")[destinationDirectoryPathFromCommand.split("/").length - 1];
					destinationDirectory.setType(Type.DIRECTORY.toString());
					destinationDirectory.setName(destinationDirectoryName);
				}

				if (FileSystem.currentFS.containsKey(destinationDirectory)) {
					pwd = destinationDirectory;
					return;
				} else {
					System.out.println("ERR: Directory does not exist.Can't change directory");
					return;
				}
			}

		}

	}

	void processCommand(Command c) {

		// System.out.println("Processing the command " + c);

		if (validCommands.contains(c)) {
			// System.out.println("Processing the valid command " + c);

			if (c.getCommand().equals("mkdir")) {
				executeMkdir(c);
			}

			else if (c.getCommand().equals("ls")) {
				executeLs(c);
			}

			else if (c.getCommand().equals("cd")) {
				executeCd(c);
			}

			else if (c.getCommand().equals("pwd")) {
				executePwd(c);
			}

			else if (c.getCommand().equals("session")) {
				executeSessionCommand(c);
			}
		}
	}

	public static void printFileSystem() {

		System.out.println("---------FILESYSTEM-----------");
		System.out.println(FileSystem.currentFS);
	}

	public static void main(String args[]) {
		Terminal T = new Terminal();

		System.out.println("$Starting the linux path traversal application");
		System.out.print("$");

		Scanner myObj = new Scanner(System.in);

		while (true) {
			Command c = T.getCommandFromInput(myObj.nextLine());

			T.processCommand(c);
			System.out.print("$");

		}

	}

}
