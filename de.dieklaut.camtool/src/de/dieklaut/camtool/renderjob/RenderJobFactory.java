package de.dieklaut.camtool.renderjob;

public class RenderJobFactory {
	
	private static RenderJobFactoryProvider instance;
	
	private RenderJobFactory() {
	}
	
	public static RenderJobFactoryProvider getInstance(){
		if (instance == null) {
			throw new IllegalStateException("No render job factory provider set");
		}
		return instance;
	}
	
	public static void setFactoryInstance(RenderJobFactoryProvider replacement){
		instance = replacement;
	}
}
