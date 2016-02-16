package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/13.
 */
public class BaseCount implements Cloneable {

    public BaseCount() {
        super();
    }

    public BaseCount(String gameId) {
        super();
        this.gameId = gameId;
        this.ballNum = 0;
        this.firstBase = false;
        this.secondBase = false;
        this.thirdBase = false;
    }

    public String gameId;

    // この投球を実施した後のベース状況を示す
    public int ballNum;

    public boolean firstBase;

    public boolean secondBase;

    public boolean thirdBase;

    @Override
    public BaseCount clone() {
        try {
            return (BaseCount) super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore
            return null;
        }
    }
}
