package snl.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Tree {
    TreeNode head;
    List<Integer> a=new ArrayList<>();
    public void setHead(TreeNode head) {
        this.head = head;

    }
    public void print(TreeNode node,int offset){
        print_offset(offset);
        System.out.print("¡ª¡ª"+node.value);
        if(node.hasChildren()){
            a.add(node.value.length()+offset);
            for (TreeNode treeNode:node.children){
                print(treeNode,node.value.length()+offset+2);
            }
            a.remove(a.indexOf(node.value.length()+offset));
        }
    }
    void print_offset(int offset){
        System.out.print("\n");
        for (int i = 0; i < offset; i++) {
//            if (a.indexOf(i)!=-1) {
//                System.out.print("|");
//                continue;
//            }
            System.out.print(" ");
        }
    }
}
