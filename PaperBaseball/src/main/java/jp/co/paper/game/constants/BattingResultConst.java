package jp.co.paper.game.constants;

/**
 * 打撃結果IDのマスタ
 * Created by kawakami_note on 2015/08/13.
 */
public class BattingResultConst {
    /** 判定前 */
    public static final int NO_RESULT = 0;

    /** 打撃結果の残らないボール判定 */
    public static final int NO_RESULT_BALL = 1;

    /** 打撃結果の残らない見逃しまたは空振りのストライク */
    public static final int NO_RESULT_STRIKE = 2;

    /** 打撃結果の残らないファール判定 */
    public static final int NO_RESULT_FOUL = 3;

    public static final int HOME_RUN = 4;

    public static final int THREE_BASE_HIT = 5;

    public static final int TWO_BASE_HIT = 6;

    public static final int SINGLE_HIT = 7;

    public static final int FOUR_BALL = 8;

    public static final int STRIKE_OUT = 9;

    public static final int FLY_OUT = 10;

    /** ゴロアウト(進塁打)、三塁ランナーの得点を認める */
    public static final int GROUND_OUT_CRITICAL = 11;

    /** 一塁打だが、二塁ランナーの得点を認める */
    public static final int SINGLE_HIT_CRITICAL = 12;

    /** フライアウトだが、三塁ランナーの得点を認める */
    public static final int FLY_OUT_CRITICAL = 13;

    /** 三塁ランナーの得点を認めない */
    public static final int GROUND_OUT = 14;

    /** ゲッツー、三塁ランナーの得点を認めない */
    public static final int GET_TWO = 15;
}
