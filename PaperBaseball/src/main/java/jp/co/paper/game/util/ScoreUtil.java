package jp.co.paper.game.util;

import jp.co.paper.game.domain.BaseCount;
import jp.co.paper.game.constants.BattingResultConst;

/**
 * Created by kawakami_note on 2015/08/14.
 */
public class ScoreUtil {
    private ScoreUtil() {
        // インスタンス化を防止する
    }

    /**
     * 塁上のランナーと打撃結果から取得した得点を返す
     * @param baseCount
     * @param battingResultId
     * @return
     */
    public static int calculateGetScore(BaseCount baseCount, int battingResultId) {
        int score = 0;
        if (battingResultId == BattingResultConst.HOME_RUN) {
            score++;
            if (baseCount.firstBase) {
                score++;
            }
            if (baseCount.secondBase) {
                score++;
            }
            if (baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.THREE_BASE_HIT) {
            if (baseCount.firstBase) {
                score++;
            }
            if (baseCount.secondBase) {
                score++;
            }
            if (baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.TWO_BASE_HIT) {
            if (baseCount.secondBase) {
                score++;
            }
            if (baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.SINGLE_HIT_CRITICAL) {
            // シングルヒットで2塁ベースから生還
            if (baseCount.secondBase) {
                score++;
            }
            if (baseCount.thirdBase) {
                score++;
            }
        }
        else if (battingResultId == BattingResultConst.SINGLE_HIT) {
            if (baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.FOUR_BALL) {
            // 満塁時のみ得点が入る
            if (baseCount.firstBase && baseCount.secondBase && baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.FLY_OUT_CRITICAL) {
            // 犠牲フライ
            if (baseCount.thirdBase) {
                score++;
            }
        } else if (battingResultId == BattingResultConst.GROUND_OUT_CRITICAL) {
            // 進塁打による得点
            if (baseCount.thirdBase) {
                score++;
            }
        }
        return score;
    }
}
