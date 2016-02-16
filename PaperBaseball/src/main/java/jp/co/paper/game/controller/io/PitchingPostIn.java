package jp.co.paper.game.controller.io;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by kawakami_note on 2015/08/12.
 */
public class PitchingPostIn {
    /** 対戦中の試合ID */
    @Size(max = 10)
    @NotNull
    public String gameId;

    /** 試合中の何球目のピッチングか */
    @Size(max = 4)
    public int ballNum;

    /** 投球コース(X軸) */
    @Max(5)
    @Min(1)
    public int courseX;

    /** 投球コース(Y軸) */
    @Max(5)
    @Min(1)
    public int courseY;

    /** 球種ID */
    public int ballKindId;
}
