package lineage.world.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lineage.share.TimeLine;

public class ScriptController {

	private static ScriptEngineManager sem;
	private static Map<String, ScriptEngine> engines;
	
	public static void init() {
		TimeLine.start("ScriptController...");
		
		sem = new ScriptEngineManager();
		engines = new HashMap<String, ScriptEngine>();
		
		TimeLine.end();
	}
	
	public static Invocable getInvocable(String path) {
		//
		if(engines.containsKey(path))
			return (Invocable) engines.get(path);
		//
		FileReader fr = null;
        try {
        	//
            path = "scripts/" + path;
            ScriptEngine engine = null;
            //
            File scriptFile = new File(path);
            if (!scriptFile.exists())
                return null;
            // 
            engine = sem.getEngineByName("javascript");
           	engines.put(path, engine);
            //
            fr = new FileReader(scriptFile);
            engine.eval(fr);
            //
            return (Invocable) engine;
        } catch (Exception e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException ignore) { }
        }
	}
}
