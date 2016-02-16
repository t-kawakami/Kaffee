package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class Pitching {
    /** 試合ID */
    public String gameId;

    /** 投球番号 */
    public int ballNum;

    /** 投球コース(X軸 1～5) */
    public int courseX;

    /** 投球コース(Y軸 1～5) */
    public int courseY;

    /** 変化球ID */
    public int ballKindId;
}
