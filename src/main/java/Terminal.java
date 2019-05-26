
import java.nio.file.Files;
import java.util.*;
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
		pwd = new File("/", Type.DIRECTORY.toString(), "-");
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

		System.out.println("PATH: " + pwd);
	}

	/**
	 * Restoring the session state to a what it was at the start of the session
	 * which in this case is particularly "/" directory state.
	 * 
	 * TBD: An extension of this would involve storing terminal state globally
	 * somewhere , retrieving it at the start of the terminal session and
	 * working on it .
	 * 
	 * @param c
	 */
	void executeSessionCommand(Command command) {
		pwd = File.getRootDirectory();
		this.initialiseCommandList();

		FileSystem.restoreFileSystemStateToSessionStart();

	}

	void executeMkdir(Command c) {

		// making only in the present working directory
		File directoryToBeCreated = new File(c.getArgs().get(0), Type.DIRECTORY.toString(), pwd.getName());

		if (FileSystem.currentFS.containsKey(pwd)) {

			FileSystem.currentFS.get(pwd).add(directoryToBeCreated);

		} else {

			List<File> subDirectories = new ArrayList<File>();
			subDirectories.add(directoryToBeCreated);
			FileSystem.currentFS.put(pwd, subDirectories);
		}

		printFileSystem();
	}

	/**
	 * This command is used to print all the subDirectories in a directory
	 * 
	 * @param command
	 */
	void executeLs(Command command) {

		// for current directory

		// File directoryToSearchIn = pwd;
		// int numberOfArgumentsToCommand = command.getArgs().size();
		//
		// if (numberOfArgumentsToCommand != 0) {
		//
		// for(int i=0;i<numberOfArgumentsToCommand;i++) {
		//
		// String directoryName = command.getArgs().get(i).split("/")[]
		// directoryToSearchIn = new File(command.getArgs().))
		// }
		// }
		//
		// System.out.println(directoryToSearchIn);
		//
		// printFileSystem();
		// if (FileSystem.currentFS.containsKey(directoryToSearchIn)) {
		// System.out.println(FileSystem.currentFS.get(directoryToSearchIn));
		// }

	}

	void executeCd(Command command) {

		File startingDirectory = pwd;
		File destinationDirectory = pwd;
		if (command.getArgs().size() == 0) {
			// No support for changing to ~ . This would happen in a normal
			// terminal.
			// Can be extended as per use case

		}

		else {

			String destinationDirectoryPath = command.getArgs().get(0);

			// Destination directory is root directory
			if (("/").equals(destinationDirectoryPath)) {
				pwd = File.getRootDirectory();
				return;
			}

			if (("/").equals(Character.toString(destinationDirectoryPath.charAt(0)))) {

				// operate in root directory
				// String destDirectory = pathToDirectory.sp
				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPath.split("/"));

				String absolutePathToDestinationDirectory = destPathFoldersList.get(destPathFoldersList.size() - 1);
				String pathToDestExcludingDestinationDirectory = null;

				if (destPathFoldersList.size() == 1) {
					pathToDestExcludingDestinationDirectory = "/";
				} else {
					pathToDestExcludingDestinationDirectory = String.join("/",
							destPathFoldersList.subList(0, destPathFoldersList.size() - 1));
				}

				destinationDirectory.setAbsolutePath(pathToDestExcludingDestinationDirectory);
				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));

				if (FileSystem.currentFS.containsKey(destinationDirectory)) {
					pwd = destinationDirectory;
					return;
				} else {
					System.out.println("ERR:");
				}

			}

			else {

				// operate in root directory
				// String destDirectory = pathToDirectory.sp

				System.out.println("HEEEEREEE");
				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPath.split("/"));
				String destinationDirectoryName;
				String absolutePathToDestinationDirectory;

				// case: cd xyz
				if (destPathFoldersList.isEmpty()) {
					destinationDirectoryName = destinationDirectoryPath;
					destinationDirectory.setAbsolutePath(pwd.getAbsolutePath());
					destinationDirectory.setType(Type.DIRECTORY.toString());
					destinationDirectory.setName(destinationDirectoryName);

				} else {

					// case: cd a/b/c
					absolutePathToDestinationDirectory = destPathFoldersList.get(destPathFoldersList.size() - 1);

					String pathToDestExcludingDestinationDirectory = pwd.getAbsolutePath()
							+ String.join("/", destPathFoldersList.subList(0, destPathFoldersList.size() - 1));
					destinationDirectory.setAbsolutePath(pathToDestExcludingDestinationDirectory);
					destinationDirectory.setType(Type.DIRECTORY.toString());
					destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));
				}

				if (FileSystem.currentFS.containsKey(destinationDirectory)) {
					pwd = destinationDirectory;
					return;
				} else {
					System.out.println("ERR:");
				}
			}

		}

	}

	void processCommand(Command c) {

		// System.out.println("Processing the command " + c);

		if (validCommands.contains(c)) {
			System.out.println("Processing the valid command " + c);

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

		System.out.print("$Starting the linux path traversal application \n");
		System.out.print("$");

		Scanner myObj = new Scanner(System.in);

		while (true) {
			Command c = T.getCommandFromInput(myObj.nextLine());

			System.out.println("The command is : " + c);
			T.processCommand(c);
			System.out.print("$");

		}

	}

}
