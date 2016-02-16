package jp.co.paper.game.dao;

import jp.co.paper.game.domain.InningResult;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 試合結果記録テーブルのアクセサ
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class InningResultDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * 試合結果を登録する
     * @param gameResult
     */
    public void insert(InningResult gameResult) {
        sqlTemplate.update("sql/InningResultDao/insert.sql", gameResult);
    }

    /**
     * 試合結果を更新する
     * @param gameResult
     */
    public void update(InningResult gameResult) {
        sqlTemplate.update("sql/InningResultDao/update.sql", gameResult);
    }

    /**
     * 試合結果を取得する
     * @param gameId 試合ID
     * @return 試合結果
     */
    public List<InningResult> getByGameId(String gameId) {
        return sqlTemplate.forList("sql/InningResultDao/getByGameId.sql", InningResult.class, gameId);
    }

    /**
     * 試合結果をイニング(表、裏は区別)ごとに取得する
     * @param gameId
     * @param inning
     * @param top
     * @return
     */
    public InningResult getById(String gameId, int inning, boolean top) {
        return sqlTemplate.forObject("sql/InningResultDao/getById.sql", InningResult.class, gameId, inning, top);
    }
}
