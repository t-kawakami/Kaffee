package jp.co.paper.game.controller.io;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class BattingPostIn {
    /** 対戦中の試合ID */
    @Size(max = 10, min = 1)
    public String gameId;

    /** 試合全体の何球目に対してのバッティングか */
    @Size(max = 4)
    public int ballNum;

    /** 打撃を行ったコース（X軸）(見逃す場合はnull) */
    @Max(5)
    @Min(1)
    public Integer courseX;

    /** 打撃を行ったコース（Y軸）(見逃す場合はnull) */
    @Max(5)
    @Min(1)
    public Integer courseY;

    /** 狙い球の球種(打撃コースがnullの場合は無視する) */
    public Integer ballKindId;
}
