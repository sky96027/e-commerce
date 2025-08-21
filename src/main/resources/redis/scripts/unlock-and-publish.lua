-- Pub/Sub 락 해제 스크립트: DEL + PUBLISH 원자화
-- KEYS[1] = lockKey
-- KEYS[2] = channel  
-- ARGV[1] = token

local v = redis.call('GET', KEYS[1])
if v == ARGV[1] then
    redis.call('DEL', KEYS[1])
    redis.call('PUBLISH', KEYS[2], ARGV[1])
    return 1
else
    return 0
end
