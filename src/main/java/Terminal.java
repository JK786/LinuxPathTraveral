
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import linux.path.traversal.enums.Type;

public class Terminal {

	public static File pwd; // This at all stages gives the path to the
							// present working directory
	public static List<Command> validCommands = new ArrayList<Command>();

	public static List<Command> sessionHistory = new ArrayList<Command>(); // Used
																			// to
	// represent
	// terminal session
	// history

	void printHistory() {
		// PlaceHolder to print the history of the terminal
	}

	void initialiseCommandList() {

		Command c = new Command("mkdir", null);
		validCommands.add(c);

		c = new Command("ls", null);
		validCommands.add(c);

		c = new Command("cd", null);
		validCommands.add(c);

		c = new Command("rm", null);
		validCommands.add(c);

		c = new Command("pwd", null);
		validCommands.add(c);

		c = new Command("session", null);
		validCommands.add(c);
	}

	public Terminal() {
		pwd = new File("/", Type.DIRECTORY.toString(), "/");
		initialiseCommandList();
	}

	public Command getCommandFromInput(String input) {

		Command c = new Command();

		c.setCommand(input.split("\\s+")[0]); // Regex to take care of
												// unnecessary spaces between
												// commandline arguments

		List<String> arguments = Arrays.asList(Arrays.copyOfRange(input.split("\\s+"), 1, input.split("\\s+").length));

		// removing the last slashes '/'

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

	void executeMkdir(Command c) {

		// making only in the present working directory

		File directoryToBeCreated = null;

		String absolutePathOfDirectoryBeingCreated = "";
		String nameOfDirectoryToBeCreated = c.getArgs().get(0);

		if (pwd.equals(File.getRootDirectory())) {
			absolutePathOfDirectoryBeingCreated = pwd.getAbsolutePath() + nameOfDirectoryToBeCreated;
		} else {
			absolutePathOfDirectoryBeingCreated = pwd.getAbsolutePath() + "/" + nameOfDirectoryToBeCreated;
		}

		directoryToBeCreated = new File(nameOfDirectoryToBeCreated, Type.DIRECTORY.toString(),
				absolutePathOfDirectoryBeingCreated);

		if (FileSystem.currentFS.containsKey(pwd)) {

			FileSystem.currentFS.get(pwd).add(directoryToBeCreated);

		} else {

			List<File> subDirectories = new ArrayList<File>();
			subDirectories.add(directoryToBeCreated);
			FileSystem.currentFS.put(pwd, subDirectories);
		}

		FileSystem.currentFS.put(directoryToBeCreated, new ArrayList<File>());

		// printFileSystem();
	}

	/**
	 * This command is used to print all the subDirectories in a directory
	 * 
	 * @param command
	 */
	void executeLs(Command command) {


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
					if (directoriesFromRootToPwd.size() == 1 || ("").equals(directoriesFromRootToPwd.get(0))) {
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
