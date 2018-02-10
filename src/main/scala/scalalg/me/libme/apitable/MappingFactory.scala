package scalalg.me.libme.apitable

import me.libme.kernel._c.util.CliParams
import me.libme.xstream.EntryTupe.Entry
import me.libme.xstream.excel.ExcelCompositer
import me.libme.xstream.{Consumer, ConsumerMeta, Tupe}

import scala.collection.JavaConversions._
import scala.collection.{JavaConversions, mutable}

/**
  * Created by J on 2018/2/8.
  */
object MappingFactory extends ConsumerFactory{



  override def factory(conf: mutable.Map[String, Object]): Consumer = {


    val cliParams:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))


    val name:String=cliParams.getString("name")
//    val mappings:Map[String,String]=classOf[Map[String,String]].cast(cliParams.getObject("mapping"))


    val consumerMeta=new ConsumerMeta(name)

    return new MappingConsumer(conf,consumerMeta)



  }




}
class MappingFactory{}

class MappingConsumer(conf:mutable.Map[String,AnyRef],consumerMeta: ConsumerMeta) extends ExcelCompositer(consumerMeta){

  val mappings:mutable.Map[String,String]=classOf[java.util.Map[String,String]].cast(conf.get("mapping").get)

  override def doPrepare(tupe: Tupe[_]): Unit = {

  }


  def mapping(tupe: Tupe[_],entry:Entry):Unit={

    mappings.get(entry.getKey) match {
      case Some(value)=>produce(new Entry(value,entry.getValue))
      case None =>

    }



  }

  override def doConsume(tupe: Tupe[_]): Unit = {

    val iterator:Iterator[_]=JavaConversions.asScalaIterator(tupe.iterator());
    while(iterator.hasNext){
      mapping(tupe,classOf[Entry].cast(iterator.next()))
    }

  }


}


























