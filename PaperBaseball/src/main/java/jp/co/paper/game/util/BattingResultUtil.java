package jp.co.paper.game.util;

import jp.co.paper.game.domain.BaseCount;
import jp.co.paper.game.domain.Batting;
import jp.co.paper.game.domain.BattingResult;
import jp.co.paper.game.domain.Pitching;
import jp.co.paper.game.util.entity.CourseRule;
import jp.co.paper.game.constants.BattingResultConst;
import jp.co.paper.game.constants.CourseRuleConst;

import java.util.List;

/**
 * 打撃結果判定
 * Created by kawakami_note on 2015/08/13.
 */
public class BattingResultUtil {

    private BattingResultUtil() {
        // インスタンス化を防止する
    }

    public static BattingResult prepareBattingResult(String gameId, int ballNum) {
        BattingResult battingResult = new BattingResult();
        battingResult.gameId = gameId;
        battingResult.battingResultId = BattingResultConst.NO_RESULT;
        battingResult.strike = false;
        battingResult.ball = false;
        battingResult.foul = false;
        battingResult.ballNum = ballNum;
        return battingResult;
    }

    /**
     * 打撃結果がホームランであるかを判定する
     * ホームランとする判定条件
     * 1.ストライクゾーンへの投球であること。
     * 2.球種が一致すること
     * 3.コースが一致すること
     *
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @return true;ホームランである
     */
    public static boolean isHomeRun(Batting batting, Pitching pitching) {
        if (isBallMissed(batting)) {
            return false;
        }
        return (isStrikePitch(pitching) && isMatchBallKind(batting, pitching) && isMatchCourse(batting, pitching));
    }

