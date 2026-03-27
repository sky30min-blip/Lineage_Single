package lineage.plugin;

public interface Plugin {

	// 처리 함수.
	public Object init(Class<?> c, Object ... opt);
	
}
