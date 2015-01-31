package dk.antwars.core.classloading;

import dk.antwars.core.ant.Ant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AntClassLoader extends ClassLoader {

    private Logger logger = LoggerFactory.getLogger(AntClassLoader.class);

	private String root;
	public AntClassLoader(String rootDir) {
		if (rootDir == null) {
			throw new IllegalArgumentException("Null root directory");
		}
		root = rootDir;
	}

	@SuppressWarnings({ "rawtypes" })
	public Class<Ant> loadAntClass(String name, boolean resolve) {
		if (name != null) {
            Class c = findLoadedClass(name);
            if (c == null) {
                try {
                    byte data[] = loadClassData(StringUtils.substringAfterLast(name, ".") + ".class");
                    c = defineClass(name, data, 0, data.length);
                    if (c == null) {
                        throw new ClassNotFoundException(name);
                    }
                    if (!Ant.class.isAssignableFrom(c)) {
                        throw new IllegalStateException("Loaded class " + c.getSimpleName() +  " was not an instance of Ant");
                    }
                    if (resolve) {
                        resolveClass(c);
                    }
                } catch (Exception e) {
                    logger.error("Error occurred whilst attempting to load Ant '"+name+"'", e);
                }
            }
            return c;
        }
        return null;
	}

	private byte[] loadClassData(String filename) throws IOException {
		final File f = new File(root, filename);
		final int size = (int) f.length();
		final byte buff[] = new byte[size];
		final FileInputStream fis = new FileInputStream(f);
		final DataInputStream dis = new DataInputStream(fis);
		dis.readFully(buff);
		dis.close();
		return buff;
	}
}
