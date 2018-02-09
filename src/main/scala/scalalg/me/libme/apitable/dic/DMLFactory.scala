package scalalg.me.libme.apitable.dic

import me.libme.kernel._c.pubsub.{Produce, Publisher, QueuePools, Topic}
import me.libme.kernel._c.util.CliParams
import me.libme.xstream.EntryTupe.Entry
import me.libme.xstream.excel.ExcelCompositer
import me.libme.xstream.{Consumer, ConsumerMeta, Tupe}

import scala.collection.JavaConversions

/**
  * Created by J on 2018/2/8.
  */
object DMLFactory extends ConsumerFactory{



  override def factory(conf: Map[String, Object]): Consumer = {


    val cliParams:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))


    val name:String=cliParams.getString("name")
//    val mappings:Map[String,String]=classOf[Map[String,String]].cast(cliParams.getObject("mapping"))
    val consumerMeta=new ConsumerMeta(name)

    return new DMLConsumer(conf,consumerMeta)
  }




}

class DMLFactory{}

class DMLConsumer(conf:Map[String,AnyRef],consumerMeta: ConsumerMeta) extends ExcelCompositer(consumerMeta){


  val dmlInsert:String=classOf[String].cast(conf.get("dmlInsert"))
  val topic:String=classOf[String].cast(conf.get("topic"))
  val publisher:Publisher=new Publisher(new Topic(topic),QueuePools.defaultPool())
  val producer:Produce=publisher.produce();

  override def doPrepare(tupe: Tupe[_]): Unit = {

  }


  override def doConsume(tupe: Tupe[_]): Unit = {

    val iterator:Iterator[_]=JavaConversions.asScalaIterator(tupe.iterator())
    var tempDML=dmlInsert
    while(iterator.hasNext){
      val entry=classOf[Entry].cast(iterator.next())
      tempDML=tempDML.replaceAll("[$]{[ ]*"+entry.getKey+"[ ]*}",String.valueOf(entry.getValue))
    }

    producer.produce(tempDML)

  }


}


























