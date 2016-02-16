package jp.co.paper.game.controller.converter;

import jp.co.paper.game.controller.io.PitchingGetOut;
import jp.co.paper.game.controller.io.PitchingPostIn;
import jp.co.paper.game.controller.io.internal.BattingResultOut;
import jp.co.paper.game.controller.io.internal.GameResultOut;
import jp.co.paper.game.domain.BattingResult;
import jp.co.paper.game.domain.Pitching;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by kawakami_note on 2015/08/17.
 */
@Component
public class PitchingConverter {

    public Pitching convertToPitching(PitchingPostIn in) {
        Pitching pitching = new Pitching();
        pitching.gameId = in.gameId;
        pitching.ballNum = in.ballNum;
        pitching.courseX = in.courseX;
        pitching.courseY = in.courseY;
        pitching.ballKindId = in.ballKindId;
        return pitching;
    }

    public PitchingGetOut convertToPitchingGetOut(BattingResult battingResult) {
        PitchingGetOut pitchingGetOut = new PitchingGetOut();

        pitchingGetOut.gameResult = new GameResultOut();
        pitchingGetOut.gameResult.inningResultList = new ArrayList<>();
        pitchingGetOut.gameResult.gameOver = false;

        pitchingGetOut.battingResult = new BattingResultOut();
        pitchingGetOut.battingResult.ballCount = 0;
        pitchingGetOut.battingResult.strikeCount = 0;
        pitchingGetOut.battingResult.outCount = 0;
        pitchingGetOut.battingResult.battingResultName = "";

        pitchingGetOut.finished = true;
        pitchingGetOut.battingCourseX = null;
        pitchingGetOut.battingCourseY = null;
        pitchingGetOut.battingBallKindId = null;
        pitchingGetOut.battingBallKindName = null;
        pitchingGetOut.pitchingCourseX = 0;
        pitchingGetOut.pitchingCourseY = 0;
        pitchingGetOut.pitchingBallKindId = 0;
        pitchingGetOut.pitchingBallName = null;
        pitchingGetOut.battingCourseX = null;
        return pitchingGetOut;
    }
}
