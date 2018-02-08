package scalalg.me.libme.apitable.dic

import me.libme.xstream.Sourcer

/**
  * Created by J on 2018/2/8.
  */
trait SourcerFactory {

  @throws(classOf[Exception])
  def factory(conf: Map[String,Object]):Sourcer;

}
