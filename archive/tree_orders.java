import java.util.*;
import java.io.*;

public class tree_orders {
    class FastScanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		FastScanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}
	
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

	public class TreeOrders {
		int n;
		int[] key, left, right;
		
		void read() throws IOException {
			FastScanner in = new FastScanner();
			n = in.nextInt();
			key = new int[n];
			left = new int[n];
			right = new int[n];
			for (int i = 0; i < n; i++) { 
				key[i] = in.nextInt();
				left[i] = in.nextInt();
				right[i] = in.nextInt();
			}
		}

		List<Integer> inOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
			inOrderAdd(result, 0);
			return result;
		}

		private void inOrderAdd(ArrayList<Integer> result, int root) {
			if(left[root] >= 0) {
				inOrderAdd(result, left[root]);
			}
			result.add(key[root]);
			if (right[root] >= 0){
				inOrderAdd(result, right[root]);
			}
		}

		List<Integer> preOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
			preOrderAdd(result,0);
			return result;
		}

		private void preOrderAdd(ArrayList<Integer> result, int root) {
			result.add(key[root]);
			if(left[root] >= 0) {
				preOrderAdd(result, left[root]);
			}
			if (right[root] >= 0){
				preOrderAdd(result, right[root]);
			}
		}

		List<Integer> postOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
			postOrderAdd(result, 0);
			return result;
		}

		private void postOrderAdd(ArrayList<Integer> result, int root) {
			if(left[root] >= 0) {
				postOrderAdd(result, left[root]);
			}
			if (right[root] >= 0){
				postOrderAdd(result, right[root]);
			}
			result.add(key[root]);
		}
	}

	static public void main(String[] args) throws IOException {
            new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new tree_orders().run();
                        } catch (IOException e) {
                        }
                    }
                }, "1", 1 << 26).start();
	}

	public void print(List<Integer> x) {
		for (Integer a : x) {
			System.out.print(a + " ");
		}
		System.out.println();
	}

	public void run() throws IOException {
		TreeOrders tree = new TreeOrders();
		tree.read();
		print(tree.inOrder());
		print(tree.preOrder());
		print(tree.postOrder());
	}
}
