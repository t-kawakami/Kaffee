package jp.co.paper.game.dao;

import jp.co.paper.game.domain.Batting;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * バッティング内容の入力テーブルのアクセサ
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class BattingDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * バッティング入力を記録する
     * @param batting バッティング入力
     */
    public void insert(Batting batting) {
        sqlTemplate.update("sql/BattingDao/insert.sql", batting);
    }

    /**
     * バッティング入力を取得する
     * @param gameId 試合ID
     * @param ballNum 何球目
     * @return バッティング入力
     */
    public Batting getById(String gameId, int ballNum){
        return sqlTemplate.forObject("sql/BattingDao/getById.sql", Batting.class, gameId, ballNum);
    }
}
