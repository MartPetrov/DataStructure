package implementations;

import interfaces.AbstractTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class Tree<E> implements AbstractTree<E> {

    private E key;
    private Tree<E> parent;
    private List<Tree<E>> children;


    public Tree(E key) {
        this.key = key;
        this.children = new ArrayList<>();
    }

    public Tree() {
        this.children = new ArrayList<>();
    }

    @Override
    public void setParent(Tree<E> parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(Tree<E> child) {
        this.children.add(child);
    }

    @Override
    public Tree<E> getParent() {
        return this.parent;
    }

    @Override
    public E getKey() {
        return this.key;
    }

    @Override
    public String getAsString() {
        StringBuilder builder = new StringBuilder();

//        traverseTreeWithRecurrence(builder, 0, this);

        return builder.toString().trim();
    }

    public List<Tree<E>> traverseWithBFS() {
//        StringBuilder builder = new StringBuilder();

        Deque<Tree<E>> queue = new ArrayDeque<>();
        queue.offer(this);
        int indent = 0;

        List<Tree<E>> allNodes = new ArrayList<>();
        while (!queue.isEmpty()) {
            Tree<E> tree = queue.poll();
            allNodes.add(tree);
//            if (tree.getParent() != null && tree.getParent().getKey().equals(this.getKey())) {
//                indent = 2;
//            } else if (tree.children.size() == 0) {
//                indent = 4;
//            }
//            builder.append(getPadding(indent)).append(tree.getKey()).append(System.lineSeparator());


            for (Tree<E> child : tree.children) {
                queue.offer(child);
            }
        }
        return allNodes;
    }

    private void traverseTreeWithRecurrence(List<Tree<E>> collection, Tree<E> tree) {

        collection.add(tree);
//
//        builder.append(this.getPadding(indent))
//                .append(tree.getKey()).append(System.lineSeparator());

        for (Tree<E> child : tree.children) {
            traverseTreeWithRecurrence(collection, child);
        }

    }

    private String getPadding(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    @Override
    public List<E> getLeafKeys() {
        return traverseWithBFS().stream().filter(eTree -> eTree.children.isEmpty()).map(Tree::getKey).collect(Collectors.toList());
    }

    @Override
    public List<E> getMiddleKeys() {
        List<Tree<E>> allNodes = new ArrayList<>();
        this.traverseTreeWithRecurrence(allNodes, this);
        return allNodes.stream()
                .filter(eTree -> eTree.parent != null && eTree.children.size() > 0)
                .map(Tree::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Tree<E> getDeepestLeftmostNode() {
//        List<Tree<E>> trees = this.traverseTreeWithRecurrence();
        int[] maxPath = new int[1];
        List<Tree<E>> deepestLeftMostNote = new ArrayList<>();

        // getDeepestLeafWithBFS
//        for (Tree<E> tree : trees) {
//            if (tree.isLeaf()) {
//                int currentPath = getPStepsFromLeafToRootCount(tree);
//                if (currentPath > maxPath) {
//                    maxPath = currentPath;
//                    deepestLeftMostNote = tree;
//                }
//            }
//        }

        deepestLeftMostNote.add(new Tree<E>());
        int max = 0;
        findDeepestNodeDFS(deepestLeftMostNote, maxPath, max, this);
        return deepestLeftMostNote.get(0);
    }

    private void findDeepestNodeDFS(List<Tree<E>> deepestLeftMostNote, int[] maxPath, int max, Tree<E> tree) {

        if (max > maxPath[0]) {
            maxPath[0] = max;
            deepestLeftMostNote.set(0, tree);
        }
        for (Tree<E> child : tree.children) {
            findDeepestNodeDFS(deepestLeftMostNote, maxPath, max + 1, child);
        }

    }

    private int getPStepsFromLeafToRootCount(Tree<E> tree) {
        int counter = 0;
        Tree<E> current = tree;
        while (current.parent != null) {
            counter++;
            current = current.parent;
        }
        return counter;
    }

    private boolean isLeaf() {
        return this.parent != null && this.children.isEmpty();
    }

    @Override
    public List<E> getLongestPath() {
        return null;
    }

    @Override
    public List<List<E>> pathsWithGivenSum(int sum) {
        return null;
    }

    @Override
    public List<Tree<E>> subTreesWithGivenSum(int sum) {
        return null;
    }
}



