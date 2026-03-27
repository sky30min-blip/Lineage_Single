package lineage.world;




public class Node {
	public int		f;			// f = g+h
	public int		h;			// 휴리스틱 값
	public int		g;			// 현재까지의 거리
	public int		x, y;		// 노드의 위치
	public Node	prev;			// 이전 노드
	public Node	next;			// 다음 노드
	public int uid;				// pool 위치 고유값.
	
	public void close() {
		prev = next = null;
		uid = 0;
	}
	
}
