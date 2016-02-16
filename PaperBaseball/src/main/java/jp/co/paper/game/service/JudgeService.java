package jp.co.paper.game.service;

import jp.co.paper.game.dao.*;
import jp.co.paper.game.domain.*;
import jp.co.paper.game.util.*;
import jp.co.paper.game.util.entity.CourseRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kawakami_note on 2015/08/12.
 */
@Component
@Transactional
public class JudgeService {
    @Autowired
    protected BattingDao battingDao;

    @Autowired
    protected BattingResultDao battingResultDao;

    @Autowired
    protected PitchingDao pitchingDao;

    @Autowired
    protected BallKindDao ballKindDao;

    @Autowired
    protected BattingCountDao battingCountDao;

    @Autowired
    protected BaseCountDao baseCountDao;

    @Autowired
    protected InningResultDao inningResultDao;

    /**
     * 打撃入力を登録する
     * @param batting
     */
    public void insertBatting(Batting batting) {
        battingDao.insert(batting);
    }

    /**
     * ピッチング入力を登録する
     * @param pitching
     */
    public void insertPitching(Pitching pitching) {
        pitchingDao.insert(pitching);
    }

    /**
     * バッティング結果を判定する。
     * @param gameId
     * @param ballNum
     * @return
     */
    public BattingResult judgeBattingResult(String gameId, int ballNum) {
        BattingResult battingResult = battingResultDao.getById(gameId, ballNum);
        // バッティング結果がすでに登録されている場合は結果を返す
        if (battingResult != null) {
            return battingResult;
        }
        // 未登録の結果の場合は打撃入力、投球入力を取得する。
        Batting batting = battingDao.getById(gameId, ballNum);
        Pitching pitching = pitchingDao.getById(gameId, ballNum);
        // どちらかの入力がない場合は結果なしとしてnullを返す
        if (batting == null || pitching == null) {
            return null;
        }
        // 1球前のボールカウント、打撃成績、塁上のランナーを取得する
        BattingCount preBattingCount = BattingCountUtil.initBattingCount(gameId);
        BattingResult preBattingResult = null;
        BaseCount baseCount = new BaseCount(gameId);
        if (ballNum > 1) {
            preBattingCount = battingCountDao.getById(gameId, ballNum - 1);
            preBattingResult = battingResultDao.getById(gameId, ballNum - 1);
            baseCount = baseCountDao.getById(gameId, ballNum - 1);
        }
        // 1球前の結果から、打撃結果判定前の状態を生成する
        BattingCount battingCount = BattingCountUtil.prepareBattingCount(preBattingCount, preBattingResult);
        battingResult = BattingResultUtil.prepareBattingResult(gameId, ballNum);

        // 変化球種からヒットコースを算出する
        List<CourseRule> courseRuleList = createCourseRule(pitching);

        // イニングの記録を取得する
        InningResult inningResult = inningResultDao.getById(gameId, battingCount.inning, battingCount.top);
        if (inningResult == null) {
            inningResult = new InningResult(gameId, battingCount.inning, battingCount.top);
            inningResultDao.insert(inningResult);
        }

        // バッティング結果を判定し、イニングの記録に反映する
        if (BattingResultUtil.isBall(batting, pitching)) {
            battingCount.ballCount++;
            battingResult.ball = true;
            if (battingCount.ballCount == 4) {
                battingResult.battingResultId = FOUR_BALL;
            }
        } else if (BattingResultUtil.isHomeRun(batting, pitching)) {
            inningResult.hitNum++;
            inningResult.score += ScoreUtil.calculateGetScore(baseCount, HOME_RUN);
            battingResult.battingResultId = HOME_RUN;
        } else if (BattingResultUtil.isTwoBaseHit(batting, pitching)) {
            inningResult.hitNum++;
            inningResult.score += ScoreUtil.calculateGetScore(baseCount, TWO_BASE_HIT);
            battingResult.battingResultId = TWO_BASE_HIT;
        } else if (BattingResultUtil.isSingleHitCritical(batting, pitching, courseRuleList)) {
            inningResult.hitNum++;
            inningResult.score += ScoreUtil.calculateGetScore(baseCount, SINGLE_HIT_CRITICAL);
            battingResult.battingResultId = SINGLE_HIT_CRITICAL;
        } else if (BattingResultUtil.isSingleHit(batting, pitching, courseRuleList)) {
            int gettingScore = ScoreUtil.calculateGetScore(baseCount, SINGLE_HIT);
            inningResult.hitNum++;
            inningResult.score += gettingScore;
            battingResult.battingResultId = SINGLE_HIT;
        } else if (BattingResultUtil.isFlyOutCritical(batting, pitching, courseRuleList)) {
            int gettingScore = ScoreUtil.calculateGetScore(baseCount, FLY_OUT_CRITICAL);
            battingCount.outCount++;
            if (battingCount.outCount < 3) {
                inningResult.score += gettingScore;
            }
            battingResult.battingResultId = FLY_OUT_CRITICAL;
        } else if (BattingResultUtil.isFlyOut(batting, pitching, courseRuleList)) {
            battingResult.battingResultId = FLY_OUT;
            battingCount.outCount++;
        } else if (BattingResultUtil.isGroundOutCritical(batting, pitching, courseRuleList)) {
            int gettingScore = ScoreUtil.calculateGetScore(baseCount, GROUND_OUT_CRITICAL);
            battingCount.outCount++;
            if (battingCount.outCount < 3) {
                inningResult.score += gettingScore;
            }
            battingResult.battingResultId = GROUND_OUT_CRITICAL;
        } else if (BattingResultUtil.isGroundOut(batting, pitching, courseRuleList, baseCount)) {
            battingResult.battingResultId = GROUND_OUT;
            battingCount.outCount++;
        } else if (BattingResultUtil.isGetTwo(batting, pitching, courseRuleList, baseCount)) {
            battingResult.battingResultId = GROUND_OUT;
            battingCount.outCount += 2;
        } else if (BattingResultUtil.isFoul(batting, pitching, courseRuleList)) {
            // 2ストライクでない場合のみストライクカウントを増やす
            battingResult.foul = true;
            if (battingCount.strikeCount < 2) {
                battingResult.strike = true;
                battingCount.strikeCount++;
            }
        } else {
            // その他は見逃しのストライクまたは空振りであり、三振判定を行う
            battingCount.strikeCount++;
            battingResult.strike = true;
            if (battingCount.strikeCount >= 3) {
                battingResult.battingResultId = STRIKE_OUT;
                battingCount.outCount++;
            }
        }
        battingCountDao.insert(battingCount);
        battingResultDao.insert(battingResult);
        baseCountDao.insert(BaseCountUtil.updateBaseCount(gameId, baseCount, battingResult, battingCount));
        inningResultDao.update(inningResult);
        return battingResult;
    }

