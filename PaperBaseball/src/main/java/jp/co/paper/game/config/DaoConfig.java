package jp.co.paper.game.config;

import ninja.cero.sqltemplate.core.SqlTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by kawakami_note on 2015/08/12.
 */
@Configuration
public class DaoConfig {

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * DBコネクションを作成する
     * @param jdbcTemplate
     * @param namedParameterJdbcTemplate
     * @return
     */
    @Bean
    SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
