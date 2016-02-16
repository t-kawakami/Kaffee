package jp.co.paper.game.controller.io.internal;

/**
 * 一球単位での打撃結果
 * Created by kawakami_note on 2015/08/12.
 */
public class BattingResultOut {
    /** 打撃結果表示名 */
    public String battingResultName;

    /** 打撃結果に伴うストライクカウント */
    public int strikeCount;

    /** 打撃結果に伴うボールカウント */
    public int ballCount;

    /** 打撃結果に伴うアウトカウント */
    public int outCount;
}
