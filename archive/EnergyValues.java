import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Equation {
    Equation(double a[][], double b[]) {
        this.a = a;
        this.b = b;
    }

    double a[][];
    double b[];
}

class Position {
    Position(int column, int raw) {
        this.column = column;
        this.raw = raw;
    }

    int column;
    int raw;
}

class EnergyValues {
    static Equation ReadEquation() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();

        double a[][] = new double[size][size];
        double b[] = new double[size];
        for (int raw = 0; raw < size; ++raw) {
            for (int column = 0; column < size; ++column)
                a[raw][column] = scanner.nextInt();
            b[raw] = scanner.nextInt();
        }
        return new Equation(a, b);
    }

    static Position SelectPivotElement(double a[][], boolean used_raws[], boolean used_columns[]) {
        int column = -1;
        for (int i = 0; i < a[0].length; i++) {
            if(used_columns[i]){
                continue;
            }
            column = i;
            break;
        }
        int row = -1;
        for (int i = 0; i < a.length; i++) {
            if(used_raws[i] || a[i][column] == 0){
                continue;
            }
            row = i;
            break;
        }
        return new Position(column,row);
    }

    static void ProcessPivotElement(double a[][], double b[], Position pivot_element) {
        double coef = a[pivot_element.raw][pivot_element.column];
        for(int j = 0; j < a[pivot_element.raw].length; j++){
            a[pivot_element.raw][j] /= coef;
        }
        b[pivot_element.raw] /= coef;

        for(int i = 0; i< a.length; i++){
            if(i == pivot_element.raw){
                continue;
            }
            coef = a[i][pivot_element.column];
            for(int j = 0; j < a[i].length; j++){
                a[i][j] -= a[pivot_element.raw][j]* coef;
            }
            b[i] -= b[pivot_element.raw]* coef;
        }
    }

    static void MarkPivotElementUsed(Position pivot_element, boolean used_raws[], boolean used_columns[]) {
        used_raws[pivot_element.raw] = true;
        used_columns[pivot_element.column] = true;
    }

    static double[] SolveEquation(Equation equation) {
        double a[][] = equation.a;
        double b[] = equation.b;
        int size = a.length;

        boolean[] used_columns = new boolean[size];
        boolean[] used_raws = new boolean[size];
        int[] basis = new int[size]; //
        for (int step = 0; step < size; ++step) {
            Position pivot_element = SelectPivotElement(a, used_raws, used_columns);
            basis[pivot_element.column] = pivot_element.raw;
            ProcessPivotElement(a, b, pivot_element);
            MarkPivotElementUsed(pivot_element, used_raws, used_columns);
        }

        double[] result = new double[size];
        for (int i = 0; i < basis.length; i++) {
            result[i] = b[basis[i]];
        }
        return result;
    }

    static void PrintColumn(double column[]) {
        int size = column.length;
        for (int raw = 0; raw < size; ++raw)
            System.out.printf("%.20f\n", column[raw]);
    }

    public static void main(String[] args) throws IOException {
        Equation equation = ReadEquation();
        double[] solution = SolveEquation(equation);
        PrintColumn(solution);
    }
}
