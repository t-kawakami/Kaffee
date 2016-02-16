package jp.co.paper.game.controller;

import jp.co.paper.game.controller.converter.PitchingConverter;
import jp.co.paper.game.controller.io.PitchingGetOut;
import jp.co.paper.game.controller.io.PitchingPostIn;
import jp.co.paper.game.domain.BattingResult;
import jp.co.paper.game.framework.ApplicationException;
import jp.co.paper.game.service.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * 投球の入力受付
 * Created by kawakami_note on 2015/08/12.
 */
@RestController
public class PitchingController {
    @Autowired
    protected JudgeService judgeService;

    @Autowired
    protected PitchingConverter pitchingConverter;

    /**
     * 投球内容を入力する。
     * 入力異常がない場合は200OKを返す。
     * @param in
     */
    @RequestMapping(value = "/pitch", method = RequestMethod.POST)
    public void pitchPost(@RequestBody PitchingPostIn in) {
        judgeService.insertPitching(pitchingConverter.convertToPitching(in));
    }

    /**
     * 投球内容と打撃内容とがそろっていれば、打撃結果を返す。
     * 打撃結果がない場合は、
     * @param gameId
     * @param ballNum
     * @return
     */
    @RequestMapping(value = "/pitch", method = RequestMethod.GET)
    public PitchingGetOut pitchGet(@RequestParam String gameId, @RequestParam int ballNum) {
        BattingResult battingResult = judgeService.judgeBattingResult(gameId, ballNum);
        if (battingResult == null) {
             // 打撃の入力待ち
            throw new ApplicationException(MessageFormat.format("打撃の入力待ち:gameId={0},ballNum={1}", gameId, ballNum));
        }
        return pitchingConverter.convertToPitchingGetOut(battingResult);
    }
}
