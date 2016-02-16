INSERT INTO BATTING_RESULT (
    GAME_ID,
    BALL_NUM,
    STRIKE,
    BALL,
    FOUL,
    BATTING_RESULT_ID
)
VALUES (
   :gameId,
   :ballNum,
   :strike,
   :ball,
   :foul,
   :battingResultId
)