import java.util.List;
import java.util.Map;

public class Command {

	// Name of the command
	private String command;

	// For extendibility purpose in the future
	private Map<String, String> optionAndValue;

	private List<String> args;

	/**
	 * Default Constructor
	 */
	Command() {

	}

	/**
	 * Constructor
	 * 
	 * @param command
	 * @param optionAndValue
	 */

	Command(String command, List<String> args, Map<String, String> optionAndValue) {
		this.command = command;
		this.args = args;
		this.optionAndValue = optionAndValue;
	}

	@Override
	public String toString() {
		return "Command [command=" + command + ", optionAndValue=" + optionAndValue + ", args=" + args + "]";
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Map<String, String> getOptionAndValue() {
		return optionAndValue;
	}

	public void setOptionAndValue(Map<String, String> optionAndValue) {
		this.optionAndValue = optionAndValue;
	}

	/*
	 * Generating equals and hashcode based on name of command for equality.
	 * Will be used when contains is called in a list or maybe someone searches
	 * history with the command.
	 * 
	 * PS:Generated this using eclipse.
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		return true;
	}

}
