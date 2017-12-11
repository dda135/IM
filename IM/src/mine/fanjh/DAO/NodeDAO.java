package mine.fanjh.DAO;

import java.util.ArrayList;
import java.util.List;

import mine.fanjh.DO.Node;
import mine.fanjh.utils.Const;

public class NodeDAO {

	public List<Node> getNodes(String token) {
		ArrayList<Node> nodes = new ArrayList<>();
		Node node = new Node(Const.COMMON_IP, 50001);
		nodes.add(node);
		return nodes;
	}

}
