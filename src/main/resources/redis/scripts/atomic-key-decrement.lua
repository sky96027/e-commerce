-- 단일 키 기반 원자적 감소
-- KEYS[1] = stock:{optionId}
-- ARGV[1] = qty
-- cur < req 이면 -1, 성공 시 감소 후 남은 수량 반환

local cur = tonumber(redis.call('GET', KEYS[1]) or '0')
local req = tonumber(ARGV[1])
if cur < req then 
    return -1 
end
return redis.call('DECRBY', KEYS[1], req)
