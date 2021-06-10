class SumBoundedArrayIterator implements java.util.Iterator<int[]>, Iterable<int[]>{
    int length;
    int maxSum;
    private int[] nextArray;
    private int nextSum;

    public SumBoundedArrayIterator(int length, int maxSum){
        this.length = length;
        this.maxSum = maxSum;
        this.nextArray = new int[length];
        this.nextSum = 0;
    }
    public boolean hasNext(){return nextArray != null;}
    public int[] next(){
        int[] r = nextArray.clone();
        roll();
        return r;
    }
    private void roll(){
        if(nextArray==null || nextArray[length-1]==maxSum){
            nextArray = null;
            nextSum = 0;
            return;
        }else if(nextSum < maxSum){
            nextArray[0]++;
            nextSum++;
            return;
        }else{
            for(int i=0; i<length; i++){
                if(nextArray[i] > 0){
                    nextArray[i+1]++; nextSum++;
                    for(int j=0; j<=i; j++){
                        nextSum -= nextArray[j];
                        nextArray[j] = 0;
                    }
                    return;
                }
            }
        }        
    }

    public java.util.Iterator<int[]> iterator() {return this;}
}