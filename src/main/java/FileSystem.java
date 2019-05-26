import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import linux.path.traversal.enums.Type;

/**
 * 
 * 
 * @author jibrankhan
 *
 */
public class FileSystem {

	public static File rootDirectory;
	public static Map<File, List<File>> currentFS = new HashMap<File, List<File>>();

	static {
		File root = File.getRootDirectory();
		currentFS.put(root, new ArrayList<File>());
	}

	public static void restoreFileSystemStateToSessionStart() {
		currentFS = new HashMap<File, List<File>>();
		File root = File.getRootDirectory();
		currentFS.put(root, new ArrayList<File>());
	}

	@Override
	public String toString() {
		return "FileSystem [rootDirectory=" + rootDirectory + ", currentFS=" + currentFS + "]";
	}

}
