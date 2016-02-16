package jp.co.paper.game.dao;

import jp.co.paper.game.domain.BattingCount;
import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kawakami_note on 2015/08/13.
 */
@Component
public class BattingCountDao {
    @Autowired
    protected SqlTemplate sqlTemplate;

    public void insert(BattingCount battingCount) {
        sqlTemplate.update("sql/BattingCountDao/insert.sql", battingCount);
    }

    public BattingCount getById(String gameId, int ballNum) {
        return sqlTemplate.forObject("sql/BattingCountDao/getById.sql", BattingCount.class, gameId, ballNum);
    }
}
