package snl.syntax;

public class TreeNode {
    String value;
    TreeNode[] children;
    public TreeNode(String value){
        this.value=value;
    }
    public boolean hasChildren(){return children!=null;}
    public void setChildren(TreeNode... nodes) {
        this.children = nodes;
    }
}
