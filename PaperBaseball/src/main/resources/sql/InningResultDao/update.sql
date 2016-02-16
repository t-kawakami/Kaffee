UPDATE
    INNING_RESULT
SET
    SCORE = :score,
    HIT_NUM = :hitNum
WHERE
    GAME_ID = :gameId
    AND INNING = :inning
    AND TOP = :top