package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class InningResult {

    public InningResult() {
        super();
    }

    public InningResult(String gameId, int inning, boolean top) {
        super();
        this.gameId = gameId;
        this.inning = inning;
        this.top = top;
        this.score = 0;
        this.hitNum = 0;
    }

    public String gameId;

    public int inning;

    /** trueなら表 */
    public boolean top;

    public int score;

    public int hitNum;
}