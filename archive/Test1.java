import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.UnboundedSolutionException;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Test1 {

    @Test
    public void test() throws IOException {

        Random random = new Random(100);
        int index = 0;

        test:
        while (true) {
            if (index++ % 10000 == 0) {
                System.out.println(index);
            }
            double[] commonsResult = null;
            PointValuePair solution = null;
            boolean unbounded = false;
            boolean unfeacible = false;
            double[] objective = null;
            List constraintsList = null;
            List<Double> constraintsRhs = null;
            int variables = random.nextInt(3) + 2;
            int constraintNumber = random.nextInt(3) + 1;
            int bound = 1000;
            try {
                constraintsList = new ArrayList();
                constraintsRhs = new ArrayList<>();
                for (int i = 0; i < constraintNumber; i++) {
                    double[] contraint = new double[variables];
                    for (int j = 0; j < variables; j++) {
                        contraint[j] = random.nextInt(bound) - bound / 2;
                    }
                    constraintsList.add(contraint);
                    constraintsRhs.add((double) random.nextInt(bound) - bound / 2);
                }
                objective = new double[variables];
                for (int i = 0; i < variables; i++) {
                    objective[i] = random.nextInt(bound) - bound / 2;
                }


                LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0);

                Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
                for (int i = 0; i < constraintNumber; i++) {
                    constraints.add(new LinearConstraint((double[]) constraintsList.get(i), Relationship.LEQ, constraintsRhs.get(i)));
                }

                SimplexSolver solver = new SimplexSolver();
                solution = solver.optimize(f, new LinearConstraintSet(constraints), GoalType.MAXIMIZE, new NonNegativeConstraint(true));
                commonsResult = solution.getPoint();
            } catch (UnboundedSolutionException exception) {
                unbounded = true;
            } catch (NoFeasibleSolutionException e) {
                unfeacible = true;
            }
            boolean myUnbounded = false;
            boolean myUnfeisible = false;
            double[] myResult = null;
            try {
                myResult = new MySimplexSolver().solve(objective, constraintsList, constraintsRhs);
            } catch (MySimplexSolver.UnboundedSolutionException e) {
                myUnbounded = true;
            } catch (MySimplexSolver.NoFeasibleSolutionException e) {
                myUnfeisible = true;
            }
            if (unbounded != myUnbounded) {
                System.out.println("Input values: ");
                for (int i = 0; i < constraintsList.size(); i++) {
                    System.out.print(Arrays.toString((double[]) constraintsList.get(i)));
                    System.out.print(" ");
                    System.out.println(constraintsRhs.get(i));
                }
                System.out.println("Obbjective: ");
                System.out.println(Arrays.toString(objective));
                System.out.println("Different unbounded results: my " + myUnbounded + " commons: " + unbounded);
                throw new RuntimeException();
            }
            if (unfeacible != myUnfeisible) {
                System.out.println("Input values: ");
                for (int i = 0; i < constraintsList.size(); i++) {
                    System.out.print(Arrays.toString((double[]) constraintsList.get(i)));
                    System.out.print(" ");
                    System.out.println(constraintsRhs.get(i));
                }
                System.out.println("Obbjective: ");
                System.out.println(Arrays.toString(objective));
                System.out.println("Different unfeacible results: my " + myUnfeisible + " commons: " + unfeacible);
                throw new RuntimeException();
            }
            if (unbounded || unfeacible) {
                continue;
            }
            if (commonsResult.length != myResult.length) {
                System.out.println("Input values: ");
                for (int i = 0; i < constraintsList.size(); i++) {
                    System.out.print(Arrays.toString((double[]) constraintsList.get(i)));
                    System.out.print(" ");
                    System.out.println(constraintsRhs.get(i));
                }
                System.out.println("Obbjective: ");
                System.out.println(Arrays.toString(objective));
                System.out.println("Different results length: " + Arrays.toString(commonsResult) + " my: " + Arrays.toString(myResult));
                throw new RuntimeException();
            }
            for (int i = 0; i < myResult.length; i++) {
                if (Math.abs(commonsResult[i] - myResult[i]) > 0.0000001) {
                    double myObjective = calculateObjective(objective, myResult);
                    double commonsObjective = calculateObjective(objective, commonsResult);
                    if (Math.abs(commonsObjective - myObjective) < 0.00000000001) {
                        continue test;
                    }
                    System.out.println("Input values: ");
                    for (int j = 0; j < constraintsList.size(); j++) {
                        System.out.print(Arrays.toString((double[]) constraintsList.get(j)));
                        System.out.print(" ");
                        System.out.println(constraintsRhs.get(j));
                    }
                    System.out.println("Obbjective: ");
                    System.out.println(Arrays.toString(objective));
                    System.out.println("Different bounded results: commons " + Arrays.toString(commonsResult) + " my: " + Arrays.toString(myResult));
                    System.out.println("Commons solution is" + checkSolution(constraintsList, constraintsRhs, commonsResult));
                    System.out.println("My solution is" + checkSolution(constraintsList, constraintsRhs, myResult));

                    throw new RuntimeException();
                }
            }
        }
    }

    private double calculateObjective(double[] objective, double[] myResult) {
        double sum = 0;
        for (int i = 0; i < objective.length; i++) {
            sum += objective[i] * myResult[i];
        }
        return sum;
    }

    private boolean checkSolution(List constraintsList, List<Double> constraintsRhs, double[] commonsResult) {
        for (int i = 0; i < constraintsList.size(); i++) {
            double[] constraint = (double[]) constraintsList.get(i);
            double sum = 0;
            for (int j = 0; j < constraint.length; j++) {
                sum += constraint[j] * commonsResult[j];
            }
            if (sum - constraintsRhs.get(i) > 0.00000000000001) {
                System.out.println("Wrong at constraint " + Arrays.toString(constraint) + "<=" + constraintsRhs.get(i));
                return false;
            }
        }
        return true;
    }

    @Test
    public void testOne() throws IOException {

        Random random = new Random(100);

        double[] commonsResult = null;
        PointValuePair solution = null;
        boolean unbounded = false;
        boolean unfeacible = false;
        double[] objective = null;
        List constraintsList = null;
        List<Double> constraintsRhs = null;
        try {
            constraintsList = new ArrayList();

            constraintsList.add(new double[]{-64.0, -75.0, 424.0});
            constraintsList.add(new double[]{447.0, 182.0, -93.0});
            constraintsList.add(new double[]{-254.0, 185.0, -94.0});

            constraintsRhs = new ArrayList<>();
            constraintsRhs.add(-329.0);
            constraintsRhs.add(213.0);
            constraintsRhs.add(-410.0);
            objective = new double[]{-146.0, 497.0, -377.0};

            LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0);

            Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
            for (int i = 0; i < constraintsList.size(); i++) {
                constraints.add(new LinearConstraint((double[]) constraintsList.get(i), Relationship.LEQ, constraintsRhs.get(i)));
            }

            SimplexSolver solver = new SimplexSolver();
            solution = solver.optimize(f, new LinearConstraintSet(constraints), GoalType.MAXIMIZE, new NonNegativeConstraint(true));
            commonsResult = solution.getPoint();
        } catch (UnboundedSolutionException exception) {
            unbounded = true;
        } catch (NoFeasibleSolutionException e) {
            unfeacible = true;
        }
        new MySimplexSolver().solve(objective, constraintsList, constraintsRhs);
    }
}
