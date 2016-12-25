import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MySimplexSolver {
    public boolean debug = false;
    public static final double LARGE = 1e40;
    private int numArtVars = 0;
    private int decisionVars = 0;
    private int slackVars = 0;

    public double[] solve(double[] objective, List constraints, List<Double> rhs) {
        if (debug) {
            System.out.println("Input values: ");
            for (int i = 0; i < constraints.size(); i++) {
                System.out.print(Arrays.toString((double[]) constraints.get(i)));
                System.out.print(" ");
                System.out.println(rhs.get(i));
            }
            System.out.println("Obbjective: ");
            System.out.println(Arrays.toString(objective));
        }

        List<Constraint> constraintsList = new ArrayList<>(constraints.size());

        for (int i = 0; i < constraints.size(); i++) {
            constraintsList.add(new Constraint((double[]) constraints.get(i), rhs.get(i), Relationship.LEQ));
        }

        normalize(constraintsList);

        decisionVars = objective.length;
        slackVars = constraints.size();

        double[][] tableu;

        for (Constraint constraint : constraintsList) {
            if (constraint.relationship != Relationship.LEQ) {
                numArtVars++;
            }
        }

        tableu = initP1Tableu(constraintsList, objective);
        if (numArtVars > 0) {

            if (debug) {
                System.out.println("Tableu initialized: ");
                System.out.println(print(tableu));
            }

            optimize(tableu);

            if (Math.abs(tableu[0][tableu[0].length - 1]) > 0.000000001) {
                throw new NoFeasibleSolutionException();
            }

            if (debug) {
                System.out.println("Optimization of phase 1 finished: ");
                System.out.println(print(tableu));
            }
        } else if (debug) {
            System.out.println("phase 1 not required");
        }

        tableu = dropPhase1(tableu);

        if (debug) {
            System.out.println("Optimization of phase 2 starting: ");
            System.out.println(print(tableu));
        }

        optimize(tableu);
        if (debug) {
            System.out.println("Optimization of phase 2 finished: ");
            System.out.println(print(tableu));
        }
        return getResult(tableu);
    }

    private double[] getResult(double[][] tableu) {
        double[] result = new double[decisionVars];
        Set<Integer> basicRows = new HashSet<>();
        for (int i = 1; i < decisionVars + 1; i++) {
            Integer basicRow = getBasicRow(tableu, i);
            if (basicRow != null && basicRow == 0) {
                result[i - 1] = 0;
            } else if (basicRows.contains(basicRow)) {
                result[i - 1] = 0;
            } else if (basicRow != null) {
                basicRows.add(basicRow);
                result[i - 1] = tableu[basicRow][tableu[0].length - 1];
            } else {
                result[i - 1] = 0;
            }
        }
        return result;
    }

    private double[][] dropPhase1(double[][] tableu) {
        if (numObjectiveFunctions() == 1) {
            return tableu;
        }
        Set<Integer> columnsToDrop = new HashSet<>();
        columnsToDrop.add(0);

        for (int i = numObjectiveFunctions(); i < numObjectiveFunctions() + decisionVars + slackVars; i++) {
            if (compare(tableu[0][i], 0) > 0) {
                columnsToDrop.add(i);
            }
        }

        for (int i = 0; i < numArtVars; i++) {
            int col = i + numObjectiveFunctions() + decisionVars + slackVars;
            if (getBasicRow(tableu, col) == null) {
                columnsToDrop.add(col);
            }
        }


        double[][] newTableu = new double[tableu.length - 1][tableu[0].length - columnsToDrop.size()];
        for (int i = 1; i < tableu.length; i++) {
            int col = 0;
            for (int j = 0; j < tableu[0].length; j++) {
                if (!columnsToDrop.contains(j)) {
                    newTableu[i - 1][col++] = tableu[i][j];
                }
            }
        }

        numArtVars = 0;

        return newTableu;
    }

    protected Integer getBasicRow(double[][] tableu, final int col) {
        Integer row = null;
        for (int i = 0; i < tableu.length; i++) {
            final double entry = tableu[i][col];
            if (compare(entry, 1) == 0 && (row == null)) {
                row = i;
            } else if (compare(entry, 0) != 0) {
                return null;
            }
        }
        return row;
    }

    private double[][] initP1Tableu(List<Constraint> constraintsList, double[] objective) {
        int constraintSize = constraintsList.size();
        int width = objective.length + constraintSize + numArtVars + numObjectiveFunctions() + 1;
        int height = constraintSize + numObjectiveFunctions();
        double[][] tableu = new double[height][];
        for (int i = 0; i < constraintSize + numObjectiveFunctions(); i++) {
            tableu[i] = new double[width];
        }
        if (numObjectiveFunctions() == 2) {
            tableu[0][0] = -1;
        }
        int zIndex = (numObjectiveFunctions() == 1) ? 0 : 1;
        tableu[zIndex][zIndex] = 1; // maximize
        for (int i = 0; i < objective.length; i++) {
            tableu[zIndex][i + numObjectiveFunctions()] = -objective[i];
        }

//         ???
        int slackVar = 0;
        int artificialVar = 0;
        for (int i = 0; i < constraintSize; i++) {
            Constraint constraint = constraintsList.get(i);
            int row = numObjectiveFunctions() + i;

            for (int j = 0; j < constraint.getVariablesCount(); j++) {
                tableu[row][j + numObjectiveFunctions()] = constraint.coefs[j];
            }

            tableu[row][width - 1] = constraint.rhs;

            if (constraint.relationship == Relationship.LEQ) {
                tableu[row][numObjectiveFunctions() + objective.length + slackVar++] = 1;
            } else {
                tableu[row][numObjectiveFunctions() + objective.length + slackVar++] = -1;
            }

            if (constraint.relationship != Relationship.LEQ) {
                tableu[0][numObjectiveFunctions() + objective.length + constraintSize + artificialVar] = 1;
                tableu[row][numObjectiveFunctions() + objective.length + constraintSize + artificialVar] = 1;

                for (int j = 0; j < width; j++) {
                    tableu[0][j] -= tableu[row][j];
                }
                artificialVar++;
            }
        }
        return tableu;
    }

    private int numObjectiveFunctions() {
        return numArtVars > 0 ? 2 : 1;
    }

    private void normalize(List<Constraint> constraints) {
        for (Constraint constraint : constraints) {
            if (constraint.rhs < 0) {
                constraint.opposite();
            }
        }
    }

    private void optimize(double[][] tableu) {
        while (!isOptimal(tableu)) {
            int enteringVar = selectEntering(tableu);
            if (enteringVar < 0) {
                break;
            } // optimized
            int leavingRow = selectLeavingRow(tableu, enteringVar);
            if (leavingRow < 0) {
                throw new UnboundedSolutionException();
            }

            pivot(tableu, enteringVar, leavingRow);


            if (debug) {
                System.out.println("Pivot done: ");
                System.out.println(print(tableu));
                System.out.println("Entering var: " + enteringVar);
                System.out.println("Leaving row: " + leavingRow);
            }

        }
    }

    private boolean isOptimal(double[][] tableu) {
        for (int i = numObjectiveFunctions(); i < tableu[0].length - 1; i++) {
            double entry = tableu[0][i];
            if (compare(entry, 0) < 0) {
                return false;
            }
        }
        return true;
    }

    private void pivot(double[][] tableu, int enteringVar, int leavingRow) {
        int width = tableu[0].length;

        double value = tableu[leavingRow][enteringVar];
        for (int i = 0; i < width; i++) {
            tableu[leavingRow][i] = tableu[leavingRow][i] / value;
        }
        for (int i = 0; i < tableu.length; i++) {
            if (i == leavingRow) {
                continue;
            }
            double ratio = tableu[i][enteringVar] / tableu[leavingRow][enteringVar];
            for (int j = 0; j < width; j++) {
                tableu[i][j] = tableu[i][j] - tableu[leavingRow][j] * ratio;
            }
        }
    }

    private int selectLeavingRow(double[][] tableu, int enteringVar) {
        double min = Double.MAX_VALUE;
        int leaving = -1;
        int width = tableu[0].length;
        for (int i = numObjectiveFunctions(); i < tableu.length; i++) {
            double value = tableu[i][enteringVar];
            double rhs = tableu[i][width - 1];
            if (compare(value, 0) > 0) {
                double ratio = rhs / value;
                if (compare(ratio, min) < 0) {
                    leaving = i;
                    min = ratio;
                }
            }
        }
        return leaving;
    }

    private int selectEntering(double[][] tableu) {
        double min = 1;
        int minIndex = -1;
        double[] objectiveRow = tableu[0];
        for (int i = numObjectiveFunctions(); i < objectiveRow.length - 1; i++) {
            if (compare(objectiveRow[i], 0) < 0 && compare(objectiveRow[i], min) < 0) {
                minIndex = i;
                min = objectiveRow[i];
            }
        }
        return minIndex;
    }

    static String print(double[][] tableu) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableu.length; i++) {
            double[] row = tableu[i];
            sb.append("[");
            for (int j = 0; j < row.length; j++) {
                sb.append(row[j]).append(" ");
            }
            sb.append("]\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private int compare(double a, double b) {
        double epsilon = 0.00000000001;
        double delta = a - b;
        if (delta > epsilon) {
            return 1;
        } else if (delta < -epsilon) {
            return -1;
        } else {
            return 0;
        }
    }

    private class Constraint {
        double[] coefs;
        double rhs;
        Relationship relationship;

        public Constraint(double[] coefs, double rhs, Relationship relationship) {
            this.coefs = coefs;
            this.rhs = rhs;
            this.relationship = relationship;
        }

        public void opposite() {
            for (int i = 0; i < coefs.length; i++) {
                coefs[i] = -coefs[i];
            }
            rhs = -rhs;
            if (relationship == Relationship.GEQ) {
                relationship = Relationship.LEQ;
            } else {
                relationship = Relationship.GEQ;
            }
        }

        public int getVariablesCount() {
            return coefs.length;
        }

        @Override
        public String toString() {
            return "Constraint{" +
                    "" + Arrays.toString(coefs) +
                    ", rhs=" + rhs +
                    ", " + relationship +
                    '}';
        }
    }

    enum Relationship {
        GEQ, LEQ
    }

    class UnboundedSolutionException extends RuntimeException {
    }

    class NoFeasibleSolutionException extends RuntimeException {
    }
}
