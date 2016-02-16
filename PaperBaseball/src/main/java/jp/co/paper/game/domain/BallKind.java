package jp.co.paper.game.domain;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class BallKind {
    /** 変化球種ID */
    public int ballKindId;

    /** 投球コースから、X軸方向にいくつ移動した部分のコースの定義か */
    public int courseXVector;

    /** 投球コースから、Y軸方向にいくつ移動した部分のコースの定義か */
    public int courseYVector;

    /** コース一致時の判定ID */
    public int ruleId;
}
