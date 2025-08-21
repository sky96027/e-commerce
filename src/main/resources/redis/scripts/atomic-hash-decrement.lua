-- 해시 기반 원자적 감소
-- KEYS[1] = stock:prod:{productId}
-- ARGV[1] = optionId
-- ARGV[2] = qty
-- cur < req 이면 -1, 성공 시 감소 후 남은 수량 반환

local cur = tonumber(redis.call('HGET', KEYS[1], ARGV[1]) or '0')
local req = tonumber(ARGV[2])
if cur < req then 
    return -1 
end
return redis.call('HINCRBY', KEYS[1], ARGV[1], -req)
