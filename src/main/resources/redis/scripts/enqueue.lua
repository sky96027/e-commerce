-- ENQUEUE: Redis TIME으로 μs 타임스탬프를 만들고,
-- 쿠폰별 시퀀스를 더해 유니크/단조 score 생성 후 ZADD
-- KEYS[1] = ZSET key
-- KEYS[2] = SEQ key  
-- ARGV[1] = member(reservationId:userId)

local t = redis.call('TIME')                           -- {sec, usec}
local ts_ms = t[1] * 1000 + math.floor(t[2] / 1000)    -- ms
local seq = redis.call('INCR', KEYS[2])
local score = ts_ms * 1000 + (seq % 1000)
redis.call('ZADD', KEYS[1], score, ARGV[1])
return score
