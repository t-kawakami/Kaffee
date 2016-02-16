package jp.co.paper.game.controller.converter;

import jp.co.paper.game.controller.io.BattingGetOut;
import jp.co.paper.game.controller.io.BattingPostIn;
import jp.co.paper.game.controller.io.internal.BattingResultOut;
import jp.co.paper.game.controller.io.internal.GameResultOut;
import jp.co.paper.game.domain.Batting;
import jp.co.paper.game.domain.BattingResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by kawakami_note on 2015/08/17.
 */
@Component
public class BattingConverter {

    public Batting convertToBatting(BattingPostIn in) {
        Batting batting = new Batting();
        batting.gameId = in.gameId;
        batting.ballNum = in.ballNum;
        batting.courseX = in.courseX;
        batting.courseY = in.courseY;
        batting.ballKindId = in.ballKindId;
        return batting;
    }

    public BattingGetOut convertToBattingGetOut(BattingResult battingResult) {
        BattingGetOut battingGetOut = new BattingGetOut();
        battingGetOut.gameResult = new GameResultOut();
        battingGetOut.gameResult.inningResultList = new ArrayList<>();
        battingGetOut.gameResult.gameOver = false;

        battingGetOut.battingResult = new BattingResultOut();
        battingGetOut.battingResult.ballCount = 0;
        battingGetOut.battingResult.strikeCount = 0;
        battingGetOut.battingResult.outCount = 0;
        battingGetOut.battingResult.battingResultName = "";

        battingGetOut.finished = true;
        battingGetOut.battingCourseX = null;
        battingGetOut.battingCourseY = null;
        battingGetOut.battingBallKindId = null;
        battingGetOut.battingBallKindName = null;
        battingGetOut.pitchingCourseX = 0;
        battingGetOut.pitchingCourseY = 0;
        battingGetOut.pitchingBallKindId = 0;
        battingGetOut.pitchingBallName = null;
        battingGetOut.battingCourseX = null;
        return battingGetOut;
    }
}
