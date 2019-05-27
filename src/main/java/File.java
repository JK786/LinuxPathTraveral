import java.util.Vector;

import linux.path.traversal.enums.Type;

/**
 * This class denotes a file or a directory unit
 * 
 * @author jibrankhan
 *
 */

public class File {

	/**
	 * {@link Type enum for signifying if it is a File or Directory}
	 */
	private String type;

	private String name;

	// Path from root to directory including the directory name
	private String absolutePath;

	/**
	 * Parameterized Constructor
	 * 
	 * @param name
	 * @param type
	 * @param absolutePath
	 */
	public File(String name, String type, String absolutePath) {
		this.name = name;
		this.type = type;
		this.absolutePath = absolutePath;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsolutePath() {
		return this.absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public static File getRootDirectory() {
		File root = new File("/", Type.DIRECTORY.toString(), "/");
		return root;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((absolutePath == null) ? 0 : absolutePath.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		File other = (File) obj;
		if (absolutePath == null) {
			if (other.absolutePath != null)
				return false;
		} else if (!absolutePath.equals(other.absolutePath))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "File [type=" + type + ", name=" + name + ", absolutePath=" + absolutePath + "]";
	}

}
