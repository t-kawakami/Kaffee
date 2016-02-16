package jp.co.paper.game.controller.io.internal;

import java.util.List;

/**
 * 試合進行結果
 * Created by kawakami_note on 2015/08/12.
 */
public class GameResultOut {
    // 各イニングの情報
    public List<InningResultOut> inningResultList;

    // true : 試合終了
    public boolean gameOver;
}
