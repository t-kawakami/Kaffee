package jp.co.paper.game.controller;

import jp.co.paper.game.Application;
import jp.co.paper.game.controller.io.BattingPostIn;
import jp.co.paper.game.framework.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by kawakami_note on 2015/08/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class BattingControllerTest {

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
    protected BattingController battingController;

    @Test
    public void test_batGet_no_pitch() {
        BattingPostIn postIn = createBattingPostIn("testgame", 1, STRAIGHT, 3, 3);
        battingController.batPost(postIn);

        try {
            battingController.batGet("testgame", 1);
        } catch (ApplicationException aex) {
            assertThat(aex.getMessage(), is("投球の入力待ち:gameId=testgame,ballNum=1"));
        }
    }

    /**
     * 見逃しバッティング入力
     * @return
     */
    private BattingPostIn createBattingPostInMissed(String gameId, int ballNum){
        return createBattingPostIn(gameId, ballNum, null, null, null);
    }

    private BattingPostIn createBattingPostIn(String gameId, int ballNum, Integer ballKindId, Integer courseX, Integer courseY) {
        BattingPostIn in = new BattingPostIn();
        in.gameId = gameId;
        in.ballNum = ballNum;
        in.ballKindId = ballKindId;
        in.courseX = courseX;
        in.courseY = courseY;
        return in;
    }

}
