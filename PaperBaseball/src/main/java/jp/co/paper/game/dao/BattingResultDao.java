package jp.co.paper.game.dao;

import jp.co.paper.game.domain.BattingResult;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * バッティング結果テーブルのアクセサ
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class BattingResultDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * バッティング記録を登録する
     * @param battingResult
     */
    public void insert(BattingResult battingResult) {
        sqlTemplate.update("sql/BattingResultDao/insert.sql", battingResult);
    }

    /**
     * バッティング記録を取得する
     * @param gameId 試合ID
     * @param ballNum 何球目
     * @return バッティング記録
     */
    public BattingResult getById(String gameId, int ballNum) {
        return sqlTemplate.forObject("sql/BattingResultDao/getById.sql", BattingResult.class, gameId, ballNum);
    }
}
