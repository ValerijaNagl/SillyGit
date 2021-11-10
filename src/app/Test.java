package app;

import java.io.File;

public class Test {
	
	public Test() {
		File test1 = new File(AppConfig.WORK_ROUTE_ABSOLUTE +  File.separator + "test" + AppConfig.myServentInfo.getChordId());
		WorkWithFiles.writeToFile(test1.getAbsolutePath(), "ovo je proba");
		
		AppConfig.myServentInfo.getVersions().put(test1.getName(), 0);
		
		File test2 = new File(AppConfig.WORK_ROUTE_ABSOLUTE +  File.separator + "test" + AppConfig.myServentInfo.getChordId());
		WorkWithFiles.writeToFile(test2.getAbsolutePath(), "ovo je proba");
		AppConfig.myServentInfo.getVersions().put(test1.getName(), 0);
	}

}
