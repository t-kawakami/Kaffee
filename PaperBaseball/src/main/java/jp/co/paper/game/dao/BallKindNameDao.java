package jp.co.paper.game.dao;

import jp.co.paper.game.domain.BallKindName;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 変化球種名のテーブルにアクセスする
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class BallKindNameDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * 変化球種表示名を取得する
     * @param ballKindId 変化球種ID
     * @return 変化球表示名
     */
    public BallKindName getById(int ballKindId) {
        return sqlTemplate.forObject("sql/BallKindNameDao/getById.sql", BallKindName.class, ballKindId);
    }
}
