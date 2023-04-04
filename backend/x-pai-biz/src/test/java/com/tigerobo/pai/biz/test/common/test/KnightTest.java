package com.tigerobo.pai.biz.test.common.test;

public class KnightTest {

    private static final int m = 8;
    private static final int n=8;
    static int[][] boardMatrix =new int[m][n];
    static int[][] usedFlag =new int[m][n];
    static int startX = 2;
    static int starty = 3;
    public static void createMatrix(){

        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                boardMatrix[i][j]=0;
                usedFlag[i][j]=0;
            }
        }
    }

    static int[] step1={1,1,-1,-1,2,2,-2,-2};
    static int[] step2={2,-2,2,-2,1,-1,1,-1};
    public static boolean check(int x,int y){
        if(x>=m||x<0||y>=n||y<0|| usedFlag[x][y]==1)
            return false;
        return true;
    }
    public static boolean dfs(int x,int y,int step){
        System.out.println("step:"+step);
        usedFlag[x][y]=1;
        boardMatrix[x][y]=step;
        if(step==m*n){
            print();
            return true;
        }else if (step>m*n){
            return false;
        }
        int stepTypeNum = 8;
        for(int i=0;i<stepTypeNum;i++){
            if(check(x+step1[i],y+step2[i])){
                System.out.println("step:"+step+",i:"+i);
                step++;
                int nextX = x + step1[i];
                int nextY = y + step2[i];
                boolean result=dfs(nextX, nextY,step);//参数局部变量，并不对x做改变
                if(result==true)//一直走到最后，若满足条件输出，若不满足回溯
                    return true;
                //这里节点使用的是局部变量x+step1，因此节点不用回溯

                usedFlag[nextX][nextY]=0;
                boardMatrix[nextX][nextY]=0;

                step--;

            }
        }

        return false;
    }
    public static void print(){
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                System.out.print(boardMatrix[i][j]+" ");
            }
            System.out.println("");
        }
    }
    public static void main(String[] args) {
        createMatrix();

        boolean f=dfs(startX, starty, 1);
        System.out.println(f);
    }
}
