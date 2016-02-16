package jp.co.paper.game.dao;

import jp.co.paper.game.domain.BaseCount;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kawakami_note on 2015/08/13.
 */
@Component
public class BaseCountDao {
    @Autowired
    protected SqlTemplate sqlTemplate;

    public void insert(BaseCount baseCount) {
        sqlTemplate.update("sql/BaseCountDao/insert.sql", baseCount);
    }

    public BaseCount getById(String gameId, int ballNum) {
        return sqlTemplate.forObject("sql/BaseCountDao/getById.sql", BaseCount.class, gameId, ballNum);
    }
}
