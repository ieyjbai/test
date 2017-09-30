package com.by.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.SortingParams;

public class Test01_basicJedis {
	private Jedis jedis;

	// 创建 Jedis 对象，如果没有密码可以不设置
	@Before
	public void init() {
		JedisShardInfo info = new JedisShardInfo("127.0.0.1", 6379);
		info.setPassword("123");
		jedis = new Jedis(info);
	}

	// 字符串操作
//	@Test
	public void testString() {
		jedis.set("dvd:1", "疯狂动物城");
		String name = jedis.get("dvd:1");
		System.out.println(name);

		jedis.mset("dvd:2", "三体", "dvd:count", "0");
		jedis.incr("dvd:count");
		jedis.incr("dvd:count");
		System.out.println(jedis.get("dvd:2"));
		System.out.println(jedis.get("dvd:count"));

		jedis.del("dvd:1");
		System.out.println(jedis.get("dvd:1"));
	}

	// hash 表操作
//	@Test
	public void testHash() {
		// 1. 存入 hash 键: dvd:1 --> DVD 信息
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", "1");
		map.put("name", "AI");
		map.put("count", "0");
		jedis.hmset("dvd:" + map.get("id"), map);

		// 2. 读取 hash 键: dvd:1
		List<String> list = jedis.hmget("dvd:1", "id", "name", "count");
		System.out.println(list);
		System.out.println(jedis.exists("dvd:1"));
		System.out.println(jedis.hexists("dvd:1", "lendDate"));
		System.out.println(jedis.hlen("dvd:1"));
		System.out.println(jedis.hkeys("dvd:1"));
		System.out.println(jedis.hvals("dvd:1"));

		// 3. 用迭代器返回 hash 键中的 键值对 信息
		Iterator<String> it = jedis.hkeys("dvd:1").iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println(key + " - " + jedis.hget("dvd:1", key));
		}

		// 4. 删除 hash 键
		jedis.hdel("dvd:1", "count");
		System.out.println(jedis.hexists("dvd:1", "count"));
	}

	// 列表操作
//	@Test
	public void testList() {
		// 1. 向列表 dvds 左侧依次添加数据
		jedis.lpush("dvds", "AI", "机械公敌", "二百年", "机械战警");

		// 2. 返回列表全部数据
		System.out.println(jedis.lrange("dvds", 0, -1));

		// 3. 弹出列表最右侧元素
		System.out.println(jedis.rpop("dvds"));
		System.out.println(jedis.lrange("dvds", 0, -1));

		// 4. 想列表左侧插入数据
		jedis.rpush("dvds", "银河系漫游指南");
		System.out.println(jedis.lrange("dvds", 0, -1));

		// 5. 弹出列表左侧数据
		System.out.println(jedis.lpop("dvds"));
		System.out.println(jedis.lrange("dvds", 0, -1));

		// 6. 删除列表
		jedis.del("dvds");
		System.out.println(jedis.lrange("dvds", 0, -1));
	}

	// 无序集合操作
//	@Test
	public void testSet() {
		// 1. 向集合添加数据
		jedis.sadd("stars", "金", "木", "水", "火", "土");

		// 2. 查看集合所有元素
		System.out.println(jedis.smembers("stars"));

		// 3. 删除集合中某个元素
		jedis.srem("stars", "金");

		// 4. 查看集合中是否有某元素
		System.out.println(jedis.sismember("stars", "金"));

		// 5. 查看集合中元素个数
		System.out.println(jedis.scard("stars"));

		// 6. 随机从集合中返回一个元素
		System.out.println(jedis.srandmember("stars"));

		// 7. 删除集合
		jedis.del("stars");
		System.out.println(jedis.smembers("stars"));
	}

	// 有序集合操作
//	@Test
	public void testZSet() {
		// 1. 向有序集合中插入键：stars
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("土", 1d);
		map.put("金", 2d);
		map.put("水", 3d);
		map.put("木", 4d);
		map.put("火", 5d);
		jedis.zadd("stars", map);

		// 2. 列举集合中所有元素
		System.out.println(jedis.zrange("stars", 0, -1));

		// 3. 删除集合中某元素
		jedis.zrem("stars", "金");

		// 4. 查询集合中元素个数
		System.out.println(jedis.zcard("stars"));

		// 5. 反向列举集合中所有元素
		System.out.println(jedis.zrevrange("stars", 0, -1));

		// 6. 删除集合 stars
		jedis.del("stars");
		System.out.println(jedis.get("stars"));
	}

//	@Test
	public void testOrder() {
		jedis.del("rank");
		jedis.rpush("rank", "3", "2", "4", "1", "5");
		System.out.println(jedis.lrange("rank", 0, -1));
		System.out.println(jedis.sort("rank"));
		System.out.println(jedis.sort("rank"));
		System.out.println(jedis.lrange("rank", 0, -1));
	}
}
