package jp.co.paper.game.util;

import jp.co.paper.game.domain.BallKind;
import jp.co.paper.game.domain.Pitching;
import jp.co.paper.game.util.entity.CourseRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kawakami_note on 2015/08/13.
 */
public class CourseRuleUtil {
    private CourseRuleUtil() {
        // インスタンス化を防止する
    }

    /**
     * ヒットコースの一覧を返す
     * @param ballKindList 変化球種のゾーンリスト
     * @param pitching ピッチング入力内容
     * @return ヒットコースの一覧
     */
    public static List<CourseRule> createCourseRule(List<BallKind> ballKindList, Pitching pitching) {
        List<CourseRule> courseRuleList = new ArrayList<>();
        ballKindList.stream().forEach(ballKind -> {
            CourseRule courseRule = new CourseRule();
            courseRule.courseX = pitching.courseX + ballKind.courseXVector;
            courseRule.courseY = pitching.courseY + ballKind.courseYVector;
            courseRule.ruleId = ballKind.ruleId;
            if (validCourse(courseRule.courseX) && validCourse(courseRule.courseY)) {
                courseRuleList.add(courseRule);
            }
        });
        return courseRuleList;
    }

    /**
     * 範囲内に収まっているコースであることを判定する
     * @param course コース
     * @return true : 範囲内である, false : 範囲外である
     */
    private static boolean validCourse(int course) {
        if (1 <= course && course <= 5) {
            return true;
        }
        return false;
    }
}
