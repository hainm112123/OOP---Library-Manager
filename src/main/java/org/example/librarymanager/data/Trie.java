package org.example.librarymanager.data;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.*;

public class Trie {
    private static final int maxChar = 301;
    private static final int maxCnt = 40000;
    private static final String Viet    = "ắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõọứừửữựưúùủũụýỳỷỹỵ";
    private static final String Eng     = "aaaaaaaaaaaaaaaaadeeeeeeeeeeeiiiiioooooooooooooooouuuuuuuuuuuyyyyy";

    private int cnt = 0;
    private int[][] nextChar = new int[maxChar*40][40];
    private int[] prevChar = new int[maxChar*40];
    private Node[] leftNode = new Node[maxChar*40];
    private Node[] rightNode = new Node[maxChar*40];
    private Map<Character, Character> trans = new HashMap<Character, Character>();

    private static Trie instance;

    private Trie() {
        for (int i = 0; i < Viet.length(); i++) {
            trans.put(Viet.charAt(i), Eng.charAt(i));
        }
    }

    public static synchronized Trie getInstance() {
        if (instance == null) {
            instance = new Trie();
        }
        return instance;
    }

    public int getCnt() {
        return cnt;
    }

    public void buildTrie() {
        if (cnt > 0) {
            Arrays.fill(nextChar, 0);
            Arrays.fill(prevChar, 0);
            Node.deleteAll(leftNode[0]);
            Arrays.fill(leftNode, null);
            Arrays.fill(rightNode, null);
            cnt = 0;
        }

        Task<List<Document>> task = new Task<List<Document>>() {
            @Override
            protected List<Document> call() throws Exception {
                return DocumentQuery.getInstance().getDocuments(null, 0);
            }
        };
        task.setOnSucceeded(e -> {
            List<Document> data = task.getValue();
            for (int i = 0; i < data.size(); i++) {
                addTrie(data.get(i).getTitle(), data.get(i).getId());
            }
        });
        new Thread(task).start();
    }

    private int value(char x) {
        if(x >= 'a' && x <= 'z') return x - 'a';
        if(x >= 'A' && x <= 'Z') return x - 'A';
        if(x >= '0' && x <= '9') return 26 + x - '0';
        return -1;
    }

    public StringBuffer modify(String x) {
        String tmp = x.toLowerCase();
        StringBuffer res = new StringBuffer();
        for(int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if(value(c) != -1) {
                res.append(c);
            } else {
                if (trans.containsKey(c)) res.append(trans.get(c));
            }
        }
        return res;
    }

    public void addTrie(String x, int id) {
        if (cnt > maxCnt) {
            buildTrie();
        }
        StringBuffer tmp = modify(x);
        int top = 0;
        Node left = null;
        Node right = null;
        for (int i = 0; i < tmp.length(); i++) {
            int u = value(tmp.charAt(i));
            for (int j = 0; j < u; ++j) if(nextChar[top][j] != 0){
                left = rightNode[nextChar[top][j]];
            }

            for (int j = 35; j > u; --j) if(nextChar[top][j] != 0){
                right = leftNode[nextChar[top][j]];
            }

            if(nextChar[top][u] == 0) {
                nextChar[top][u] = ++cnt;
                prevChar[cnt] = top;
            }
            top = nextChar[top][u];
        }
        Node newNode = new Node(x, left, right, top, id);
        leftNode[top] = newNode;
        rightNode[top] = newNode;
        while(top != 0) {
            top = prevChar[top];
            for(int i = 0; i <= 35; i++) if(nextChar[top][i] != 0 && leftNode[nextChar[top][i]] != null){
                leftNode[top] = leftNode[nextChar[top][i]];
                break;
            }

            for(int i = 35; i >= 0; i--) if(nextChar[top][i] != 0 && rightNode[nextChar[top][i]] != null){
                rightNode[top] = rightNode[nextChar[top][i]];
                break;
            }
        }
    }

//    public void delTrie(String x) throws Exception{
    public void delTrie(String x) {
        StringBuffer tmp = modify(x);
        int top = 0;
        for (int i = 0; i < tmp.length(); i++) {
            int u = value(tmp.charAt(i));
            if(nextChar[top][u] == 0 || leftNode[nextChar[top][u]] == null) {
//                throw new NoSuchElementException("Không có xâu: " + x);
                return;
            }
            top = nextChar[top][u];
        }

        Node.delete(leftNode[top]);
        leftNode[top] = null;
        rightNode[top] = null;
        while(top != 0) {
            top = prevChar[top];
            leftNode[top] = null;
            rightNode[top] = null;
            for(int i = 0; i <= 35; i++) if(nextChar[top][i] != 0 && leftNode[nextChar[top][i]] != null){
                leftNode[top] = leftNode[nextChar[top][i]];
                break;
            }

            for(int i = 35; i >= 0; i--) if(nextChar[top][i] != 0 && rightNode[nextChar[top][i]] != null){
                rightNode[top] = rightNode[nextChar[top][i]];
                break;
            }
        }
    }

    public Pair<Node, Node> getRange(String x) {
        StringBuffer tmp = modify(x);
        int top = 0;
        for (int i = 0; i < tmp.length(); i++) {
            int u = value(tmp.charAt(i));
            if(nextChar[top][u] == 0 || leftNode[nextChar[top][u]] == null) return new Pair<Node, Node>(null, null);
            top = nextChar[top][u];
        }
        return new Pair<>(leftNode[top], rightNode[top]);
    }

    public static class Node {
        private String string;
        private Node pre;
        private Node nex;
        int triePosition;
        private int id;

        public int getId() {
            return id;
        }

        public String getString() {
            return string;
        }

        public Node getPre() {
            return pre;
        }

        public Node getNex() {
            return nex;
        }

        public Node(String string, Node pre, Node next, int triePosition, int id) {
            this.string = string;
            this.pre = pre;
            this.nex = next;
            this.triePosition = triePosition;
            connect(this.pre, this);
            connect(this, this.nex);
            this.id = id;
        }

        public static void connect(Node x, Node y) {
            if (x != null) x.nex = y;
            if (y != null) y.pre = x;
        }

        public static void delete(Node x) {
            if (x == null) return;
            connect(x.pre, x.nex);
            x.pre = null;
            x.nex = null;
            x = null;
        }

        public static void deleteAll(Node x) {
            while (x != null) {
                Node tmp = x.nex;
                delete(x);
                x = tmp;
            }
        }
    }
}
