package com.github.raipc.moex;

public class SimpleTrie {
	private static final int ALPHABET_SIZE = 'Z' - '0';
	private final TrieNode root = new TrieNode();

	public void insert(String key, String value) {
		int length = key.length();
		TrieNode current = this.root;
		for (int index = 0; index < length; index++) {
			current = current.getOrCreateChild(key.charAt(index));
		}
		current.value = value;
	}

	public String find(String key) {
		int length = key.length();
		TrieNode current = this.root;
		for (int index = 0; index < length; index++) {
			current = current.getChild(key.charAt(index));
			if (current == null) {
				return null;
			}
		}
		return current.value;
	}

	private static final class TrieNode {
		private TrieNode[] children;
		private String value;

		private TrieNode getOrCreateChild(char c) {
			if (children == null) {
				children = new TrieNode[ALPHABET_SIZE];
			}
			int idx = c - '0';
			if (idx >= ALPHABET_SIZE) {
				throw new AssertionError("Only 0..Z alphabet supported for insertion");
			}
			TrieNode child = children[idx];
			if (child == null) {
				child = new TrieNode();
				children[idx] = child;
			}
			return child;
		}

		private TrieNode getChild(char c) {
			if (children == null) {
				return null;
			}
			int idx = c - '0';
			if (idx >= ALPHABET_SIZE || idx < 0) {
				return null;
			}
			return children[idx];
		}
	}
}
