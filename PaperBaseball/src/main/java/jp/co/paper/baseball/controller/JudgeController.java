package jp.co.paper.baseball.controller;

import jp.co.paper.baseball.controller.io.JudgeGetOut;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kawakami_note on 2016/02/16.
 */
@RestController
public class JudgeController {
    @RequestMapping(value = "/judge", method= RequestMethod.GET)
    public JudgeGetOut judgeGet(@RequestParam String gameId, @RequestParam int ballNum) {
        return new JudgeGetOut();
    }
}