    /**
     * 投球内容からヒットコースルールを生成する
     * @param pitching 投球内容
     * @return ヒットコースルール
     */
    private List<CourseRule> createCourseRule(Pitching pitching) {
        List<BallKind> ballKindList = ballKindDao.getByBallKindId(pitching.ballKindId);
        return CourseRuleUtil.createCourseRule(ballKindList, pitching);
    }

    /**
     * 打撃結果を返す
     * @param batting
     * @param pitching
     * @param courseRuleList
     * @param baseCount
     * @return
     */
    private int judgeBattingResult(Batting batting, Pitching pitching, List<CourseRule> courseRuleList, BaseCount baseCount) {
        if (BattingResultUtil.isBall(batting, pitching)) {
            return NO_RESULT_BALL;
        } else if (BattingResultUtil.isHomeRun(batting, pitching)) {
            return HOME_RUN;
        } else if (BattingResultUtil.isTwoBaseHit(batting, pitching)) {
            return TWO_BASE_HIT;
        } else if (BattingResultUtil.isSingleHitCritical(batting, pitching, courseRuleList)) {
            return SINGLE_HIT_CRITICAL;
        } else if (BattingResultUtil.isSingleHit(batting, pitching, courseRuleList)) {
            return SINGLE_HIT;
        } else if (BattingResultUtil.isFlyOutCritical(batting, pitching, courseRuleList)) {
            return FLY_OUT_CRITICAL;
        } else if (BattingResultUtil.isFlyOut(batting, pitching, courseRuleList)) {
            return FLY_OUT;
        } else if (BattingResultUtil.isGroundOutCritical(batting, pitching, courseRuleList)) {
            return GROUND_OUT_CRITICAL;
        } else if (BattingResultUtil.isGroundOut(batting, pitching, courseRuleList, baseCount)) {
            return GROUND_OUT;
        } else if (BattingResultUtil.isGetTwo(batting, pitching, courseRuleList, baseCount)) {
            return GET_TWO;
        } else if (BattingResultUtil.isFoul(batting, pitching, courseRuleList)) {
            return NO_RESULT_FOUL;
        } else {
            return NO_RESULT_STRIKE;
        }
    }
}
