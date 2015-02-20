public class Utils {
	public static String printMatrix(long[][] matrix, int size) {
		// Use StringBuilder for more efficient string mutation/appending.
		StringBuilder sb = new StringBuilder();

		// Print the matrix.
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sb.append(matrix[i][j] + " \t");
			}
			sb.append("\n");
		}

		return sb.toString();
	}
}
