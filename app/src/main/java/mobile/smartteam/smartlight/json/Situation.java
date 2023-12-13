package mobile.smartteam.smartlight.json;

public class Situation {

    private int sleep;
    private int eat;
    private int study;

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public int getEat() {
        return eat;
    }

    public void setEat(int eat) {
        this.eat = eat;
    }

    public int getStudy() {
        return study;
    }

    public void setStudy(int study) {
        this.study = study;
    }

    @Override
    public String toString() {
        return "Situation{" +
                "sleep=" + sleep +
                ", eat=" + eat +
                ", study=" + study +
                '}';
    }
}
