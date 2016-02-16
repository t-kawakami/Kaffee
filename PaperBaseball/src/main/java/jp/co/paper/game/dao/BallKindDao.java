package jp.co.paper.game.dao;

import jp.co.paper.game.domain.BallKind;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 変化球種の設定テーブルにアクセスする
 * Created by kawakami_note on 2015/08/12.
 */
@Component
public class BallKindDao {

    @Autowired
    protected SqlTemplate sqlTemplate;

    /**
     * 変化球種のヒットゾーンを取得する
     * @param ballKindId 変化球ID
     * @return 変化球種のヒットゾーン
     */
    public List<BallKind> getByBallKindId(int ballKindId) {
        return sqlTemplate.forList("sql/BallKindDao/getByBallKindId.sql", BallKind.class, ballKindId);
    }
}
