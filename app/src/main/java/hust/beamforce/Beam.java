package hust.beamforce;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Beam {
    private Double length;//梁的长度
    private Double E,I;//弹性模量、惯性矩
    private String left_constraint,right_constraint;//约束
    private List<Load> loads = Collections.synchronizedList(new ArrayList<Load>());//载荷，包括均布载荷、集中力、弯矩
    private boolean[] state = {false,false,false,false,false};

    private Double FA,FB,MA,MB,thetaA,thetaB,YA,YB;

    //setter & getter
    public boolean setLength(Double length) {
        if(length > 0){
            this.length = length;
            loads.removeAll(loads);
            state[0] = true;
            state[3] = false;
            state[4] = false;
            return true;
        }
        else {
            return false;
        }
    }
    public Double getLength() {
        return length;
    }

    public boolean setEI(Double E,Double I) {
        if((E > 0)&&(I > 0)){
            this.E = E;
            this.I = I;
            state[1] = true;
            state[4] = false;
            return true;
        }
        else {
            return false;
        }
    }
    public Double getE() {
        return E;
    }
    public Double getI() {
        return I;
    }
    public Double getEI() {
        return this.E * this.I;
    }

    public boolean setConstraint(String left_constraint,String right_constraint) {
        if(left_constraint.equals("固定端") || right_constraint.equals("固定端")){//固定端可以和任意类型搭配
            this.left_constraint = left_constraint;
            this.right_constraint = right_constraint;
            state[2] = true;
            state[4] = false;
            return true;
        } else if(left_constraint.equals("自由端") || right_constraint.equals("自由端")) {//没有固定端，则不能有自由端
            return false;
        } else if(left_constraint.equals("滑移支座") && right_constraint.equals("滑移支座")) {//既没有固定端又没有自由端，则不能都是滑移支座
            return false;
        } else {
            this.left_constraint = left_constraint;
            this.right_constraint = right_constraint;

            state[2] = true;
            state[4] = false;
            return true;
        }
    }
    public int[] getConstraintType() {
        int[] type = new int[2];
        if(left_constraint.equals("固定端")){
            type[0] = 0;
        } else if(left_constraint.equals("固定铰支座")){
            type[0] = 1;
        } else if(left_constraint.equals("滑移支座")){
            type[0] = 2;
        } else if(left_constraint.equals("自由端")){
            type[0] = 3;
        }
        if(right_constraint.equals("固定端")){
            type[1] = 0;
        } else if(right_constraint.equals("固定铰支座")){
            type[1] = 1;
        } else if(right_constraint.equals("滑移支座")){
            type[1] = 2;
        } else if(right_constraint.equals("自由端")){
            type[1] = 3;
        }
        return type;
    }

    public boolean addLoads(Load load) {
            loads.add(load);
            state[3] = true;
            state[4] = false;
            return true;
    }
    public boolean removeLoads(Load load) {
        boolean flag = false;
        Load remove_item = new Load(this);
        for(Load beam_load:loads) {
            if(load.equals(beam_load)) {
                remove_item = beam_load;
                flag = true;
            }
        }
        loads.remove(remove_item);
        if(loads.isEmpty()) {
            state[3] = false;
        }
        state[4] = false;
        return flag;
    }
    public List<Load> getLoads() {
        return loads;
    }

    //check state
    public boolean getState(int i){
        return state[i];
    }
    public boolean check() {
        if(state[0]&&state[1]&&state[2]&&state[3]) {
            return true;
        }
        return false;
    }
    public boolean checkAll() {
        if(state[0]&&state[1]&&state[2]&&state[3]&&state[4]) {
            return true;
        }
        return false;
    }
    public String info() {
        String info= "平面直梁模型的参数：\n";
        if(state[0]){
            info += "梁的长度：" + getLength() + "m\n";
        }
        if(state[1]){
            info += "梁的抗弯刚度EI：" + getEI() + "N•m²\n";
        }
        if(state[2]){
            info += "梁的约束：\n" + left_constraint + " : " + right_constraint + "\n";
        }
        if(state[3]){
            info += "梁的载荷：\n";
            for(Load beam_load:loads) {
                info += beam_load.info();
            }
        }
        if(state[4]){
            DecimalFormat ft = new DecimalFormat("0.00000");
            info += "求解结果：\n";
            info += String.format("%1$-30s %2$-30s\n","FA = "+ft.format(FA)+"N","FB = "+ft.format(FB)+"N");
            info += String.format("%1$-25s %2$-30s\n","MA = "+ft.format(MA)+"N•m","MB = "+ft.format(MB)+"N•m");
            info += String.format("%1$-30s %2$-30s\n","thetaA = "+ft.format(thetaA),"thetaB = "+ft.format(thetaB));
            info += String.format("%1$-30s %2$-30s\n","YA = "+ft.format(YA)+"m","YB = "+ft.format(YB)+"m");
        }
        return info;
    }

    //prepare for solver
    public double[][] getLeftConstraintMatrix() {
        double[][] ConstraintMatrix;
        switch (left_constraint){
            case "滑移支座":{// MA=0, yA=0
                ConstraintMatrix = new double[][]{
                        {0,0,1,0,0,0,0,0},
                        {0,0,0,0,0,0,1,0}};
                break;
            }
            case "固定铰支座":{//MA=0, yA=0
                ConstraintMatrix = new double[][]{
                        {0,0,1,0,0,0,0,0},
                        {0,0,0,0,0,0,1,0}};
                break;
            }
            case "固定端":{//qA=0, yA=0
                ConstraintMatrix = new double[][]{
                        {0,0,0,0,1,0,0,0},
                        {0,0,0,0,0,0,1,0}};
                break;
            }
            case "自由端":{//FA=0, MA=0
                ConstraintMatrix = new double[][]{
                        {1,0,0,0,0,0,0,0},
                        {0,0,1,0,0,0,0,0}};
                break;
            }
            default:{
                ConstraintMatrix = new double[][]{
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0}};
            }
        }
        return ConstraintMatrix;
    }
    public double[][] getRightConstraintMatrix() {
        double[][] ConstraintMatrix;
        switch (right_constraint){
            case "滑移支座":{// MB=0, yB=0
                ConstraintMatrix = new double[][]{
                        {0,0,0,1,0,0,0,0},
                        {0,0,0,0,0,0,0,1}};
                break;
            }
            case "固定铰支座":{//MB=0, yB=0
                ConstraintMatrix = new double[][]{
                        {0,0,0,1,0,0,0,0},
                        {0,0,0,0,0,0,0,1}};
                break;
            }
            case "固定端":{//qB=0, yB=0
                ConstraintMatrix = new double[][]{
                        {0,0,0,0,0,1,0,0},
                        {0,0,0,0,0,0,0,1}};
                break;
            }
            case "自由端":{//FB=0, MB=0
                ConstraintMatrix = new double[][]{
                        {0,1,0,0,0,0,0,0},
                        {0,0,0,1,0,0,0,0}};
                break;
            }
            default:{
                ConstraintMatrix = new double[][]{
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0}};
            }
        }
        return ConstraintMatrix;
    }

    public double[] getLoadVector() {
        double L = getLength();
        double[] LoadMatrix = new double[]{0,0,0,0,0,0,0,0};
        for(Load beam_load:loads) {
            double Fvs = beam_load.getValue();
            switch (beam_load.getType()){
                case "均布力":{
                    double locL = beam_load.getLeftPosition();
                    double locR = beam_load.getRightPosition();

                    LoadMatrix[0] += Fvs*(locR-locL);
                    LoadMatrix[1] += Fvs*(locR-locL)*(L-0.5*(locR+locL));
                    LoadMatrix[2] += Fvs*(locR-locL)*(locR-locL)*(locR-locL)/6.0 + 0.5*Fvs*(locR-locL)*(L-locR)*(L-locL);
                    LoadMatrix[3] += Fvs*(locR-locL)*(locR-locL)*(locR-locL)*(locR-locL)/24.0
                            + Fvs*(locR-locL)*(locR-locL)*(locR-locL)*(L-locR)/6.0
                            + Fvs*(locR-locL)*(2*L*L*L - 3*locL*L*L - 3*locR*L*L + 6*locL*locR*L + locR*locR*locR - 3*locL*locR*locR)/12.0;
                    break;
                }
                case "集中力":{
                    double loc = beam_load.getLeftPosition();

                    LoadMatrix[0] += Fvs;
                    LoadMatrix[1] += Fvs*(L-loc);
                    LoadMatrix[2] += 0.5*Fvs*(L-loc)*(L-loc);
                    LoadMatrix[3] += Fvs*(L-loc)*(L-loc)*(L-loc)/6.0;
                    break;
                }
                case "弯矩":{
                    double loc = beam_load.getLeftPosition();

                    LoadMatrix[0] -= 0;
                    LoadMatrix[1] -= Fvs;
                    LoadMatrix[2] -= Fvs*(L-loc);
                    LoadMatrix[3] -= 0.5*Fvs*(L-loc)*(L-loc);
                    break;
                }
            }
        }
        return LoadMatrix;
    }

    //solver
    public boolean constraintSolver() {
        if(check()){
            double l = getLength();
            double EI = getEI();
            double[][] LeftConstraintMatrix = getLeftConstraintMatrix();
            double[][] RightConstraintMatrix = getRightConstraintMatrix();
            //              FA     FB       MA    MB    qA   qB  yA   yB
            double[][] Stiffness= {
                    {        1,   -1,        0,   0,    0,   0,  0,   0},
                    {        l,    0,        1,  -1,    0,   0,  0,   0},
                    {  0.5*l*l,    0,        l,   0,   EI, -EI,  0,   0},
                    {l*l*l/6.0,    0,  0.5*l*l,   0, EI*l,   0, EI, -EI},
                    LeftConstraintMatrix[0],
                    LeftConstraintMatrix[1],
                    RightConstraintMatrix[0],
                    RightConstraintMatrix[1]};

            RealMatrix Stiffness_Matrix = new Array2DRowRealMatrix(Stiffness,true);
            DecompositionSolver solver = new LUDecomposition(Stiffness_Matrix).getSolver();

            RealVector LoadVector = new ArrayRealVector(getLoadVector(),true);
            RealVector ResultVector = solver.solve(LoadVector);

            FA = ResultVector.getEntry(0);
            FB = ResultVector.getEntry(1);
            MA = ResultVector.getEntry(2);
            MB =ResultVector.getEntry(3);
            thetaA =ResultVector.getEntry(4);
            thetaB =ResultVector.getEntry(5);
            YA = ResultVector.getEntry(6);
            YB = ResultVector.getEntry(7);

            state[4] = true;
            return true;
        }
        return false;
    }

    public float[][] pointForDraw() {
        double EI = getEI();
        float points[][] = new float[3][1001];
        float step = (float) (length/1000);
        float x = 0;

        for(int i =0; i <= 1000; i++ ){
            x = i*step;
            points[0][i] = (float) (FA*1);//剪力
            points[1][i] = (float) (MA + FA*x);//弯矩
            points[2][i] = (float) (EI*YA + EI*thetaA*x + 0.5*MA*x*x + FA*x*x*x/6.0);

            for(Load beam_load : loads){
                double Fvs = beam_load.getValue();
                double l = beam_load.getLeftPosition();
                double r = beam_load.getRightPosition();

                switch (beam_load.getType()) {
                    case "均布力":{
                        if(x >= 0 && x < l){
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        } else if(x >= l && x < r) {
                            points[0][i] -= Fvs*(x-l);
                            points[1][i] -= 0.5*Fvs*(x-l)*(x-l);
                            points[2][i] -= Fvs*(x-l)*(x-l)*(x-l)*(x-l)/24.0;
                        } else if(x >= r && x-length<=0.00001){
                            points[0][i] -= Fvs*(r-l);
                            points[1][i] -= Fvs*(r-l)*(x-0.5*(r+l));
                            points[2][i] -= Fvs*(r-l)*(r-l)*(r-l)*(r-l)/24.0
                                    +Fvs*(r-l)*(r-l)*(r-l)*(x-r)/6.0
                                    +Fvs*(r-l)*(2*x*x*x - 3*l*x*x - 3*r*x*x + 6*l*r*x + r*r*r - 3*l*r*r)/12.0;
                        } else {
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        }
                        break;
                    }
                    case "集中力":{
                        if(x >= 0 && x < l) {
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        } else if(x >= l && x-length<=0.00001) {
                            points[0][i] -= Fvs;
                            points[1][i] -= Fvs*(x-l);
                            points[2][i] -= Fvs*(x-l)*(x-l)*(x-l)/6.0;
                        } else{
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        }
                        break;
                    }
                    case "弯矩":{
                        if(x >=0 && x < l) {
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        } else if(x >= l && x-length<=0.00001) {
                            points[0][i] += 0;
                            points[1][i] += Fvs;
                            points[2][i] += Fvs*(x-l)*(x-l)/2.0;
                        } else{
                            points[0][i] += 0;
                            points[1][i] += 0;
                            points[2][i] += 0;
                        }
                        break;
                    }
                }
            }
            points[2][i] = (float) (points[2][i]/EI);
            Log.e("pointValue"+i,points[0][i]+"::"+points[1][i]+"::"+points[2][i]);
            if(i==1000){
                Log.e("xValue1000:length",x+"::"+length);
            }
        }
        return points;
    }
}
