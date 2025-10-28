public class ConfigData {
    private int moveTime;
    private int stopTime;
    private int maxLevel;
    private int subfloors;

    // Getters
    public int getMoveTime() { return moveTime; }
    public void setMoveTime(int moveTime){
        if (!(moveTime > 0)) return;
        this.moveTime = moveTime;
    }

    public int getStopTime() { return stopTime; }
    public void setStopTime(int stopTime){
        if(!(stopTime > 0)) return;
        this.stopTime = stopTime;
    }

    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel){
        if(!(this.maxLevel > 1)) return;
        this.maxLevel = maxLevel;

    }

    public int getSubfloors() { return subfloors; }
    public void setSubfloors(int subfloors) {
        if(!(subfloors >= 0 && subfloors < this.maxLevel)) return;
        this.subfloors = subfloors;
    }

    public ConfigData(int moveTime, int stopTime, int maxLevel, int subfloors){
        this.moveTime = (moveTime > 0)? moveTime : 1000;
        this.stopTime = (stopTime > 0)? stopTime : 1000;
        this.maxLevel = (maxLevel > 1)? maxLevel : 10;
        this.subfloors = (subfloors >= 0 && subfloors < this.maxLevel)? subfloors: 0;
    }

    public ConfigData(){
        this(0, 0, 0, 0);
    }

    @Override
    public String toString() {
        return "ElevatorConfig{" +
                "moveTime=" + moveTime +
                ", stopTime=" + stopTime +
                ", maxLevel=" + maxLevel +
                ", subfloors=" + subfloors +
                '}';
    }
}
