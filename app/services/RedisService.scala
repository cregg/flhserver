package services

import com.redis.RedisClient

/**
  * Created by cleclair on 2017-03-05.
  */
object RedisService {

  val redis = new RedisClient("localhost", 6379)

}
