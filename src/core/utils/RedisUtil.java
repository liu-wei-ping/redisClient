package core.utils;

import core.pojo.RedisResultPo;
import redis.clients.jedis.Jedis;

/**
 * @author liu_wp
 * @date 2018年1月18日
 * @see
 */
public class RedisUtil {
	private static int total = 16;
	private static int defaultDb = 0;

	public static RedisResultPo get(String key) {
		Jedis jedis = RedisPoolUtil.getJedis();
		RedisResultPo rp = null;
		if (null != jedis) {
			rp = new RedisResultPo();
			rp.setDbIndex(defaultDb);
			Object result = jedis.get(key);
			if (result == null) {
				for (int i = 0; i < total; i++) {
					if (i == defaultDb) {
						continue;
					}
					jedis.select(i);
					result = jedis.get(key);
					if (result != null) {
						rp.setDbIndex(i);
						rp.setExpireTime(jedis.ttl(key));
						rp.setType(jedis.type(key));
						break;
					}
					if (i == total - 1) {
						rp.setDbIndex(-1);
					}
				}
			} else {
				rp.setDbIndex(defaultDb);
				rp.setExpireTime(jedis.ttl(key));
				rp.setType(jedis.type(key));
			}
			rp.setResult(result);
		}
		return rp;
	}

	public static RedisResultPo get(String key, String namespace) {
		Jedis jedis = RedisPoolUtil.getJedis();
		RedisResultPo rp = null;
		key = getNamespaceKeyByte(key, namespace);
		if (null != jedis) {
			rp = new RedisResultPo();
			jedis.select(defaultDb);
			Object result = jedis.get(key);
			if (result == null) {
				for (int i = 0; i < total; i++) {
					if (i == defaultDb) {
						continue;
					}
					jedis.select(i);
					result = jedis.get(key);
					if (result != null) {
						rp.setDbIndex(i);
						rp.setExpireTime(jedis.ttl(key));
						rp.setType(jedis.type(key));
						break;
					}
					if (i == total - 1) {
						rp.setDbIndex(-1);
					}
				}
			} else {
				rp.setDbIndex(0);
				rp.setExpireTime(jedis.ttl(key));
				rp.setType(jedis.type(key));
			}
			rp.setResult(result);

		}
		return rp;
	}

	public static RedisResultPo getFromDbIndex(String key) {
		Jedis jedis = RedisPoolUtil.getJedis();
		RedisResultPo rp = null;
		if (null != jedis) {
			rp = new RedisResultPo();
			int dbIndex = getDbIndex(key);
			rp.setDbIndex(dbIndex);
			jedis.select(dbIndex);
			Object result = jedis.get(key);
			rp.setResult(result);
			if (result != null) {
				rp.setExpireTime(jedis.ttl(key));
				rp.setType(jedis.type(key));
			}
		}
		return rp;
	}

	public static RedisResultPo getFromDbIndex(String key, String namespace) {
		Jedis jedis = RedisPoolUtil.getJedis();
		RedisResultPo rp = null;

		if (null != jedis) {
			rp = new RedisResultPo();
			int dbIndex = getDbIndex(key);
			rp.setDbIndex(dbIndex);
			jedis.select(dbIndex);
			key = getNamespaceKeyByte(key, namespace);
			Object result = jedis.get(key);
			rp.setResult(result);
			if (result != null) {
				rp.setExpireTime(jedis.ttl(key));
				rp.setType(jedis.type(key));
			}
		}
		return rp;
	}

	private static int getDbIndex(String key) {
		return Math.floorMod(key.hashCode(), total);
	}

	private static String getNamespaceKeyByte(String key, String nameSpace) {
		if (nameSpace == null || key == null) {
			return null;
		}
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(nameSpace.trim());
		strBuilder.append(".");
		strBuilder.append(key.trim());
		return strBuilder.toString();
	}
}
