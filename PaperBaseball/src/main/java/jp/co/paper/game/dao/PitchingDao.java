package jp.co.paper.game.dao;

import jp.co.paper.game.domain.Pitching;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ピッチング入力のアクセサ
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class PitchingDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * ピッチング入力を登録する
     * @param pitching
     */
    public void insert(Pitching pitching) {
        sqlTemplate.update("sql/PitchingDao/insert.sql", pitching);
    }

    /**
     * ピッチング入力を取得する
     * @param gameId 試合ID
     * @param ballNum 何球目
     * @return ピッチング入力
     */
    public Pitching getById(String gameId, int ballNum) {
        return sqlTemplate.forObject("sql/PitchingDao/getById.sql", Pitching.class, gameId, ballNum);
    }
}
