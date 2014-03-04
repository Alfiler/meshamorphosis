package formats;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class FormatManager {
	public static Set<Class<? extends FormatInterface>> getFormats(){
		Reflections reflections = new Reflections("formats");

		Set<Class<? extends FormatInterface>> subTypes = 
				reflections.getSubTypesOf(FormatInterface.class);
		return subTypes;
	}

	public static List<String> getFormatsNames(){

		Set<Class<? extends FormatInterface>> subTypes = getFormats();
		List<String> formatNames = new ArrayList<String>();
		for (Class<? extends FormatInterface> sub:subTypes){
			formatNames.add(sub.getName().replace("formats.", ""));
		}
		return formatNames;
	}

	public static FormatInterface instanciateFormat(Class<? extends FormatInterface> obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException{
		return (FormatInterface)obj.getConstructors()[0].newInstance( new Object[]{});
	}
	
	public static FormatInterface instanciateFormat(String name) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException{
		Set<Class<? extends FormatInterface>> subTypes = getFormats();
		for (Class<? extends FormatInterface> sub:subTypes){
			if (sub.getName().matches("formats."+name)){
				return instanciateFormat(sub);
			}
		}
		return null;
	}
}
