package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class Batting {
    /** 試合ID */
    public String gameId;

    /** 投球数 */
    public int ballNum;

    /** 打撃コース(X軸 1～5) 見逃しの場合はnull */
    public Integer courseX;

    /** 打撃コース(Y軸 1～5) 見逃しの場合はnull */
    public Integer courseY;

    /** 狙いの変化球種 見逃しの場合はnull */
    public Integer ballKindId;
}
