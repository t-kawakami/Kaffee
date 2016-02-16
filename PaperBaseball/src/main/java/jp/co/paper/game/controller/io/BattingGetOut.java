package jp.co.paper.game.controller.io;

import jp.co.paper.game.controller.io.internal.BattingResultOut;
import jp.co.paper.game.controller.io.internal.GameResultOut;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class BattingGetOut {
    /** 完了フラグ。falseの場合は投球内容が未入力 */
    public boolean finished;

    /** 打者の狙ったコース(X軸)見逃しの場合はnull */
    public Integer battingCourseX;

    /** 打者の狙ったコース(Y軸)見逃しの場合はnull */
    public Integer battingCourseY;

    /** 打者の狙った球種ID 見逃しの場合はnull */
    public Integer battingBallKindId;

    /** 打者の狙った球種名 見逃しの場合はnull */
    public String battingBallKindName;

    /** 投手の狙ったコース(X軸) */
    public int pitchingCourseX;

    /** 投手の狙ったコース(Y軸) */
    public int pitchingCourseY;

    /** 投手の投げた球種ID */
    public int pitchingBallKindId;

    /** 投手の投げた球種名 */
    public String pitchingBallName;

    /** 打撃結果 */
    public BattingResultOut battingResult;

    /** 試合進行結果 */
    public GameResultOut gameResult;
}
