package jp.co.paper.game.controller.io.internal;

/**
 * Created by kawakami_note on 2015/08/17.
 */
public class InningResultOut {
    // 回数
    public int inning;

    // 表の得点
    public Integer topScore;

    public Integer topHitNum;

    // 裏の得点 null → 表のみ。
    public Integer bottomScore;

    public Integer bottomHitNum;

    // true : 最終回
    public boolean lastInning;

    // true : 裏の攻撃に「X」印がつく
    public boolean suddenDeath;
}
