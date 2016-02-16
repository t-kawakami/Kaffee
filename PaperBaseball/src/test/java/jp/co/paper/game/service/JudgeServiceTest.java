package jp.co.paper.game.service;

import jp.co.paper.game.Application;
import jp.co.paper.game.constants.BattingResultConst;
import jp.co.paper.game.dao.*;
import jp.co.paper.game.domain.*;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by kawakami_note on 2015/08/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JudgeServiceTest {

    @Value("${ball.kind.id.straight}")
    protected int STRAIGHT;

    @Value("${ball.kind.id.slider}")
    protected int SLIDER;

    @Value("${ball.kind.id.curve}")
    protected int CURVE;

    @Value("${ball.kind.id.split}")
    protected int SPLIT;

    @Value("${ball.kind.id.sinker}")
    protected int SINKER;

    @Value("${ball.kind.id.shoot}")
    protected int SHOOT;

    @Autowired
    protected JudgeService judgeService;

    @Autowired
    protected SqlTemplate sqlTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected BaseCountDao baseCountDao;

    @Autowired
    protected BattingCountDao battingCountDao;

    @Autowired
    protected BattingDao battingDao;

    @Autowired
    protected BattingResultDao battingResultDao;

    @Autowired
    protected InningResultDao inningResultDao;

    @Autowired
    protected PitchingDao pitchingDao;

    @Before
    public void setUp() {
        sqlTemplate.update("sql/BaseCountDao/deleteByGameId.sql", "testgame");
        sqlTemplate.update("sql/BattingCountDao/deleteByGameId.sql", "testgame");
        sqlTemplate.update("sql/BattingDao/deleteByGameId.sql", "testgame");
        sqlTemplate.update("sql/BattingResultDao/deleteByGameId.sql", "testgame");
        sqlTemplate.update("sql/InningResultDao/deleteByGameId.sql", "testgame");
        sqlTemplate.update("sql/PitchingDao/deleteByGameId.sql", "testgame");
    }

    /**
     * 初球ストレートホームランに対しての判定確認
     */
    @Test
    public void testJudgeBattingResult_FirstPitch_straight_homeRun() {
        // 準備 初球真ん中ストレートを投げ、ストレート狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, STRAIGHT, 3, 3));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 3, 3));

        // 実行
        judgeService.judgeBattingResult("testgame", 1);

        // 検証 ランナーなし １回表カウントなし 結果ホームラン
        assertBaseCount(baseCountDao.getById("testgame" ,1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.HOME_RUN));
        // 1回表1点、ヒット1
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 1, 1));
    }

    /**
     * 初球ストレート二塁打の判定確認（ストライク）
     */
    @Test
    public void testJudgeBattingResult_FirstPitch_straight_twoBase_strike() {
        // 準備 初球真ん中ストレートを投げ、スライダー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SLIDER, 3, 3));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 3, 3));

        // 実行
        judgeService.judgeBattingResult("testgame", 1);

        // 検証 ランナー二塁 １回表カウントなし 結果二塁打
        assertBaseCount(baseCountDao.getById("testgame" ,1), createBaseCount(1, false, true, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.TWO_BASE_HIT));
        // 1回表0点、ヒット1
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
    }

    /**
     * 一塁打(二塁からの得点)確認
     * 初球に二塁打を打つ(ストライク)
     * 二球目にシングルヒット(二塁からの得点を許可)を打つ
     */
    @Test
    public void testJudgeBattingResult_SecondPitch_straight_single_getScore_from_second_base_strike() {
        // 準備 初球真ん中ストレートを投げ、カーブ狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, CURVE, 3, 3));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 3, 3));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 2球目 外角高めストレートを投げ、ストレート狙い・ヒットゾーンで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, STRAIGHT, 4, 4));

        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 検証 ランナー一塁 １回表カウントなし 結果一塁打(二塁からの生還あり)
        assertBaseCount(baseCountDao.getById("testgame" ,2), createBaseCount(2, true, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.SINGLE_HIT_CRITICAL));
        // 1回表1点、ヒット2
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 1, 2));
    }

    /**
     * 一塁打確認
     * 初球に二塁打を打つ(ボール)
     * 二球目にシングルヒットを打つ
     */
    @Test
    public void testJudgeBattingResult_SecondPitch_straight_single_strike() {
        // 準備 初球ボール球シンカーを投げ、シンカー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SINKER, 1, 1));
        judgeService.insertPitching(createPitching(1, SINKER, 1, 1));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 2球目 外角高めストレートを投げ、カーブ狙い・ヒットゾーンで打つ
        judgeService.insertBatting(createBatting(2, CURVE, 3, 4));
        judgeService.insertPitching(createPitching(2, STRAIGHT, 4, 4));

        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 検証
        // ランナー一塁三塁
        assertBaseCount(baseCountDao.getById("testgame" ,2), createBaseCount(2, true, false, true));
        // １回表カウントなし
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 0));
        // 結果一塁打
        // 1回表0点、ヒット2
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 2));
    }

    /**
     * ファール確認
     * 初球ファール(ストライク、狙い一致、フライ)
     * 二球目ファール(ストライク、狙い一致、ゴロ)
     * 三球目ファール(ボール、狙い不一致、ヒットコース)
     */
    @Test
    public void testJudgeBattingResult_Three_foul() {
        // 準備 初球ストライク球シュートを投げ、シュート狙い・フライで打つ
        judgeService.insertBatting(createBatting(1, SHOOT, 2, 2));
        judgeService.insertPitching(createPitching(1, SHOOT, 2, 4));
        // 実行 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);

        // 準備 2球目 ストライク球カーブを投げ、カーブ狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(2, CURVE, 4, 2));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 準備 3球目 ボール球カーブを投げ、ストレート狙い・ヒットで打つ
        judgeService.insertBatting(createBatting(3, STRAIGHT, 2, 3));
        judgeService.insertPitching(createPitching(3, CURVE, 1, 3));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);

        // 検証 1球目  ランナーなし １回表ノーボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 2球目  ランナーなし １回表ノーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 2, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 3球目  ランナーなし １回表ノーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 2, 0));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, true, BattingResultConst.NO_RESULT));
        // 1回表 0点 ノーヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 0));
    }

    /**
     * 空振り三振確認
     * 初球空振り(ボール)
     * 二球目ファール(ストライク、狙い一致、ゴロ)
     * 三球目空振り（ストライク）
     */
    @Test
    public void testJudgeBattingResult_swing_out() {
        // 準備 初球ボール球 空振り
        judgeService.insertBatting(createBatting(1, STRAIGHT, 4, 4));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 2, 1));
        // 実行 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);

        // 準備 2球目 ストライク球カーブを投げ、カーブ狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(2, CURVE, 4, 2));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 準備 3球目 ボール球 空振り
        judgeService.insertBatting(createBatting(3, SHOOT, 4, 4));
        judgeService.insertPitching(createPitching(3, SHOOT, 1, 4));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);

        // 検証 1球目  ランナーなし １回表ノーボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, true, false, BattingResultConst.NO_RESULT));
        // 検証 2球目  ランナーなし １回表ノーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 2, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 3球目  ランナーなし １回表ノーボール ノーストライク ワンアウト 結果 三振
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 3, 1));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, true, false, BattingResultConst.STRIKE_OUT));
        // 1回表 0点 ノーヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 0));
    }

    /**
     * 見逃し三振確認
     * 初球空振り(ボール)
     * 二球目ファール(ストライク、狙い一致、ゴロ)
     * 三球目見逃し（ストライク）
     */
    @Test
    public void testJudgeBattingResult_strike_out() {
        // 準備 初球ボール球 空振り
        judgeService.insertBatting(createBatting(1, STRAIGHT, 4, 4));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 2, 1));
        // 実行 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);

        // 準備 2球目 ストライク球カーブを投げ、カーブ狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(2, CURVE, 4, 2));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 準備 3球目 ストライク 見逃し
        judgeService.insertBatting(createBatting(3, null, null, null));
        judgeService.insertPitching(createPitching(3, SPLIT, 3, 3));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);

        // 検証 1球目  ランナーなし １回表ノーボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, true, false, BattingResultConst.NO_RESULT));
        // 検証 2球目  ランナーなし １回表ノーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 2, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 3球目  ランナーなし １回表ノーボール ノーストライク ワンアウト 結果 三振
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 3, 1));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, true, false, BattingResultConst.STRIKE_OUT));
        // 1回表 0点 ノーヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 0));
    }

    /**
     * 見逃し、四球確認
     * 初球見逃し(ボール)
     * 二球目見逃し(ストライク)
     * 三球目見逃し（ボール）
     * 四球目見逃し（ボール）
     * 五球目見逃し（ストライク）
     * 六球目ファール
     * 七球目見逃し(ボール、四球)
     */
    @Test
    public void testJudgeBattingResult_four_ball() {
        // 準備 初球ボール球 見逃し
        judgeService.insertBatting(createBatting(1, null, null, null));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 2, 1));
        // 実行 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);

        // 準備 2球目 ストライク球カーブを投げ、見逃し
        judgeService.insertBatting(createBatting(2, null, null, null));
        judgeService.insertPitching(createPitching(2, CURVE, 4, 2));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);

        // 準備 3球目 ボール球 見逃し
        judgeService.insertBatting(createBatting(3, null, null, null));
        judgeService.insertPitching(createPitching(3, SHOOT, 1, 4));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);

        // 準備 4球目 ボール球 見逃し
        judgeService.insertBatting(createBatting(4, null, null, null));
        judgeService.insertPitching(createPitching(4, SHOOT, 1, 4));
        // 実行 4球目の判定
        judgeService.judgeBattingResult("testgame", 4);

        // 準備 5球目 ストライク 見逃し
        judgeService.insertBatting(createBatting(5, null, null, null));
        judgeService.insertPitching(createPitching(5, SHOOT, 2, 4));
        // 実行 5球目の判定
        judgeService.judgeBattingResult("testgame", 5);

        // 準備 6球目 ストライク球カーブを投げ、カーブ狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(6, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(6, CURVE, 4, 2));
        // 実行 6球目の判定
        judgeService.judgeBattingResult("testgame", 6);

        // 準備 7球目 ボール球 見逃し
        judgeService.insertBatting(createBatting(7, null, null, null));
        judgeService.insertPitching(createPitching(7, SHOOT, 1, 4));
        // 実行 7球目の判定
        judgeService.judgeBattingResult("testgame", 7);

        // 検証 1球目  ランナーなし １回表ワンボールノーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 1, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, true, false, false, BattingResultConst.NO_RESULT));
        // 検証 2球目  ランナーなし １回表ワンボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 1, 1, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, true, false, BattingResultConst.NO_RESULT));
        // 検証 3球目  ランナーなし １回表ツーボール ワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 2, 1, 0));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, true, false, false, BattingResultConst.NO_RESULT));
        // 検証 4球目  ランナーなし １回表スリーボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(4), createBaseCount(4, false, false, false));
        assertBattingCount(getBattingCountByBallNum(4), createBattingCount(4, 1, true, 3, 1, 0));
        assertBattingResult(getBattingResultByBallNum(4), createBattingResult(4, true, false, false, BattingResultConst.NO_RESULT));
        // 検証 5球目  ランナーなし １回表スリーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(5), createBaseCount(5, false, false, false));
        assertBattingCount(getBattingCountByBallNum(5), createBattingCount(5, 1, true, 3, 2, 0));
        assertBattingResult(getBattingResultByBallNum(5), createBattingResult(5, false, true, false, BattingResultConst.NO_RESULT));
        // 検証 6球目  ランナーなし １回表スリーボールツーストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(6), createBaseCount(6, false, false, false));
        assertBattingCount(getBattingCountByBallNum(6), createBattingCount(6, 1, true, 3, 2, 0));
        assertBattingResult(getBattingResultByBallNum(6), createBattingResult(6, false, false, true, BattingResultConst.NO_RESULT));
        // 検証 7球目  ランナー一塁 １回表フォアボールツーストライク 結果四球
        assertBaseCount(getBaseCountByBallNum(7), createBaseCount(7, true, false, false));
        assertBattingCount(getBattingCountByBallNum(7), createBattingCount(7, 1, true, 4, 2, 0));
        assertBattingResult(getBattingResultByBallNum(7), createBattingResult(7, true, false, false, BattingResultConst.FOUR_BALL));
        // 1回表 0点 ノーヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 0));
    }

    /**
     * 二塁打→進塁打→フライアウト(犠牲フライ)
     * での得点確認
     */
    @Test
    public void testJudgeBattingResult_TwoBaseHit_GroundOutCritical_FlyOutCritical() {
        // 準備 初球ボール球シンカーを投げ、シンカー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SINKER, 1, 1));
        judgeService.insertPitching(createPitching(1, SINKER, 1, 1));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 2球目 ストライク カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, CURVE, 3, 3));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 3球目 ストライク ストレートを投げる。スプリット狙い・フライで打つ
        judgeService.insertBatting(createBatting(3, SPLIT, 3, 1));
        judgeService.insertPitching(createPitching(3, STRAIGHT, 3, 3));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);
        // 検証 1回表 1点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 1, 1));

        // 検証 1球目  ランナー二塁 １回表ノーボールノーストライク 結果二塁打
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, true, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.TWO_BASE_HIT));
        // 検証 2球目  ランナー三塁 １回表ノーボールノーストライクワンアウト 結果進塁打
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, true));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 1));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.GROUND_OUT_CRITICAL));
        // 検証 3球目  ランナーなし １回表ノーボールノーストライクツーアウト 結果犠牲フライ
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.FLY_OUT_CRITICAL));
    }

    /**
     * 二塁打→進塁打→フライアウト
     */
    @Test
    public void testJudgeBattingResult_TwoBaseHit_GroundOutCritical_FlyOut() {
        // 準備 初球ボール球シンカーを投げ、シンカー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SINKER, 1, 1));
        judgeService.insertPitching(createPitching(1, SINKER, 1, 1));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 2球目 ストライク カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, CURVE, 3, 3));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 3球目 ボール球 ストレートを投げる。スプリット狙い・フライで打つ
        judgeService.insertBatting(createBatting(3, SPLIT, 1, 3));
        judgeService.insertPitching(createPitching(3, STRAIGHT, 1, 5));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));

        // 検証 1球目  ランナー二塁 １回表ノーボールノーストライク 結果二塁打
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, true, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.TWO_BASE_HIT));
        // 検証 2球目  ランナー三塁 １回表ノーボールノーストライクワンアウト 結果進塁打
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, true));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 1));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.GROUND_OUT_CRITICAL));
        // 検証 3球目  ランナー三塁 １回表ノーボールノーストライクツーアウト 結果フライアウト
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, true));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.FLY_OUT));
    }

    /**
     * 二塁打→進塁打→進塁打得点
     */
    @Test
    public void testJudgeBattingResult_TwoBaseHit_GroundOutCritical_GroundOutCritical() {
        // 準備 初球ボール球シンカーを投げ、シンカー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SINKER, 1, 1));
        judgeService.insertPitching(createPitching(1, SINKER, 1, 1));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 2球目 ストライク カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, CURVE, 3, 3));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 3球目 ストライク カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(3, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(3, CURVE, 3, 3));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);
        // 検証 1回表 1点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 1, 1));

        // 検証 1球目  ランナー二塁 １回表ノーボールノーストライク 結果二塁打
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, true, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.TWO_BASE_HIT));
        // 検証 2球目  ランナー三塁 １回表ノーボールノーストライクワンアウト 結果進塁打
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, true));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 1));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.GROUND_OUT_CRITICAL));
        // 検証 3球目  ランナー三塁 １回表ノーボールノーストライクツーアウト 結果進塁打
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.GROUND_OUT_CRITICAL));
    }

    /**
     * 二塁打→進塁打→ゴロアウト
     */
    @Test
    public void testJudgeBattingResult_TwoBaseHit_GroundOutCritical_GroundOut() {
        // 準備 初球ボール球シンカーを投げ、シンカー狙い・真芯で打つ
        judgeService.insertBatting(createBatting(1, SINKER, 1, 1));
        judgeService.insertPitching(createPitching(1, SINKER, 1, 1));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 2球目 ストライク カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, CURVE, 3, 3));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 3球目 ボール球 カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(3, STRAIGHT, 3, 2));
        judgeService.insertPitching(createPitching(3, CURVE, 3, 1));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));

        // 検証 1球目  ランナー二塁 １回表ノーボールノーストライク 結果二塁打
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, true, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.TWO_BASE_HIT));
        // 検証 2球目  ランナー三塁 １回表ノーボールノーストライクワンアウト 結果進塁打
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, true));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 1));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.GROUND_OUT_CRITICAL));
        // 検証 3球目  ランナー三塁 １回表ノーボールノーストライクツーアウト 結果ゴロアウト
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, true));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.GROUND_OUT));
    }

    /**
     * 一塁打→一塁打→ゴロアウト（ゲッツー）
     */
    @Test
    public void testJudgeBattingResult_SingleHit_SingleHit_GroundOut() {
        // 準備 ストライク ストレートを投げ、ストレート狙い・ヒットゾーンで打つ
        judgeService.insertBatting(createBatting(1, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 4, 4));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 検証 1回表 0点 1ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 1));
        // 2球目 ストライク ストレートを投げ、ストレート狙い・ヒットゾーンで打つ
        judgeService.insertBatting(createBatting(2, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(2, STRAIGHT, 4, 4));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 検証 1回表 0点 2ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 2));
        // 3球目 ボール球 カーブを投げる。ストレート狙い・ゴロで打つ
        judgeService.insertBatting(createBatting(3, STRAIGHT, 3, 2));
        judgeService.insertPitching(createPitching(3, CURVE, 3, 1));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);
        // 検証 1回表 0点 2ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 2));

        // 検証 1球目  ランナー一塁 １回表ノーボールノーストライク 結果一塁打
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, true, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.SINGLE_HIT_CRITICAL));
        // 検証 2球目  ランナー一塁二塁 １回表ノーボールノーストライク 結果一塁打
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, true, true, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 0));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.SINGLE_HIT_CRITICAL));
        // 検証 3球目  ランナー一塁 １回表ノーボールノーストライクツーアウト 結果ゴロアウト（ゲッツー）
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, true, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.GROUND_OUT));
    }

    /**
     * フライアウト→フライアウト→フライアウト
     */
    @Test
    public void testJudgeBattingResult_FlyOut_change() {
        // 1球目 ボール球 ストレートを投げる。スプリット狙い・フライで打つ
        judgeService.insertBatting(createBatting(1, SPLIT, 1, 3));
        judgeService.insertPitching(createPitching(1, STRAIGHT, 1, 5));
        // 1球目の判定
        judgeService.judgeBattingResult("testgame", 1);
        // 2球目 ボール球 ストレートを投げる。スプリット狙い・フライで打つ
        judgeService.insertBatting(createBatting(2, SPLIT, 1, 3));
        judgeService.insertPitching(createPitching(2, STRAIGHT, 1, 5));
        // 実行 2球目の判定
        judgeService.judgeBattingResult("testgame", 2);
        // 3球目 ボール球 ストレートを投げる。スプリット狙い・フライで打つ
        judgeService.insertBatting(createBatting(3, SPLIT, 1, 3));
        judgeService.insertPitching(createPitching(3, STRAIGHT, 1, 5));
        // 実行 3球目の判定
        judgeService.judgeBattingResult("testgame", 3);

        // 検証 1球目  ランナーなし １回表ノーボールノーストライクワンアウト 結果フライアウト
        assertBaseCount(getBaseCountByBallNum(1), createBaseCount(1, false, false, false));
        assertBattingCount(getBattingCountByBallNum(1), createBattingCount(1, 1, true, 0, 0, 1));
        assertBattingResult(getBattingResultByBallNum(1), createBattingResult(1, false, false, false, BattingResultConst.FLY_OUT));
        // 検証 2球目  ランナーなし １回表ノーボールノーストライクツーアウト 結果フライアウト
        assertBaseCount(getBaseCountByBallNum(2), createBaseCount(2, false, false, false));
        assertBattingCount(getBattingCountByBallNum(2), createBattingCount(2, 1, true, 0, 0, 2));
        assertBattingResult(getBattingResultByBallNum(2), createBattingResult(2, false, false, false, BattingResultConst.FLY_OUT));
        // 検証 3球目  ランナーなし １回表ノーボールノーストライクスリーアウト 結果フライアウト
        assertBaseCount(getBaseCountByBallNum(3), createBaseCount(3, false, false, false));
        assertBattingCount(getBattingCountByBallNum(3), createBattingCount(3, 1, true, 0, 0, 3));
        assertBattingResult(getBattingResultByBallNum(3), createBattingResult(3, false, false, false, BattingResultConst.FLY_OUT));
    }

    /**
     * スリーアウト後の投球が1回裏に変化しており、カウントがリセットされていることの確認
     */
    @Test
    public void testJudgeBattingResult_ThreeOut_change() {
        // 準備 3球でスリーアウト、1回表終了の状況を作る
        threeOutWithThreeBall();

        // 準備 4球目 ストライク球カーブを投げ、カーブ狙い・ゴロで打つ(ファール)
        judgeService.insertBatting(createBatting(4, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(4, CURVE, 4, 2));
        // 実行 4球目の判定
        judgeService.judgeBattingResult("testgame", 4);

        // 検証 4球目  ランナーなし １回裏ノーボールワンストライクノーアウト 結果なし
        assertBaseCount(getBaseCountByBallNum(4), createBaseCount(4, false, false, false));
        assertBattingCount(getBattingCountByBallNum(4), createBattingCount(4, 1, false, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(4), createBattingResult(4, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 1回裏 0点 0ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, false), createInningResult(1, false, 0, 0));
    }

    /**
     * スリーアウト後の投球が1回裏に変化しており、カウントがリセットされていることの確認(三振)
     */
    @Test
    public void testJudgeBattingResult_ThreeOut_change_by_strike_out() {
        // 準備 2球でツーアウトの状況を作る
        twoOutWithTwoBall();

        // 空振り→見逃しストライク→ボール→空振り三振(チェンジ) → ファール
        swingStrike(3);
        missedStrike(4);
        missedBall(5);
        swingStrike(6);
        foul(7);

        // 検証 6球目  ランナーなし １回表ワンボールスリーストライクスリーアウト 結果三振
        assertBaseCount(getBaseCountByBallNum(6), createBaseCount(6, false, false, false));
        assertBattingCount(getBattingCountByBallNum(6), createBattingCount(6, 1, true, 1, 3, 3));
        assertBattingResult(getBattingResultByBallNum(6), createBattingResult(6, false, true, false, BattingResultConst.STRIKE_OUT));
        // 検証 7球目 ランナーなし １回裏ノーボールワンストライクノーアウト 結果なし
        assertBaseCount(getBaseCountByBallNum(7), createBaseCount(7, false, false, false));
        assertBattingCount(getBattingCountByBallNum(7), createBattingCount(7, 1, false, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(7), createBattingResult(7, false, true, true, BattingResultConst.NO_RESULT));
        // 検証 1回表 0点 0ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, true), createInningResult(1, true, 0, 0));
        // 検証 1回裏 0点 0ヒット
        assertInningResult(inningResultDao.getById("testgame", 1, false), createInningResult(1, false, 0, 0));
    }

    /**
     * 一塁打によりカウントをリセットすることの確認
     */
    @Test
    public void testJudgeBattingResult_count_reset_by_hit() {
        // 空振り→ボール→ボール→見逃し→シングルヒット  → 空振り
        swingStrike(1);
        missedBall(2);
        missedBall(3);
        missedStrike(4);
        singleHit(5);
        swingStrike(6);

        // 検証 5球目  ランナー一塁 １回表ツーボールツーストライク 結果シングルヒット
        assertBaseCount(getBaseCountByBallNum(5), createBaseCount(5, true, false, false));
        assertBattingCount(getBattingCountByBallNum(5), createBattingCount(5, 1, true, 2, 2, 0));
        assertBattingResult(getBattingResultByBallNum(5), createBattingResult(5, false, false, false, BattingResultConst.SINGLE_HIT));
        // 検証 6球目  ランナー一塁 １回表ノーボールワンストライク 結果なし
        assertBaseCount(getBaseCountByBallNum(6), createBaseCount(6, true, false, false));
        assertBattingCount(getBattingCountByBallNum(6), createBattingCount(6, 1, true, 0, 1, 0));
        assertBattingResult(getBattingResultByBallNum(6), createBattingResult(6, false, true, false, BattingResultConst.NO_RESULT));
    }

    private void singleHitCritical(int ballNum) {
        // シングルヒット 二塁からの得点を認める
        judgeService.insertBatting(createBatting(ballNum, STRAIGHT, 3, 4));
        judgeService.insertPitching(createPitching(ballNum, STRAIGHT, 4, 4));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void singleHit(int ballNum) {
        // シングルヒット
        judgeService.insertBatting(createBatting(ballNum, CURVE, 3, 4));
        judgeService.insertPitching(createPitching(ballNum, STRAIGHT, 4, 4));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void swingStrike(int ballNum) {
        // ボール球 空振り
        judgeService.insertBatting(createBatting(ballNum, STRAIGHT, 4, 4));
        judgeService.insertPitching(createPitching(ballNum, STRAIGHT, 2, 1));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void missedStrike(int ballNum) {
        // ストライク 見逃し
        judgeService.insertBatting(createBatting(ballNum, null, null, null));
        judgeService.insertPitching(createPitching(ballNum, SPLIT, 3, 3));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void missedBall(int ballNum) {
        // ボール球 見逃し
        judgeService.insertBatting(createBatting(ballNum, null, null, null));
        judgeService.insertPitching(createPitching(ballNum, SHOOT, 1, 4));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void foul(int ballNum) {
        // ファールを打つ
        judgeService.insertBatting(createBatting(ballNum, CURVE, 4, 3));
        judgeService.insertPitching(createPitching(ballNum, CURVE, 4, 2));
        judgeService.judgeBattingResult("testgame", ballNum);
    }

    private void twoOutWithTwoBall() {
        // ツーアウトの状態を再現する
        String[] queries = {
                "INSERT INTO BASE_COUNT ( GAME_ID, BALL_NUM, FIRST_BASE, SECOND_BASE, THIRD_BASE ) VALUES \n" +
                        "('testgame',1,0,0,0),\n" +
                        "('testgame',2,0,0,0);",
                "INSERT INTO BATTING ( GAME_ID, BALL_NUM, COURSE_X, COURSE_Y, BALL_KIND_ID ) VALUES \n" +
                        "('testgame',1,1,3,4),\n" +
                        "('testgame',2,1,3,4);",
                "INSERT INTO BATTING_COUNT ( GAME_ID, BALL_NUM, INNING, TOP, STRIKE_COUNT, BALL_COUNT, OUT_COUNT ) VALUES \n" +
                        "('testgame',1,1,1,0,0,1),\n" +
                        "('testgame',2,1,1,0,0,2);",
                "INSERT INTO BATTING_RESULT ( GAME_ID, BALL_NUM, STRIKE, BALL, FOUL, BATTING_RESULT_ID ) VALUES \n" +
                        "('testgame',1,0,0,0,7),\n" +
                        "('testgame',2,0,0,0,7);",
                "INSERT INTO INNING_RESULT ( GAME_ID, INNING, TOP, SCORE, HIT_NUM ) VALUES \n" +
                        "('testgame',1,1,0,0);",
                "INSERT INTO PITCHING ( GAME_ID, BALL_NUM, COURSE_X, COURSE_Y, BALL_KIND_ID ) VALUES \n" +
                        "('testgame',1,1,5,1),\n" +
                        "('testgame',2,1,5,1);"
        };
        Arrays.asList(queries).forEach(query -> jdbcTemplate.update(query));
    }

    private void threeOutWithThreeBall() {
        // スリーアウトの状態を再現する
        String[] queries = {
                "INSERT INTO BASE_COUNT ( GAME_ID, BALL_NUM, FIRST_BASE, SECOND_BASE, THIRD_BASE ) VALUES \n" +
                        "('testgame',1,0,0,0),\n" +
                        "('testgame',2,0,0,0),\n" +
                        "('testgame',3,0,0,0);",
                "INSERT INTO BATTING ( GAME_ID, BALL_NUM, COURSE_X, COURSE_Y, BALL_KIND_ID ) VALUES \n" +
                        "('testgame',1,1,3,4),\n" +
                        "('testgame',2,1,3,4),\n" +
                        "('testgame',3,1,3,4);",
                "INSERT INTO BATTING_COUNT ( GAME_ID, BALL_NUM, INNING, TOP, STRIKE_COUNT, BALL_COUNT, OUT_COUNT ) VALUES \n" +
                        "('testgame',1,1,1,0,0,1),\n" +
                        "('testgame',2,1,1,0,0,2),\n" +
                        "('testgame',3,1,1,0,0,3);",
                "INSERT INTO BATTING_RESULT ( GAME_ID, BALL_NUM, STRIKE, BALL, FOUL, BATTING_RESULT_ID ) VALUES \n" +
                        "('testgame',1,0,0,0,7),\n" +
                        "('testgame',2,0,0,0,7),\n" +
                        "('testgame',3,0,0,0,7);",
                "INSERT INTO INNING_RESULT ( GAME_ID, INNING, TOP, SCORE, HIT_NUM ) VALUES \n" +
                        "('testgame',1,1,0,0);",
                "INSERT INTO PITCHING ( GAME_ID, BALL_NUM, COURSE_X, COURSE_Y, BALL_KIND_ID ) VALUES \n" +
                        "('testgame',1,1,5,1),\n" +
                        "('testgame',2,1,5,1),\n" +
                        "('testgame',3,1,5,1);"
        };
        Arrays.asList(queries).forEach(query -> jdbcTemplate.update(query));
    }

    private BaseCount getBaseCountByBallNum(int ballNum) {
        return baseCountDao.getById("testgame", ballNum);
    }

    private BattingCount getBattingCountByBallNum(int ballNum) {
        return battingCountDao.getById("testgame", ballNum);
    }

    private BattingResult getBattingResultByBallNum(int ballNum) {
        return battingResultDao.getById("testgame", ballNum);
    }

    private void assertBaseCount(BaseCount actual, BaseCount expected) {
        assertThat(actual.firstBase, is(expected.firstBase));
        assertThat(actual.secondBase, is(expected.secondBase));
        assertThat(actual.thirdBase, is(expected.thirdBase));
    }

    private void assertBattingCount(BattingCount actual, BattingCount expected) {
        assertThat(actual.inning, is(expected.inning));
        assertThat(actual.ballCount, is(expected.ballCount));
        assertThat(actual.strikeCount, is(expected.strikeCount));
        assertThat(actual.outCount, is(expected.outCount));
        assertThat(actual.top, is(expected.top));
    }

    private void assertBattingResult(BattingResult actual, BattingResult expected) {
        assertThat(actual.strike, is(expected.strike));
        assertThat(actual.ball, is(expected.ball));
        assertThat(actual.foul, is(expected.foul));
        assertThat(actual.battingResultId, is(expected.battingResultId));
    }

    private void assertInningResult(InningResult actual, InningResult expected) {
        assertThat(actual.inning, is(expected.inning));
        assertThat(actual.top, is(expected.top));
        assertThat(actual.score, is(expected.score));
        assertThat(actual.hitNum, is(expected.hitNum));
    }

    private BaseCount createBaseCount(int ballNum, boolean firstBase, boolean secondBase, boolean thirdBase) {
        return createBaseCount("testgame", ballNum, firstBase, secondBase, thirdBase);
    }

    private BaseCount createBaseCount(String gameId, int ballNum, boolean firstBase, boolean secondBase, boolean thirdBase) {
        BaseCount baseCount = new BaseCount();
        baseCount.gameId = gameId;
        baseCount.ballNum = ballNum;
        baseCount.firstBase = firstBase;
        baseCount.secondBase = secondBase;
        baseCount.thirdBase = thirdBase;
        return baseCount;
    }

    private BattingCount createBattingCount(int ballNum, int inning, boolean top, int ballCount, int strikeCount, int outCount) {
        return createBattingCount("testgame", ballNum, inning, top, ballCount, strikeCount, outCount);
    }

    private BattingCount createBattingCount(String gameId, int ballNum, int inning, boolean top, int ballCount, int strikeCount, int outCount) {
        BattingCount battingCount = new BattingCount();
        battingCount.gameId = gameId;
        battingCount.ballNum = ballNum;
        battingCount.inning = inning;
        battingCount.top = top;
        battingCount.ballCount = ballCount;
        battingCount.strikeCount = strikeCount;
        battingCount.outCount = outCount;
        return battingCount;
    }

    private BattingResult createBattingResult(int ballNum, boolean ball, boolean strike, boolean foul, int battingResultId) {
        return createBattingResult("testgame", ballNum, ball, strike, foul, battingResultId);
    }

    private BattingResult createBattingResult(String gameId, int ballNum, boolean ball, boolean strike, boolean foul, int battingResultId) {
        BattingResult battingResult = new BattingResult();
        battingResult.gameId = gameId;
        battingResult.ballNum = ballNum;
        battingResult.ball = ball;
        battingResult.strike = strike;
        battingResult.foul = foul;
        battingResult.battingResultId = battingResultId;
        return battingResult;
    }

    private InningResult createInningResult(int inning, boolean top, int score, int hitNum) {
        return createInningResult("testname", inning, top, score, hitNum);
    }

    private InningResult createInningResult(String gameId, int inning, boolean top, int score, int hitNum) {
        InningResult inningResult = new InningResult();
        inningResult.gameId = gameId;
        inningResult.inning = inning;
        inningResult.top = top;
        inningResult.score = score;
        inningResult.hitNum = hitNum;
        return inningResult;
    }

    private Batting createBatting(int ballNum, Integer ballKindId, Integer courseX, Integer courseY) {
        return createBatting("testgame", ballNum, ballKindId, courseX, courseY);
    }

    private Batting createBatting(String gameId, int ballNum, Integer ballKindId, Integer courseX, Integer courseY) {
        Batting batting = new Batting();
        batting.gameId = gameId;
        batting.ballNum = ballNum;
        batting.ballKindId = ballKindId;
        batting.courseX = courseX;
        batting.courseY = courseY;
        return batting;
    }

    private Pitching createPitching(int ballNum, int ballKindId, int courseX, int courseY) {
        return createPitching("testgame", ballNum, ballKindId, courseX, courseY);
    }

    private Pitching createPitching(String gameId, int ballNum, int ballKindId, int courseX, int courseY) {
        Pitching pitching = new Pitching();
        pitching.gameId = gameId;
        pitching.ballNum = ballNum;
        pitching.ballKindId = ballKindId;
        pitching.courseX = courseX;
        pitching.courseY = courseY;
        return pitching;
    }
}
