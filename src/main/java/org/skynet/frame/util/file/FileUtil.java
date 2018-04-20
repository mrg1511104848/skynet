package org.skynet.frame.util.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {

	public static void saveBytesToFile(String filePath,byte[] bytes) throws IOException {
		BufferedOutputStream bufferedOutput = null;
		try {
			File file = new File(filePath);
			OutputStream output = new FileOutputStream(file);
			bufferedOutput = new BufferedOutputStream(output);
			bufferedOutput.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bufferedOutput!=null)
				bufferedOutput.close();
		}
	}
}
