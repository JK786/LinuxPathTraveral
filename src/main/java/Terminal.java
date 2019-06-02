
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	/**
	 * INITIALISE THE LIST OF VALID COMMANDS SUPPORTED BY THE SYSTEM
	 */
	void initialiseCommandList() {

		List<String> commandNames = Arrays.asList("mkdir", "ls", "cd", "rm", "d", "pwd", "sessionClear", "showFs");
		Command c;

		for (int i = 0; i < commandNames.size(); i++) {

			if (("rm").equals(commandNames.get(i))) {
				Map<String, String> validOption = new HashMap<String, String>();
				validOption.put("d", null);
				c = new Command(commandNames.get(i), null, validOption);
				validCommands.add(c);

			} else {
				c = new Command(commandNames.get(i), null, null);
				validCommands.add(c);
			}

		}

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Default constructor which we have overridden
	 */

	public Terminal() {
		pwd = File.getRootDirectory();
		initialiseCommandList();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * PARSE THE COMMAND LINE INPUT TO GET THE COMMAND OBJECT
	 * 
	 * @param input
	 * @return
	 */
	public Command getCommandFromInput(String input) {

		Command c = new Command("", null, null);
		List<String> spaceSplitCommand = Arrays.asList(input.split("\\s+"));

		Map<String, String> optionValue = new HashMap<String, String>();

		if (spaceSplitCommand.size() == 0) {
			return c;
		}

		c.setCommand(spaceSplitCommand.get(0));

		if (spaceSplitCommand.size() >= 2 && spaceSplitCommand.get(1).contains("-")) {

			if (spaceSplitCommand.size() >= 3) {
				optionValue.put(spaceSplitCommand.get(1).substring(1), spaceSplitCommand.get(2));
			} else {
				optionValue.put(spaceSplitCommand.get(1).substring(1), null);
			}
			c.setOptionAndValue(optionValue);

			return c;

		}

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

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * THE CORE LOGIC LIES HERE: Based on the commandline input , it construct a
	 * File object of the destination directory on which we have to execute the
	 * command
	 * 
	 * @param command
	 * @return
	 */
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

			// CASE: cd ..
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
						// going level up in directory heirarchy .
						// get parent directory of the utils can be used instead
						// of this logic too.
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

			// CASE : Example = cd / or ls /
			if (("/").equals(destinationDirectoryPathFromCommand)) {

				destinationDirectory.setName("/");
				destinationDirectory.setAbsolutePath("/");
				return destinationDirectory;
			}

			// CASE: Absolute path longer than '/'
			if (("/").equals(Character.toString(destinationDirectoryPathFromCommand.charAt(0)))) {

				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

				String absolutePathToDestinationDirectory = "";

				absolutePathToDestinationDirectory = String.join("/",
						destPathFoldersList.subList(0, destPathFoldersList.size()));

				destinationDirectory.setAbsolutePath(absolutePathToDestinationDirectory);
				destinationDirectory.setType(Type.DIRECTORY.toString());
				destinationDirectory.setName(destPathFoldersList.get(destPathFoldersList.size() - 1));

			}

			// CASE: Relative path
			else {

				List<String> destPathFoldersList = Arrays.asList(destinationDirectoryPathFromCommand.split("/"));

				if (pwd.equals(File.getRootDirectory())) {
					destinationDirectory.setAbsolutePath(pwd.getAbsolutePath() + destinationDirectoryPathFromCommand);
				} else {
					destinationDirectory
							.setAbsolutePath(pwd.getAbsolutePath() + "/" + destinationDirectoryPathFromCommand);
				}

				// CASE: cd xyz
				if (destPathFoldersList.size() == 1) {

					destinationDirectory.setType(Type.DIRECTORY.toString());
					destinationDirectory.setName(destinationDirectoryPathFromCommand);

				} else { // CASE: cd xyz/m/n

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

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * EXCECUTE THE COMMAND WHICH DISPLAYS THE PRESENT WORKING DIRECTORY
	 * 
	 * @param c
	 */
	void executePwd(Command c) {

		System.out.println("PATH: " + pwd.getAbsolutePath());

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

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

		System.out.println("SUCC: CLEARED: RESET TO ROOT");

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * EXECUTE 'CD' COMMAND
	 * 
	 * @param command
	 */

	void executeCd(Command command) {

		File destDirectory = getDestinationDirectory(command);

		if (FileSystem.currentFS.containsKey(destDirectory)) {
			pwd = destDirectory;
		}

		else {
			System.out.println("ERR: THIS DIRECTORY DOES NOT EXIST : " + destDirectory.getAbsolutePath());
		}

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * EXECUTE 'LS' COMMAND
	 * 
	 * @param command
	 */
	void executeLs(Command command) {
		File destDirectory = getDestinationDirectory(command);

		linuxPathTraversalUtils.displayAllSubDirectories(destDirectory);

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * EXECUTE MAKE DIRECTORY COMMAND
	 * 
	 * @param command
	 */
	void executeMkdir(Command command) {

		if (command.getArgs().size() == 0) {
			System.out.println("ERR: Cannot create directory. No path supplied");
			return;
		}

		File destDirectory = getDestinationDirectory(command);

		// This dest directory is including the one we have to create.
		// So we have to move a level up

		if (File.getRootDirectory().equals(destDirectory)) {
			System.out.println("ERR: CANNOT CREATE ROOT DIRECTORY. IT ALREADY EXISTS");
		}

		File parentDirOfDestDirectory = linuxPathTraversalUtils.getParentDirectory(destDirectory);

		if (FileSystem.currentFS.containsKey(parentDirOfDestDirectory)) {
			linuxPathTraversalUtils.makeDirectory(destDirectory, parentDirOfDestDirectory);
		} else {
			System.out.println("ERR : CANNOT CREATE DIRECTORY AS THIS PATH DOESN'T EXIST : "
					+ parentDirOfDestDirectory.getAbsolutePath());
		}

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * EXECUTE REMOVE COMMAND
	 * 
	 * @param command
	 */

	void executeRemove(Command command) {

		String directoryPathToBeRemoved;

		if (command.getOptionAndValue() == null && command.getArgs().size() == 0) {
			System.out.println("ERR: NO ARGUMENTS PROVIDED FOR RM");
			return;
		}

		// CASE: rm a (invalid)
		else if (command.getArgs() != null) {
			System.out.println(
					"ERR: " + command.getArgs().get(0) + " IS A DIRECTORY. SUPPLY -D OPTION TO MAKE THIS WORK");
			return;
		}

		List<String> args = new ArrayList<String>();

		Map<String, String> optionAndValues = command.getOptionAndValue();

		if (optionAndValues.containsKey("d")) {

			if (optionAndValues.get("d") == null) {
				System.out.println("ERR: NO VALUES PROVIDED FOR OPTION 'D' . NO OPERATION CAN BE PERFORMED");
				return;

			} else {
				directoryPathToBeRemoved = optionAndValues.get("d");
				args.add(directoryPathToBeRemoved);
			}
		} else {
			System.out.println("ERR: ILLEGAL OPTION GIVEN. -d is only supported");
			return;
		}

		File destinationDirectory = getDestinationDirectory(new Command("rm", args, null));

		if (File.getRootDirectory().equals(destinationDirectory)) {
			System.out.println("ERR: ILLEGAL OPERATION. CANNOT REMOVE ROOT DIRECTORY");
			return;
		}

		if (FileSystem.currentFS.containsKey(destinationDirectory)) {

			// We can only remove a directory provided its empty
			if (FileSystem.currentFS.get(destinationDirectory).size() == 0) {
				FileSystem.currentFS.remove(destinationDirectory);

				// You also have to remove it from the list of subdirectories of
				// its parent directory
				File parentDirectory = linuxPathTraversalUtils.getParentDirectory(destinationDirectory);

				FileSystem.currentFS.get(parentDirectory).remove(destinationDirectory);
				return;

			} else {
				System.out.println("ERR: DIRECTORY " + destinationDirectory.getAbsolutePath()
						+ " CANNOT BE REMOVED AS IT IS NOT EMPTY");
				return;
			}

		} else {
			System.out.println("ERR: THIS DIRECTORY DOES NOT EXIST. CANNOT BE REMOVED");
		}

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	void executeShowFS(Command c) {

		for (Entry<File, List<File>> a : FileSystem.currentFS.entrySet()) {

			System.out.println("Abs Path : " + a.getKey().getAbsolutePath());
			System.out.println("Directory Name  : " + a.getKey().getName());

			System.out.println("Subdirectories : ");

			for (int i = 0; i < a.getValue().size(); i++) {
				System.out.print(a.getValue().get(i).getName() + " ");
			}

			System.out.print("\n");
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Checks whether the command is a valid one and calls the code to execute
	 * it
	 * 
	 * @param c
	 */
	void processCommand(Command c) {

		// System.out.println("Processing the command " + c);

		if (validCommands.contains(c)) {

			sessionHistory.add(c);
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

			else if (c.getCommand().equals("sessionClear")) {
				executeSessionCommand(c);
			} else if (c.getCommand().equals("rm")) {
				executeRemove(c);
			} else if (c.getCommand().equals("showFs")) {
				executeShowFS(c);
			}
		} else {
			System.out.println("ERR : CANT RECOGNISE INPUT");
		}
	}

	/**
	 * Driver method
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Terminal T = new Terminal();

		System.out.println("$STARTING THE LINUX PATH TRAVERSAL APPLICATION");

		Scanner myObj = new Scanner(System.in);

		while (true) {
			System.out.print("$");
			Command c = T.getCommandFromInput(myObj.nextLine());

			// This is to simply continue when the user keeps putting spaces and
			// presses enter and
			// there is no actual command
			if ("".equals(c.getCommand())) {
				continue;
			}

			T.processCommand(c);

		}

	}

}
