import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class Command {

	private String command; // Name of the command
	private Map<String, String> optionAndValue; // For extendibility : No
												// supportProvided
	private List<String> args;

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

	Command() {

	}

	Command(String command, Map<String, String> optionAndValue) {
		this.command = command;
		this.optionAndValue = optionAndValue;
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
	 * PS: I generated this using eclipe.
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
