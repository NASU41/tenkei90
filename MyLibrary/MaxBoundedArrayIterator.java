class MaxBoundedArrayIterator implements java.util.Iterator<int[]>, Iterable<int[]>{
    int length;
    int max;
    private int[] nextArray;

    public MaxBoundedArrayIterator(int length, int max){
        this.length = length;
        this.max = max;
        this.nextArray = new int[length];
    }
    public boolean hasNext(){return nextArray != null;}
    public int[] next(){
        int[] r = nextArray.clone();
        roll();
        return r;
    }
    private void roll(){
        if(nextArray==null){
            nextArray = null;
            return;
        }else{
            for(int i=length-1; i>=0; i--){
                if(nextArray[i] < max){
                    nextArray[i]++;
                    for(int j=i+1; j<length; j++) nextArray[j] = 0;
                    return;
                }
            }
            nextArray = null;
        }
    }

    public java.util.Iterator<int[]> iterator() {return this;}
}