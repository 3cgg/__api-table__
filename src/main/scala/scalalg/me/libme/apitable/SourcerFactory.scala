package scalalg.me.libme.apitable

import me.libme.xstream.Sourcer

import scala.collection.mutable

/**
  * Created by J on 2018/2/8.
  */
trait SourcerFactory {

  @throws(classOf[Exception])
  def factory(conf: mutable.Map[String,Object]):Sourcer;

}
