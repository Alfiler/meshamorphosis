package formats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import data.Mesh;

public interface FormatInterface {
	boolean read(Path in, Mesh m) throws FileNotFoundException, Exception;
	boolean write(Path out, Mesh m)  throws IOException;
}
