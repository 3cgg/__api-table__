package scalalg.me.libme.apitable.dic

import me.libme.xstream.Consumer

/**
  * Created by J on 2018/2/8.
  */
trait ConsumerFactory {

  @throws(classOf[Exception])
  def factory(conf: Map[String,Object]):Consumer

}
