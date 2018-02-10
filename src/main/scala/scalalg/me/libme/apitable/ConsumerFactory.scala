package scalalg.me.libme.apitable

import me.libme.xstream.Consumer

import scala.collection.mutable

/**
  * Created by J on 2018/2/8.
  */
trait ConsumerFactory {

  @throws(classOf[Exception])
  def factory(conf: mutable.Map[String,Object]):Consumer

}
