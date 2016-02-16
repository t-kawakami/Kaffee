package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/13.
 */
public class BattingCount implements Cloneable {
    public String gameId;

    public int ballNum;

    public int inning;

    /** trueなら表 */
    public boolean top;

    public int strikeCount;

    public int ballCount;

    public int outCount;

    @Override
    public BattingCount clone() {
        try {
            return (BattingCount) super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore
            return null;
        }
    }
}
