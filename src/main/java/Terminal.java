
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

	File getParentDirectory(File directory) {

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

	void makeDirectory(File destDirectory, File parentDirectoryOfDestDirectory) {

		FileSystem.currentFS.get(parentDirectoryOfDestDirectory).add(destDirectory);
		FileSystem.currentFS.put(destDirectory, new ArrayList<File>());
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
			System.out.println("ERR: Can't ls , dir not found : " + directory.getAbsolutePath());
		}

	}

	File getDestinationDirectory(Command command) {

		File destinationDirectory = new File("", Type.DIRECTORY.toString(), "");

		String destinationDirectoryName = "";
		String destinationDirectoryAbsPath = "";

		if (command.getArgs().size() == 0) {
			// No support for changing to ~ . This would happen in a normal
			// terminal.
			// Can be extended as per use case

			destinationDirectory = pwd;

		}

		else {

			// This path includes the destination directory as well
			String destinationDirectoryPathFromCommand = command.getArgs().get(0);

			// case: cd ..
			if (("..").equals(destinationDirectoryPathFromCommand)) {

				// can't go beyond root
				if (pwd.equals(File.getRootDirectory())) {
					destinationDirectory = pwd;

				} else {
					List<String> directoriesFromRootToPwd = Arrays.asList(pwd.getAbsolutePath().trim().split("/"));

					// CASE: "/a".split("/")
					if (directoriesFromRootToPwd.size() == 2 && ("").equals(directoriesFromRootToPwd.get(0))) {
						destinationDirectory = File.getRootDirectory();

					}

					else {
						// going level up in directory heirarchy
						directoriesFromRootToPwd = Arrays.asList(pwd.getAbsolutePath().split("/"));
						destinationDirectoryName = pwd.getAbsolutePath().split("/")[directoriesFromRootToPwd.size()
								- 2];
						destinationDirectoryAbsPath = String.join("/",
								directoriesFromRootToPwd.subList(0, directoriesFromRootToPwd.size() - 1));

						destinationDirectory.setName(destinationDirectoryName);
						destinationDirectory.setAbsolutePath(destinationDirectoryAbsPath);

					}

				}

				return destinationDirectory;

			}

			// Destination directory is root directory
			if (("/").equals(destinationDirectoryPathFromCommand)) {

				destinationDirectory.setName("/");
				destinationDirectory.setAbsolutePath("/");
				return destinationDirectory;
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

				return destinationDirectory;

			}

		}

		return destinationDirectory;

	}

	void executeCd(Command command) {

		File destDirectory = getDestinationDirectory(command);

		if (FileSystem.currentFS.containsKey(destDirectory)) {
			pwd = destDirectory;
		}

		else {
			System.out.println("ERR: This directory does not exist : " + destDirectory.getAbsolutePath());
		}

	}

	void executeLs(Command command) {
		File destDirectory = getDestinationDirectory(command);

		displayAllSubDirectories(destDirectory);
	}

	void executeMkdir(Command command) {

		File destDirectory = getDestinationDirectory(command);

		// This dest directory is including the one we have to create.
		// So we have to move a level up

		File parentDirOfDestDirectory = getParentDirectory(destDirectory);

		if (FileSystem.currentFS.containsKey(parentDirOfDestDirectory)) {
			makeDirectory(destDirectory, parentDirOfDestDirectory);
		} else {
			System.out.println("Cannot create directory as this path doesn't exist : "
					+ parentDirOfDestDirectory.getAbsolutePath());
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
