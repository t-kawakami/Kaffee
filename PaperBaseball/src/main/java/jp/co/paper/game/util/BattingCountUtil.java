package jp.co.paper.game.util;

import jp.co.paper.game.constants.BattingResultConst;
import jp.co.paper.game.domain.BattingCount;
import jp.co.paper.game.domain.BattingResult;

/**
 * Created by kawakami_note on 2015/08/13.
 */
public class BattingCountUtil {
    private BattingCountUtil() {
        // インスタンス化を防止する
    }

    public static BattingCount initBattingCount(String gameId) {
        BattingCount battingCount = new BattingCount();
        battingCount.gameId = gameId;
        battingCount.ballNum = 0;
        battingCount.top = true;
        battingCount.inning = 1;
        battingCount.ballCount = 0;
        battingCount.strikeCount = 0;
        battingCount.outCount = 0;
        return battingCount;
    }

    /**
     * 1球前のボールカウントから、打撃前のボールカウントを準備する。
     * ほとんどの場合は1球前のボールカウントをそのまま返すが、下記の条件の場合にボールカウントを変更する。
     * ▼3アウトがカウントされている場合
     *   1.ボール、ストライク、アウトカウントを0にする。
     *   2.イニングが表の場合は裏にする。裏の場合は表にし、さらにイニングを進める。
     * ▼3ストライクがカウントされている場合
     *   1.ボール、ストライクカウントを0にする。
     * ▼4ボールがカウントされている場合
     *   1.ボール、ストライクカウントを0にする。
     *
     * @param preBattingCount
     * @return
     */
    public static BattingCount prepareBattingCount(BattingCount preBattingCount, BattingResult preBattingResult) {
        BattingCount battingCount = preBattingCount.clone();
        battingCount.ballNum++;
        // 何らかの打撃結果を残している場合はストライクカウントとボールカウントをリセットする
        if (hasResult(preBattingResult)) {
            battingCount.ballCount = 0;
            battingCount.strikeCount = 0;
        }
        // ３アウト以上の場合はアウトカウントをリセットし、イニングを進める
        if (preBattingCount.outCount >= 3) {
            battingCount.outCount = 0;
            if (preBattingCount.top) {
                battingCount.top = false;
            } else {
                battingCount.top = true;
                battingCount.inning = preBattingCount.inning++;
            }
        }
        return battingCount;
    }

    /**
     * 打撃結果があるか判定する
     * @param preBattingResult
     * @return true:打撃結果あり
     */
    private static boolean hasResult(BattingResult preBattingResult) {
        if (preBattingResult == null) {
            return false;
        }
        if (preBattingResult.battingResultId == BattingResultConst.NO_RESULT ||
                preBattingResult.battingResultId == BattingResultConst.NO_RESULT_BALL ||
                preBattingResult.battingResultId == BattingResultConst.NO_RESULT_STRIKE ||
                preBattingResult.battingResultId == BattingResultConst.NO_RESULT_FOUL) {
            return false;
        }
        return true;
    }
}
