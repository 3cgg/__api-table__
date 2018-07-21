package scalalg.me.libme.apitable

import java.util.Date

import me.libme.kernel._c.pubsub.{Produce, Publisher, QueuePools, Topic}
import me.libme.kernel._c.util.{CliParams, JDateUtils, JUniqueUtils}
import me.libme.xstream.EntryTupe.Entry
import me.libme.xstream.excel.ExcelCompositer
import me.libme.xstream.{Consumer, ConsumerMeta, Tupe}

import scala.collection.{JavaConversions, mutable}

/**
  * Created by J on 2018/2/8.
  */
object DMLFactory extends ConsumerFactory{



  override def factory(conf: mutable.Map[String, Object]): Consumer = {


    val cliParams:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))


    val name:String=cliParams.getString("name")
//    val mappings:Map[String,String]=classOf[Map[String,String]].cast(cliParams.getObject("mapping"))
    val consumerMeta=new ConsumerMeta(name)

    return new DMLConsumer(conf,consumerMeta)
  }




}

class DMLFactory{}

class DMLConsumer(conf:mutable.Map[String,AnyRef],consumerMeta: ConsumerMeta) extends ExcelCompositer(consumerMeta){

  import scala.collection.JavaConversions._

  val dmlInsert:String=classOf[String].cast(conf.get("dmlInsert").get)
  val overrideValues:mutable.Map[String,AnyRef]=classOf[java.util.Map[String,Object]].cast(conf.get("override").get)
  val topic:String=classOf[String].cast(conf.get("topic").get)
  val publisher:Publisher=new Publisher(new Topic(topic),QueuePools.defaultPool())
  val producer:Produce=publisher.produce();

  def uuid():String={JUniqueUtils.unique()}

  def sysdate():String={JDateUtils.formatWithSeconds(new Date())}

  override def doPrepare(tupe: Tupe[_]): Unit = {

  }


  override def doConsume(tupe: Tupe[_]): Unit = {

    val iterator:Iterator[_]=JavaConversions.asScalaIterator(tupe.iterator())
    var tempDML=dmlInsert
    while(iterator.hasNext){
      val entry=classOf[Entry].cast(iterator.next())
      val value:String=String.valueOf(entry.getValue)
      if(!overrideValues.contains(entry.getKey)){
        tempDML=tempDML.replaceAll("([$][{]\\s*"+entry.getKey+"\\s*[}])",value)
      }

    }

    overrideValues.foreach{case (key:String,ovalue:AnyRef)=>{
      var value:String=null
      value=ovalue match {
        case "uuid()"=> uuid()
        case "sysdate()"=>sysdate()
        case _=>String.valueOf(ovalue)
      }
      tempDML=tempDML.replaceAll("([$][{]\\s*"+key+"\\s*[}])",value)
    }}


    producer.produce(tempDML)

  }


}


























