INSERT INTO BATTING_COUNT (
    GAME_ID,
    BALL_NUM,
    INNING,
    TOP,
    STRIKE_COUNT,
    BALL_COUNT,
    OUT_COUNT
)
VALUES (
   :gameId,
   :ballNum,
   :inning,
   :top,
   :strikeCount,
   :ballCount,
   :outCount
)