    /**
     * 打撃結果が二塁打であるかを判定する。
     * 二塁打とする条件
     * ▼ストライクゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.コースが一致すること
     * ▼ボールゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.コースが一致すること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @return true:二塁打である。
     */
    public static boolean isTwoBaseHit(Batting batting, Pitching pitching) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return !isMatchBallKind(batting, pitching) && isMatchCourse(batting, pitching);
        } else {
            return isMatchBallKind(batting, pitching) && isMatchCourse(batting, pitching);
        }
    }

    /**
     * 打撃結果が一塁打（二塁ランナーの得点を認める）であるかを判定する
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.ヒットコースへの打撃であること
     * ■ボールゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.コースが一致すること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList コースの一覧
     * @return true:一塁打である。
     */
    public static boolean isSingleHitCritical(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return isMatchBallKind(batting, pitching) && isHitCourse(batting, courseRuleList);
        } else {
            return !isMatchBallKind(batting, pitching) && isMatchCourse(batting, pitching);
        }
    }

    /**
     * 打撃結果が一塁打（二塁ランナーの得点を認めない）であるかを判定する
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.ヒットコースへの打撃であること
     * ■ボールゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.ヒットコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList コースの一覧
     * @return true:一塁打である。
     */
    public static boolean isSingleHit(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return !isMatchBallKind(batting, pitching) && isHitCourse(batting, courseRuleList);
        } else {
            return isMatchBallKind(batting, pitching) && isHitCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がファウルであるかを判定する。
     * ファウルとする判定条件
     * ■ストライクゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.フライコースまたはゴロコースへの打撃であること
     * ■ボールゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.ヒットコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:ファウルである。
     */
    public static boolean isFoul(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return isMatchBallKind(batting, pitching) && (isFlyCourse(batting, courseRuleList) || isGroundCourse(batting, courseRuleList));
        } else {
            return !isMatchBallKind(batting, pitching) && isHitCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がフライアウト(ランナー進塁)であるかを判定する。
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.フライコースへの打撃であること
     * ■ボールゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.フライコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:フライアウトである。
     */
    public static boolean isFlyOutCritical(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return !isMatchBallKind(batting, pitching) && isFlyCourse(batting, courseRuleList);
        } else {
            return isMatchBallKind(batting, pitching) && isFlyCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がフライアウトであるかを判定する。
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   なし
     * ■ボールゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.フライコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:フライアウトである。
     */
    public static boolean isFlyOut(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return false;
        } else {
            return !isMatchBallKind(batting, pitching) && isFlyCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がゴロアウト(三塁ランナーの得点認める)であるかを判定する。
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.ゴロコースへの打撃であること
     * ■ボールゾーンへの投球の場合
     *   1.球種が一致すること
     *   2.ゴロコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:ゴロアウトである。
     */
    public static boolean isGroundOutCritical(Batting batting, Pitching pitching, List<CourseRule> courseRuleList) {
        if (isBallMissed(batting)) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return !isMatchBallKind(batting, pitching) && isGroundCourse(batting, courseRuleList);
        } else {
            return isMatchBallKind(batting, pitching) && isGroundCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がゴロアウト(三塁ランナーの得点認めない)であるかを判定する。
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   なし
     * ■ボールゾーンへの投球の場合
     *   1.球種が一致しないこと
     *   2.ゴロコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:ゴロアウトである。
     */
    public static boolean isGroundOut(Batting batting, Pitching pitching, List<CourseRule> courseRuleList, BaseCount baseCount) {
        if (isBallMissed(batting)) {
            return false;
        }
        // フォースプレーの場合はゲッツー判定になる
        if (baseCount.firstBase) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return false;
        } else {
            return !isMatchBallKind(batting, pitching) && isGroundCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がゲッツーであるかを判定する
     * 判定条件
     * ■ストライクゾーンへの投球の場合
     *   なし
     * ■ボールゾーンへの投球の場合
     *   1.球種が不一致であること
     *   2.ゴロコースへの打撃であること
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @param courseRuleList ヒットコースの一覧
     * @return true:ゴロアウトである。
     */
    public static boolean isGetTwo(Batting batting, Pitching pitching, List<CourseRule> courseRuleList, BaseCount baseCount) {
        if (isBallMissed(batting)) {
            return false;
        }
        // フォースプレーである必要がある
        if (baseCount.firstBase == false) {
            return false;
        }
        if (isStrikePitch(pitching)) {
            return false;
        } else {
            return !isMatchBallKind(batting, pitching) && isGroundCourse(batting, courseRuleList);
        }
    }

    /**
     * 打撃結果がボールであるかを判定する
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @return true:ボールである。
     */
    public static boolean isBall(Batting batting, Pitching pitching) {
        if (isStrikePitch(pitching)) {
            return false;
        }
        if (isBallMissed(batting)) {
            return true;
        }
        return false;
    }

    /**
     * 投球がストライクであることを判定する
     *
     * @param pitching ピッチング入力
     * @return true:ストライクである
     */
    public static boolean isStrikePitch(Pitching pitching) {
        return (isStrikeCourse(pitching.courseX) && isStrikeCourse(pitching.courseY));
    }

    /**
     * 球種の一致を判定する
     *
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @return true:球種が一致している
     */
    private static boolean isMatchBallKind(Batting batting, Pitching pitching) {
        return (batting.ballKindId == pitching.ballKindId);
    }

    /**
     * コースの一致を判定する
     *
     * @param batting  バッティング入力
     * @param pitching ピッチング入力
     * @return true:コースが一致している
     */
    private static boolean isMatchCourse(Batting batting, Pitching pitching) {
        return (batting.courseX == pitching.courseX && batting.courseY == pitching.courseY);
    }

    /**
     * ヒットコースの打撃であるかを判定する
     * @param batting バッティング入力
     * @param courseRuleList コース一覧
     * @return true:ヒットコースの打撃であるか
     */
    private static boolean isHitCourse(Batting batting, List<CourseRule> courseRuleList) {
        for (CourseRule hitCourse : courseRuleList) {
            if (batting.courseX == hitCourse.courseX
                    && batting.courseY == hitCourse.courseY
                    && hitCourse.ruleId == CourseRuleConst.NORMAL_HIT_COURSE) {
                return true;
            }
        }
        return false;
    }

    /**
     * フライコースの打撃であるかを判定する
     * @param batting バッティング入力
     * @param courseRuleList コース一覧
     * @return true:フライコースの打撃であるか
     */
    private static boolean isFlyCourse(Batting batting, List<CourseRule> courseRuleList) {
        for (CourseRule hitCourse : courseRuleList) {
            if (batting.courseX == hitCourse.courseX
                    && batting.courseY == hitCourse.courseY
                    && hitCourse.ruleId == CourseRuleConst.FLY_COURSE) {
                return true;
            }
        }
        return false;
    }

    /**
     * ゴロコースの打撃であるかを判定する
     * @param batting バッティング入力
     * @param courseRuleList コース一覧
     * @return true:ゴロコースの打撃であるか
     */
    private static boolean isGroundCourse(Batting batting, List<CourseRule> courseRuleList) {
        for (CourseRule hitCourse : courseRuleList) {
            if (batting.courseX == hitCourse.courseX
                    && batting.courseY == hitCourse.courseY
                    && hitCourse.ruleId == CourseRuleConst.GROUND_COURSE) {
                return true;
            }
        }
        return false;
    }

    /**
     * ボールを見逃したか判定する
     * @param batting バッティング入力
     * @return true:見逃しである
     */
    private static boolean isBallMissed(Batting batting) {
        return (batting.courseX == null || batting.courseY == null || batting.ballKindId == null);
    }

    /**
     * ストライクコースであることを判定する
     *
     * @param course コース
     * @return true:ストライクのコース
     */
    private static boolean isStrikeCourse(int course) {
        if (2 <= course && course <= 4) {
            return true;
        }
        return false;
    }
}
