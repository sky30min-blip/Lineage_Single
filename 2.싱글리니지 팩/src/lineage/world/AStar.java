package lineage.world;

import java.util.ArrayList;
import java.util.List;

import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class AStar {
	// 열린 노드, 닫힌 노드 리스트
	Node	OpenNode, ClosedNode;

	// 최대 루핑 회수
	public static final int LIMIT_LOOP = 300;
	public static List<List<Node>> pool;

	public static void init() {
		pool = new ArrayList<List<Node>>();
		for (int i = 0; i < Lineage.thread_ai; ++i)
			pool.add(new ArrayList<Node>());
	}
	
	private static Node getPool(object o) {
		Node node = null;
		if (Lineage.pool_astar) {
			List<Node> pool = AStar.pool.get(o.thread_uid);
			synchronized (pool) {
				if (pool.size() > 0) {
					node = pool.get(0);
					pool.remove(0);
				} else {
					node = new Node();
				}
			}
		} else {
			node = new Node();
		}
		node.uid = o.thread_uid;
		return node;
	}
	
	private static void setPool(Node node) {
		if (Lineage.pool_astar) {
			if (node != null) {
				int uid = node.uid;
				List<Node> pool = AStar.pool.get(uid);
				synchronized (pool) {
					node.close();
					if (Util.isPoolAppend(pool) && pool.contains(node) == false)
						pool.add(node);
					else
						clearPool(uid);
				}
			}
		} else {
			node.close();
			node = null;
		}
	}
	
	public static int getPoolSize() {
		int size = 0;
		if (Lineage.pool_astar) {
			for (List<Node> pool : AStar.pool)
				size += pool.size();
		}
		return size;
	}
	
	private static void clearPool(int uid) {
		TimeLine.start(String.format("AStar 에서 %d 번째 Pool 초과로 메모리 정리 중..", uid));

		List<Node> pool = AStar.pool.get(uid);
		// 풀 전체 제거.
		pool.clear();
		// gc 한번 호출.
		System.gc();

		TimeLine.end();
	}
	
	//*************************************************************************
	// Name : AStar()
	// Desc : 생성자
	//*************************************************************************
	public AStar() {

		OpenNode = null;
		ClosedNode = null;
	}

	//*************************************************************************
	// Name : ResetPath()
	// Desc : 이전에 생성된 경로를 제거
	//*************************************************************************
	public void cleanTail() {
		Node tmp;

		while (OpenNode != null) {
			tmp = OpenNode.next;
			setPool(OpenNode);
			OpenNode = tmp;
		}

		while (ClosedNode != null) {
			tmp = ClosedNode.next;
			setPool(ClosedNode);
			ClosedNode = tmp;
		}
	}

	//*************************************************************************
	// Name : FindPath()
	// Desc : 시작위치와 목표위치를 입력 받아 경로노드 리스트를 반환
	//*************************************************************************
	// 몬스터좌표 sx, xy
	// 이동할좌표 tx, ty
	public Node searchTail(object o, int tx, int ty, boolean obj)
	{
		Node src, best = null;
		int count = 0;
		int sx = o.getX();
		int sy = o.getY();
		int map = o.getMap();

		// 처음 시작노드 생성
		src = getPool(o);
		src.g = 0;
		src.h = (tx - sx) * (tx - sx) + (ty - sy) * (ty - sy);
		src.f = src.h;
		src.x = sx;
		src.y = sy;

		// 시작노드를 열린노드 리스트에 추가
		OpenNode = src;

		// 길찾기 메인 루프
		// 최대 반복 회수가 넘으면 길찾기 중지
		while (count < LIMIT_LOOP) {
			// 열린노드가 없다면 모든 노드를 검색했으므로 길찾기 중지
			if (OpenNode == null) {
				return null;
			}

			// 열린노드의 첫번째 노드를 가져오고 열린노드에서 제거
			best = OpenNode;
			OpenNode = best.next;

			// 가져온 노드를 닫힌노드에 추가
			best.next = ClosedNode;
			ClosedNode = best;

			// 현재 가져온 노드가 목표모드라면 길찾기 성공
			if (best.x == tx && best.y == ty) {
				return best;
			}

			// 현재 노드와 인접한 노드들로 확장하여 열린노드로 추가
			if (MakeChild(o, best, tx, ty, map, obj) == 0 && count == 0) {
				return null;
			}

			count++;
		}

		return null;
	}

	//*************************************************************************
	// Name : MakeChild()
	// Desc : 입력받은 노드의 인접한 노드들로 확장
	//*************************************************************************
	// 리니지 환경에 맞게 재수정 by psjump
	char MakeChild(object o, Node node, int tx, int ty, int map, boolean obj) {
		int x, y;
		char flag = 0;

		x = node.x;
		y = node.y;

		// 인접한 노드로 이동가능한지 검사
		for (int i = 0; i < 8; ++i) {
			if (World.isThroughObject(x, y, map, i)) {
				int nx = x + Util.getXY(i, true);
				int ny = y + Util.getXY(i, false);
				boolean ck = true;
				// 골인지점의 좌표는 검색할필요 없음.
				if (tx != nx || ty != ny) {
					if (obj) {					
						ck = World.isMapdynamic(nx, ny, map) == false;
						
						if (o != null && o instanceof MonsterInstance) {
							MonsterInstance boss = (MonsterInstance) o;
							if (boss.getMonster() != null && boss.getMonster().isBoss()) {
								ck = true;

								for (object oo : boss.getInsideList()) {
									for (object m : oo.getInsideList()) {
										if (m != null && m instanceof MonsterInstance && m.getX() == nx && m.getY() == ny && o.getMap() == m.getMap()) {
											MonsterInstance mi = (MonsterInstance) m;
											if (mi.getMonster() != null && mi.getMonster().isBoss()) {
												ck = false;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				if (ck) {
					MakeChildSub(node, nx, ny, tx, ty, o);
					flag = 1;
				}
			}
		}

		return flag;
	}

	//*************************************************************************
	// Name : MakeChildSub()
	// Desc : 노드를 생성. 열린노드나 닫힌노드에 이미 있는 노드라면 
	//        이전값과 비교하여 f가 더 작으면 정보 수정
	//        닫힌노드에 있다면 그에 연결된 모든 노드들의 정보도 같이 수정
	//*************************************************************************
	void MakeChildSub(Node node, int x, int y, int tx, int ty, object o) {
		Node old = null, child = null;
		int g = node.g + 1;

		// 현재노드가 열린 노드에 있고 f가 더 작으면 정보 수정
		if ((old = IsOpen(x, y)) != null) {

			if (g < old.g) {
				old.prev = node;
				old.g = g;
				old.f = old.h + old.g;
			}
		}

		// 현재노드가 닫힌 노드에 있고 f가 더 작으면 정보 수정
		else if ((old = IsClosed(x, y)) != null) {

			if (g < old.g) {
				old.prev = node;
				old.g = g;
				old.f = old.h + old.g;
			}
		}

		// 새로운 노드라면 노드정보 생성하고 열린노드에 추가
		else {
			// 새로운 노드 생성
			child = getPool(o);

			child.prev = node;
			child.g = g;
			child.h = (x - tx) * (x - tx) + (y - ty) * (y - ty);
			child.f = child.h + child.g;
			child.x = x;
			child.y = y;

			// 새로운 노드를 열린노드에 추가
			InsertNode(child);
		}
	}

	//*************************************************************************
	// Name : IsOpen()
	// Desc : 입력된 노드가 열린노드인지 검사
	//*************************************************************************
	Node IsOpen(int x, int y) {
		Node tmp = OpenNode;

		while (tmp != null) {
			if (tmp.x == x && tmp.y == y) {
				return tmp;
			}

			tmp = tmp.next;
		}

		return null;
	}

	//*************************************************************************
	// Name : IsClosed()
	// Desc : 입력된 노드가 닫힌노드인지 검사
	//*************************************************************************
	Node IsClosed(int x, int y) {
		Node tmp = ClosedNode;

		while (tmp != null) {
			if (tmp.x == x && tmp.y == y) {
				return tmp;
			}

			tmp = tmp.next;
		}

		return null;
	}

	//*************************************************************************
	// Name : InsertNode()
	// Desc : 입력된 노드를 열린노드에 f값에 따라 정렬하여 추가
	//        f값이 높은것이 제일 위에 오도록 -> 최적의 노드
	//*************************************************************************
	void InsertNode(Node src) {
		Node old = null, tmp = null;

		if (OpenNode == null) {
			OpenNode = src;
			return;
		}

		tmp = OpenNode;
		while (tmp != null && (tmp.f < src.f)) {
			old = tmp;
			tmp = tmp.next;
		}

		if (old != null) {
			src.next = tmp;
			old.next = src;
		} else {
			src.next = tmp;
			OpenNode = src;
		}
	}
	
}