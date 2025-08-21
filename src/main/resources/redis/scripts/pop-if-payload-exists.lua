-- 안전 pop: payload가 있으면 pop(OK), 없으면 tombstone pop(MISSING)
-- 반환값: "EMPTY" | "OK:<member>" | "MISSING:<member>"
-- KEYS[1] = ZSET queue key
-- ARGV[1] = payload key prefix (e.g. 'coupon:issue:cmd:')

local q = KEYS[1]
local prefix = ARGV[1]

local arr = redis.call('ZRANGE', q, 0, 0)
if (arr == nil or #arr == 0) then
    return "EMPTY"
end

local m = arr[1]                            -- "reservationId:userId"
local rid = string.match(m, "([^:]+):")     -- 첫 ':' 전까지

if (rid == nil) then
    redis.call('ZREM', q, m)
    return "MISSING:" .. m
end

local cmdKey = prefix .. rid
if (redis.call('EXISTS', cmdKey) == 1) then
    redis.call('ZREM', q, m)
    return "OK:" .. m
else
    redis.call('ZREM', q, m)
    return "MISSING:" .. m
end
