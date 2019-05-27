import java.util.List;

/**
 * Currently unused ,but some code arguably could be refactored and moved here
 * 
 * @author jibrankhan
 *
 */

public class linuxPathTraversalUtils {

	public static void removeLastSlash(List<String> args) {

		for (int i = 0; i < args.size(); i++) {

			String argument = args.get(i);

			if (argument.charAt(argument.length() - 1) == '/') {

				args.set(i, argument.substring(0, argument.length() - 1));
			}
		}
	}

}
