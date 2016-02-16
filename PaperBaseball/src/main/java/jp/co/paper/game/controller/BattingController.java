package jp.co.paper.game.controller;

import jp.co.paper.game.controller.converter.BattingConverter;
import jp.co.paper.game.controller.io.BattingPostIn;
import jp.co.paper.game.controller.io.BattingGetOut;
import jp.co.paper.game.domain.BattingResult;
import jp.co.paper.game.framework.ApplicationException;
import jp.co.paper.game.service.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * 打撃の入力受付
 * Created by kawakami_note on 2015/08/12.
 */
@RestController
public class BattingController {
    @Autowired
    protected JudgeService judgeService;

    @Autowired
    protected BattingConverter battingConverter;

    @RequestMapping(value = "/bat", method = RequestMethod.POST)
    public void batPost(@RequestBody BattingPostIn in) {
        judgeService.insertBatting(battingConverter.convertToBatting(in));
    }

    @RequestMapping(value = "/bat", method = RequestMethod.GET)
    public BattingGetOut batGet(@RequestParam String gameId, @RequestParam int ballNum) {
        BattingResult battingResult = judgeService.judgeBattingResult(gameId, ballNum);
        if (battingResult == null) {
            // 投球の入力待ち
            throw new ApplicationException(MessageFormat.format("投球の入力待ち:gameId={0},ballNum={1}", gameId, ballNum));
        }
        return battingConverter.convertToBattingGetOut(battingResult);
    }
}
