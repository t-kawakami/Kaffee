package jp.co.paper.baseball.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kawakami_note on 2016/02/16.
 */
@RestController
public class BattingController {
    @RequestMapping(value = "/batting", method = RequestMethod.GET)
    public void batting(@RequestParam String gameId, @RequestParam int ballNum, @RequestParam int x, @RequestParam int y, @RequestParam int ballKind) {

    }
}