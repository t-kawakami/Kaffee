package jp.co.paper.game.util;

import jp.co.paper.game.domain.BaseCount;
import jp.co.paper.game.domain.BattingCount;
import jp.co.paper.game.domain.BattingResult;
import jp.co.paper.game.constants.BattingResultConst;

/**
 * Created by kawakami_note on 2015/08/13.
 */
public class BaseCountUtil {
    private  BaseCountUtil() {
        // インスタンス化を防止する
    }

    /**
     * １球前の塁上のランナーの状態と打撃結果から、打撃前の塁上のランナーの状態を準備する
     * @param gameId
     * @param preBaseCount
     * @param preBattingResult
     * @return
     */
    public static BaseCount updateBaseCount(String gameId, BaseCount preBaseCount, BattingResult preBattingResult, BattingCount preBattingCount) {
        // チェンジ
        if (preBattingCount.outCount == 3) {
            BaseCount baseCount = new BaseCount(gameId);
            baseCount.ballNum = preBaseCount.ballNum + 1;
            return baseCount;
        }
        // イニングの継続
        BaseCount baseCount = preBaseCount.clone();
        baseCount.ballNum++;
        if (preBattingResult.battingResultId == BattingResultConst.HOME_RUN) {
            baseCount.thirdBase = false;
            baseCount.secondBase = false;
            baseCount.firstBase = false;
        } else if (preBattingResult.battingResultId == BattingResultConst.THREE_BASE_HIT) {
            baseCount.thirdBase = true;
            baseCount.secondBase = false;
            baseCount.firstBase = false;
        } else if (preBattingResult.battingResultId == BattingResultConst.TWO_BASE_HIT) {
            baseCount.firstBase = false;
            baseCount.secondBase = true;
            baseCount.thirdBase = preBaseCount.firstBase;
        } else if (preBattingResult.battingResultId == BattingResultConst.SINGLE_HIT_CRITICAL) {
            // 二塁ランナーの得点を認める一塁打
            baseCount.firstBase = true;
            baseCount.secondBase = preBaseCount.firstBase;
            baseCount.thirdBase = false;
        } else if (preBattingResult.battingResultId == BattingResultConst.SINGLE_HIT) {
            // 一塁打はすべてのランナーが前進
            baseCount.firstBase = true;
            baseCount.secondBase = preBaseCount.firstBase;
            baseCount.thirdBase = preBaseCount.secondBase;
        } else if (preBattingResult.battingResultId == BattingResultConst.FOUR_BALL) {
            // 四球は詰まっているランナーが前進
            baseCount.firstBase = true;
            baseCount.secondBase = preBaseCount.firstBase;
            baseCount.thirdBase = preBaseCount.firstBase && preBaseCount.secondBase;
        } else if (preBattingResult.battingResultId == BattingResultConst.FLY_OUT_CRITICAL) {
            // 犠牲フライ
            baseCount.thirdBase = false;
        } else if (preBattingResult.battingResultId == BattingResultConst.GROUND_OUT_CRITICAL) {
            // 進塁打
            baseCount.thirdBase = preBaseCount.secondBase;
            baseCount.secondBase = preBaseCount.firstBase;
            baseCount.firstBase = false;
        } else if (preBattingResult.battingResultId == BattingResultConst.GROUND_OUT) {
            // フォースアウトならゲッツー判定
            if (preBaseCount.firstBase && preBaseCount.secondBase && preBaseCount.thirdBase) {
                baseCount.thirdBase = false;
            } else if (preBaseCount.firstBase && preBaseCount.secondBase) {
                baseCount.secondBase = false;
            } else if (preBaseCount.firstBase) {
                baseCount.firstBase = false;
            }
        }
        return baseCount;
    }
}
